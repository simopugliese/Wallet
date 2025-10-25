package com.simonepugliese.core.model;

public class Field {
    private FieldType fieldType;
    private Object field;

    public Field(FieldType fieldType, Object field) {
        this.fieldType = fieldType;
        this.field = field;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public Object getField() {
        return field;
    }

    public void setField(Object field) {
        this.field = field;
    }
}
