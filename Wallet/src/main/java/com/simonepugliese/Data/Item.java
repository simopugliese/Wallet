package com.simonepugliese.Data;

public abstract class Item {
    private int id;
    private ItemType itemType;
    private String note;

    protected Item(int id, ItemType itemType, String note) {
        this.id = id;
        this.itemType = itemType;
        this.note = note;
    }

    public int getId() {
        return id;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
