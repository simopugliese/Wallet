package com.simonepugliese.Data;

public class WifiBasicItem extends Item {
    private String ssid; // Nome della rete
    private String password;

    public WifiBasicItem(
            int id,
            String ssid,
            String password,
            String notes
    ) {
        super(id, ItemType.WIFIBASIC, notes);
        this.ssid = ssid;
        this.password = password;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
