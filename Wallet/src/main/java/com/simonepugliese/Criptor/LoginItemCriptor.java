package com.simonepugliese.Criptor;

import com.simonepugliese.Item.Item;
import com.simonepugliese.Item.LoginItem;

public class LoginItemCriptor extends Criptor{
    @Override
    public Item cripta(Item item) {
        LoginItem loginItem = (LoginItem) item;
        return loginItem;
    }

    @Override
    public Item decripta(Item item) {
        LoginItem loginItem = (LoginItem) item;
        return loginItem;
    }
}
