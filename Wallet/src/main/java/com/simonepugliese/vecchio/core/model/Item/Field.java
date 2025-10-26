package com.simonepugliese.vecchio.core.model.Item;

class Field {
    private FieldType fieldType;
    private Object field;
    private Object id; //id of the item

    protected Field(FieldType fieldType, Object field, Object id) {
        this.fieldType = fieldType;
        this.field = field;
        this.id = id;
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

    public Object getId() {
        return id;
    }

    public void setId(Object id) {
        this.id = id;
    }
}
