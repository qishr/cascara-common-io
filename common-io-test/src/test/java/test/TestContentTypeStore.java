package test;

import java.util.List;

import io.github.qishr.cascara.common.annotation.Priority;
import io.github.qishr.cascara.common.diagnostic.UnimplementedMethodException;
import io.github.qishr.cascara.common.util.ContentType;
import io.github.qishr.cascara.common.util.ContentTypeResolver;

@Priority(Priority.LOWEST)
public class TestContentTypeStore implements ContentTypeResolver {

    @Override
    public ContentType resolve(String type) {
        throw new UnimplementedMethodException();
    }

    @Override
    public List<ContentType> getAll() {
        throw new UnimplementedMethodException();
    }

    @Override
    public void add(ContentType contentType) {
        throw new UnimplementedMethodException();
    }

    @Override
    public void addAll(List<? extends ContentType> contentTypes) {
        throw new UnimplementedMethodException();
    }

}
