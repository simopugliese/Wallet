package com.simonepugliese.vecchio.core.model.Item;

import java.util.ArrayList;
import java.util.List;

abstract class Item {
    private Object id;
    private ItemType itemType;
    private List<Field> fields;

    protected Item(Object id, ItemType itemType) {
        this.id = id;
        this.itemType = itemType;
        this.fields = new ArrayList<>();
    }

    public Object getId() {
        return id;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
}
