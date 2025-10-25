package com.simonepugliese.core.model;

import java.util.HashMap;

public class Item {
    private ItemType itemType;
    private HashMap<String, FieldType> fields;

    public Item(ItemType itemType, HashMap<String, FieldType> fields) {
        this.itemType = itemType;
        this.fields = fields;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public HashMap<String, FieldType> getFields() {
        return fields;
    }

    public void setFields(HashMap<String, FieldType> fields) {
        this.fields = fields;
    }
}
