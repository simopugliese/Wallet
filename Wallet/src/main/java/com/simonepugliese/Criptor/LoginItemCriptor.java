package com.simonepugliese.Criptor;

import com.simonepugliese.Item.Item;
import com.simonepugliese.Item.LoginItem;

public class LoginItemCriptor extends Criptor{
    @Override
    public Item cripta(Item item) {
        LoginItem loginItem = (LoginItem) item;
        String username = CryptoUtils.encrypt(loginItem.getUsername());
        String password = CryptoUtils.encrypt(loginItem.getPassword());
        String urlSito = CryptoUtils.encrypt(loginItem.getUrlSito());
        if (username != null & password != null & urlSito != null) {
            loginItem.setUsername(username);
            loginItem.setPassword(password);
            loginItem.setUrlSito(urlSito);
            return loginItem;
        }
        return null;
    }

    @Override
    public Item decripta(Item item) {
        LoginItem loginItem = (LoginItem) item;
        String username = CryptoUtils.decrypt(loginItem.getUsername());
        String password = CryptoUtils.decrypt(loginItem.getPassword());
        String urlSito = CryptoUtils.decrypt(loginItem.getUrlSito());
        if (username != null && password != null && urlSito != null) {
            loginItem.setUsername(username);
            loginItem.setPassword(password);
            loginItem.setUrlSito(urlSito);
            return loginItem;
        }
        return null;
    }
}
