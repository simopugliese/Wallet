package com.simonepugliese.Saver;

import com.simonepugliese.Item.Item;

import java.util.List;

public abstract class Saver {
    public abstract void salva(Item item);
    public abstract List<Item> carica();
}
