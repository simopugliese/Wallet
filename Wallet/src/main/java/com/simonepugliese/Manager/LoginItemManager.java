package com.simonepugliese.Manager;

import com.simonepugliese.Criptor.Criptor;
import com.simonepugliese.Criptor.LoginItemCriptor;
import com.simonepugliese.Item.LoginItem;
import com.simonepugliese.Saver.LoginItemSaver;
import com.simonepugliese.Saver.Saver;

public class LoginItemManager extends Manager {
    public LoginItemManager(LoginItem loginItem) {
        super(loginItem, new LoginItemSaver(loginItem), new LoginItemCriptor(loginItem));
    }

    @Override
    public void criptaPoiSalva() {
        LoginItemCriptor loginItemCriptor = (LoginItemCriptor) super.getCriptor();
        loginItemCriptor.cripta();
        LoginItem loginItemCriptato = loginItemCriptor.getLoginItem();

        LoginItemSaver loginItemSaver = (LoginItemSaver) super.getSaver();
        loginItemSaver.setLoginItem(loginItemCriptato);
        loginItemSaver.salva();

        loginItemCriptor.setLoginItem(null);
        loginItemCriptor = null;
        loginItemSaver = null;
    }

    @Override
    public void decriptaPoiMostra() {
        LoginItemSaver loginItemSaver = (LoginItemSaver) super.getSaver();
        LoginItem loginItemCriptato = loginItemSaver.getLoginItem();

        LoginItemCriptor loginItemCriptor = (LoginItemCriptor) super.getCriptor();
        loginItemCriptor.decripta();
        LoginItem loginItemDecriptato = loginItemCriptor.getLoginItem();

        loginItemCriptor =null;
        loginItemDecriptato = null;
        loginItemSaver = null;
    }
}
