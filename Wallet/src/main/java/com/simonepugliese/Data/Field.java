package com.simonepugliese.Data;

class Field {
    private int id;
    private Object field;

    protected Field(int id, Object field) {
        this.id = id;
        this.field = field;
    }

    protected int getId() {
        return id;
    }

    protected Object getField() {
        return field;
    }

    protected void setField(Object newField) {
        this.field = newField;
    }
}
