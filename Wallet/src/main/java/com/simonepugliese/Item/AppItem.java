package com.simonepugliese.Item;

public class AppItem extends Item{
    private String username;
    private String password;
    private String pin;
    private String note;
    public AppItem(String description, String username, String password, String pin, String note) {
        super(description, ItemType.APP);
        this.username = username;
        this.password = password;
        this.pin = pin;
        this.note = note;
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

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "AppItem{" +
                "description='" + super.getDescription() + '\'' +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", pin='" + pin + '\'' +
                ", note='" + note + '\'' +
                '}';
    }
}
