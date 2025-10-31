package com.simonepugliese.Criptor;

import com.simonepugliese.Item.LoginItem;

public class LoginItemCriptor extends Criptor {
    LoginItem loginItem;

    public LoginItemCriptor(LoginItem loginItem) {
        this.loginItem = loginItem;
    }

    public LoginItem getLoginItem() {
        return loginItem;
    }

    public void setLoginItem(LoginItem loginItem) {
        this.loginItem = loginItem;
    }

    @Override
    public void cripta() {

    }

    @Override
    public void decripta() {

    }
}
