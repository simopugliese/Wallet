package com.simonepugliese.core.model;

import java.util.List;

public class Item {
    private Object id;
    private List<Field> fields;

    public Item(Object id, List<Field> fields) {
        this.id = id;
        this.fields = fields;
    }

    public Object getId() {
        return id;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
