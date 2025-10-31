package com.simonepugliese.Item;

public class LoginItem extends Item{
    private String username;
    private String password;
    private String urlSito;

    public LoginItem(String description, String username, String password, String urlSito) {
        super(description, ItemType.LOGIN);
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

    @Override
    public String toString() {
        return "LoginItem{" +
                "description='" + super.getDescription() + '\'' +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", urlSito='" + urlSito + '\'' +
                '}';
    }
}
