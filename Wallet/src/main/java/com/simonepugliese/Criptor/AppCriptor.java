package com.simonepugliese.Criptor;

import com.simonepugliese.Item.AppItem;
import com.simonepugliese.Item.Item;

public class AppCriptor extends Criptor {
    @Override
    public Item cripta(Item item) {
        AppItem appItem = (AppItem) item;
        String username = CryptoUtils.encrypt(appItem.getUsername());
        String password = CryptoUtils.encrypt(appItem.getPassword());
        String pin = CryptoUtils.encrypt(appItem.getPin());
        String note = CryptoUtils.encrypt(appItem.getNote());
        if (username != null && password != null && pin != null && note != null) {
            appItem.setUsername(username);
            appItem.setPassword(password);
            appItem.setPin(pin);
            appItem.setNote(note);
            return appItem;
        }
        return null;
    }

    @Override
    public Item decripta(Item item) {
        AppItem appItem = (AppItem) item;
        String username = CryptoUtils.decrypt(appItem.getUsername());
        String password = CryptoUtils.decrypt(appItem.getPassword());
        String pin = CryptoUtils.decrypt(appItem.getPin());
        String note = CryptoUtils.decrypt(appItem.getNote());
        if (username != null && password != null && pin != null && note != null) {
            appItem.setUsername(username);
            appItem.setPassword(password);
            appItem.setPin(pin);
            appItem.setNote(note);
            return appItem;
        }
        return null;
    }
}
