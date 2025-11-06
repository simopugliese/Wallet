package com.simonepugliese.Item;

public abstract class Item {
    private final String description;
    private final ItemType itemType;

    public Item(String description, ItemType itemType) {
        this.description = description;
        this.itemType = itemType;
    }

    public String getDescription() {
        return description;
    }

    public ItemType getItemType() {
        return itemType;
    }
}
