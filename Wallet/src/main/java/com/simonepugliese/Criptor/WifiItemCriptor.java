package com.simonepugliese.Criptor;

import com.simonepugliese.Item.Item;
import com.simonepugliese.Item.WifiItem;

public class WifiItemCriptor extends Criptor{
    @Override
    public Item cripta(Item item) {
        WifiItem wifiItem = (WifiItem) item;
        String ssid = CryptoUtils.encrypt(wifiItem.getSSID());
        String password = CryptoUtils.encrypt(wifiItem.getPassword());
        if (ssid != null && password != null) {
            wifiItem.setSSID(ssid);
            wifiItem.setPassword(password);
            return wifiItem;
        }
        return null;
    }

    @Override
    public Item decripta(Item item) {
        WifiItem wifiItem = (WifiItem) item;
        String ssid = CryptoUtils.decrypt(wifiItem.getSSID());
        String password = CryptoUtils.decrypt(wifiItem.getPassword());
        if (ssid != null && password != null) {
            wifiItem.setSSID(ssid);
            wifiItem.setPassword(password);
            return wifiItem;
        }
        return null;
    }
}
