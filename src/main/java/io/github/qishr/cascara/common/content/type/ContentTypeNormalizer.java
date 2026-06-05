package io.github.qishr.cascara.common.content.type;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.qishr.cascara.common.diagnostic.NoOpReporter;
import io.github.qishr.cascara.common.diagnostic.Reporter;
import io.github.qishr.cascara.common.data.Table;
import io.github.qishr.cascara.common.util.ContentType;

public final class ContentTypeNormalizer {

    private Reporter reporter = new NoOpReporter();

    public void setReporter(Reporter reporter) { this.reporter = reporter; }

    public List<MergedContentType> normalize(List<ContentType> all) {
        List<ContentType> concreteTypes = all.stream()
            .filter(this::hasConcreteMimeType)
            .collect(Collectors.toList());

        // Normalize MIME types and suffixes
        concreteTypes.forEach(this::normalizeContentType);

        // Build graph edges
        Map<ContentType, Set<ContentType>> adjacency = buildAdjacency(concreteTypes);

        // Find connected components
        List<Set<ContentType>> groups = findConnectedComponents(adjacency);
        reporter.debug("Found " + groups.size() + " connected component groups");

        // Convert each group into a canonical content type
        List<MergedContentType> result = new ArrayList<>();
        for (Set<ContentType> group : groups) {
            result.add(canonicalizeGroup(group));
        }
        debugOutputTypes(result);
        return result;
    }

    private boolean hasConcreteMimeType(ContentType ct) {
        return ct.getMimeTypes().stream()
                 .anyMatch(m -> !m.endsWith("/*") && !m.equals("*/*"));
    }

    private void normalizeContentType(ContentType ct) {
        ct.setMimeTypes(
                ct.getMimeTypes().stream()
                        .map(this::normalizeMime)
                        .distinct()
                        .collect(Collectors.toList())
        );

        ct.setSuffixes(
                ct.getSuffixes().stream()
                        .map(s -> s.trim().toLowerCase(Locale.ROOT))
                        .distinct()
                        .collect(Collectors.toList())
        );
    }

    private Map<ContentType, Set<ContentType>> buildAdjacency(List<ContentType> all) {
        Map<ContentType, Set<ContentType>> graph = new HashMap<>();

        for (ContentType a : all) {
            graph.putIfAbsent(a, new HashSet<>());
            for (ContentType b : all) {
                reporter.debug("Checking: " + a.getName() + " vs " + b.getName());
                reporter.debug("  MIME A: " + a.getMimeTypes());
                reporter.debug("  MIME B: " + b.getMimeTypes());
                reporter.debug("  Intersect? " + mimeListsIntersect(a.getMimeTypes(), b.getMimeTypes()));

                if (a == b) continue;

                if (mimeListsIntersect(a.getMimeTypes(), b.getMimeTypes())) {
                    graph.get(a).add(b);
                }
            }
        }
        return graph;
    }

    private boolean mimeIntersect(String a, String b) {
        if (a.equals(b)) return true;

        String[] pa = a.split("/");
        String[] pb = b.split("/");
        if (pa.length != 2 || pb.length != 2) return false;

        String typeA = pa[0];
        String subA  = pa[1];
        String typeB = pb[0];
        String subB  = pb[1];

        // Ignore wildcard MIME types entirely — they are capabilities, not content types
        if (a.endsWith("/*") || b.endsWith("/*")) return false;

        if (subA.equals("*") || subB.equals("*")) return false;

        // Wildcard type
        if (typeA.equals("*") || typeB.equals("*")) return true;

        // Wildcard subtype
        if (subA.equals("*") || subB.equals("*")) {
            return typeA.equals(typeB);
        }

        // If either subtype ends with +json, treat it as JSON
        if (subA.endsWith("+json") && (subB.equals("json") || subB.endsWith("+json"))) {
            return true;
        }
        if (subB.endsWith("+json") && (subA.equals("json") || subA.endsWith("+json"))) {
            return true;
        }

        // Exact subtype match
        return subA.equals(subB);
    }

    private boolean mimeListsIntersect(List<String> a, List<String> b) {
        for (String x : a) {
            for (String y : b) {
                if (mimeIntersect(x, y)) return true;
            }
        }
        return false;
    }

    private List<Set<ContentType>> findConnectedComponents(
        Map<ContentType, Set<ContentType>> graph
    ) {
        Set<ContentType> visited = new HashSet<>();
        List<Set<ContentType>> components = new ArrayList<>();

        for (ContentType ct : graph.keySet()) {
            if (!visited.contains(ct)) {
                Set<ContentType> comp = new HashSet<>();
                dfs(ct, graph, visited, comp);
                components.add(comp);
            }
        }

        return components;
    }

    private void dfs(ContentType ct,
                    Map<ContentType, Set<ContentType>> graph,
                    Set<ContentType> visited,
                    Set<ContentType> comp) {

        visited.add(ct);
        comp.add(ct);

        for (ContentType next : graph.get(ct)) {
            if (!visited.contains(next)) {
                dfs(next, graph, visited, comp);
            }
        }
    }


    private MergedContentType canonicalizeGroup(Set<ContentType> group) {

        // Merge MIME types and suffixes
        Set<String> allMime = group.stream()
                .flatMap(ct -> ct.getMimeTypes().stream())
                .filter(m -> !m.endsWith("/*")) // ignore wildcard MIME types
                .collect(Collectors.toSet());

        if (allMime.isEmpty()) {
            allMime.add("text/plain");
        }

        Set<String> allSuffixes = group.stream()
                .flatMap(ct -> ct.getSuffixes().stream())
                .collect(Collectors.toSet());

        // Pick canonical MIME type
        String canonicalMime = allMime.stream()
                .min(Comparator.comparingInt(this::complexityScore))
                .orElse("application/octet-stream");

        // Canonical ID = canonical MIME
        String canonicalId = canonicalMime;

        // Canonical name = first non-empty name
        String canonicalName = group.stream()
                .map(ContentType::getName)
                .filter(n -> n != null && !n.isBlank())
                .findFirst()
                .orElse(canonicalMime);

        return new MergedContentType(
                canonicalId,
                canonicalName,
                allSuffixes,
                allMime,
                new ArrayList<>(group)
        );
    }

    //
    //
    //

    public String normalizeMime(String mime) {
        return mime.trim().toLowerCase(Locale.ROOT);
    }

    public boolean isTextual(String mime) {
        return mime.startsWith("text/");
    }

    public int complexityScore(String mime) {
        // Lower score = more canonical
        // Prefer text/*, then shorter subtype, then fewer structured suffixes
        String[] parts = mime.split("/");
        if (parts.length != 2) return Integer.MAX_VALUE;

        String type = parts[0];
        String subtype = parts[1];

        int score = 0;

        if (!type.equals("text")) score += 1000;
        score += subtype.length();
        score += subtype.contains("+") ? 50 : 0;

        return score;
    }

    private void debugOutputTypes(List<MergedContentType> types) {
        Table table = new Table();
        table.addColumn("Canonical ID");
        table.addColumn("Name");
        for (MergedContentType type : types) {
            table.addRow(type.getCanonicalId(), type.getName());
        }
        try (StringWriter writer = new StringWriter()){
            table.render(writer);
            reporter.debug("Canonical Content Types\n" + writer.toString());
        } catch (IOException e) {
            reporter.error(null, "Failed to write debug output: " + e.getMessage());
        }
    }
}
