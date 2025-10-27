package com.simonepugliese.Data;

public class LoginBasicItem extends Item {

    private String username;
    private String password;
    private String urlSito;

    public LoginBasicItem(
            int id,
            String username,
            String password,
            String urlSito,
            String notes
    ) {
        super(id, ItemType.LOGINBASIC, notes);
        this.username = username;
        this.password = password;
        this.urlSito = urlSito;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrlSito() {
        return urlSito;
    }

    public void setUrlSito(String urlSito) {
        this.urlSito = urlSito;
    }
}
