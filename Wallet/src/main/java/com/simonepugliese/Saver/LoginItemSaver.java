package com.simonepugliese.Saver;

import com.simonepugliese.Item.Item;
import com.simonepugliese.Item.LoginItem;

import java.util.List;

public class LoginItemSaver extends Saver {
    @Override
    public void salva(Item item) {
        LoginItem loginItem = (LoginItem) item;
    }

    @Override
    public List<Item> carica() {
        return List.of();
    }
}
