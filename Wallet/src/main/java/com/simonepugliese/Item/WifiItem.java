package com.simonepugliese.Item;

public class WifiItem extends Item{
    private String SSID;
    private String password;

    public WifiItem(String description, String SSID, String password) {
        super(description, ItemType.WIFI);
        this.SSID = SSID;
        this.password = password;
    }

    public String getSSID() {
        return SSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
