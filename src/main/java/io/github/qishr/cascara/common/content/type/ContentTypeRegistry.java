package io.github.qishr.cascara.common.content.type;

import java.util.ArrayList;
import java.util.List;

import io.github.qishr.cascara.common.lang.annotation.DataField;
import io.github.qishr.cascara.common.lang.annotation.Serializable;
import io.github.qishr.cascara.common.util.ContentType;

@Serializable
public class ContentTypeRegistry {

    @DataField
    public List<ContentType> records = new ArrayList<>();

    public ContentTypeRegistry() {}

    public List<ContentType> getRecords() {
        return records;
    }

    public void setRecords(List<ContentType> records) {
        this.records = records;
    }
}
