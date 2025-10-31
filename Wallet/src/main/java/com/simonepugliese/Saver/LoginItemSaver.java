package com.simonepugliese.Saver;

import com.simonepugliese.Item.LoginItem;

public class LoginItemSaver extends Saver {
    private LoginItem loginItem;

    public LoginItemSaver(LoginItem loginItem) {
        this.loginItem = loginItem;
    }

    public LoginItem getLoginItem() {
        return loginItem;
    }

    public void setLoginItem(LoginItem loginItem) {
        this.loginItem = loginItem;
    }

    @Override
    public void salva() {

    }

    @Override
    public void carica() {

    }
}
