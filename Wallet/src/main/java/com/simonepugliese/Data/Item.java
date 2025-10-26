package com.simonepugliese.Data;

import java.util.HashMap;
import java.util.Map;

public abstract class Item {
    private int id;
    private ItemType itemType;
    private Map<String, Field> data;

    protected Item(int id, ItemType itemType) {
        this.id = id;
        this.itemType = itemType;
        this.data = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public Map<String, Field> getData() {
        return data;
    }
}

