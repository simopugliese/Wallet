package com.simonepugliese.Manager;

import com.simonepugliese.Criptor.Criptor;
import com.simonepugliese.Item.Item;
import com.simonepugliese.Saver.Saver;

public abstract class Manager {
    private Item item;
    private Saver saver;
    private Criptor criptor;

    public Manager(Item item, Saver saver, Criptor criptor) {
        this.item = item;
        this.saver = saver;
        this.criptor = criptor;
    }

    public Item getItem() {
        return item;
    }

    public Saver getSaver() {
        return saver;
    }

    public Criptor getCriptor() {
        return criptor;
    }

    public abstract void criptaPoiSalva();
    public abstract void decriptaPoiMostra();
}
