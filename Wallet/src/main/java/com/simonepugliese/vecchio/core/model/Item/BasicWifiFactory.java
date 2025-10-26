package com.simonepugliese.vecchio.core.model.Item;

import java.util.List;

public class BasicWifiFactory extends ItemFactory{
    private Field ssidField = new NullField();
    private Field passwordField = new NullField();
    private Field notesField = new NullField();

    public BasicWifiFactory(Object id, String ssid, String password, String notes){
        super(id,ItemType.WIFI);
        setSsidField(ssid);
        setPasswordField(password);
        setNotesField(notes);
    }

    @Override
    public Object getId(){
        return super.getId();
    }

    public String getSsidField() {
        return ssidField.getField().toString();
    }

    public void setSsidField(String ssid) {
        this.ssidField = new Field(FieldType.STRING, (Object) ssid, super.getId());
        updateList();
    }

    public String getPasswordField() {
        return passwordField.getField().toString();
    }

    public void setPasswordField(String password) {
        this.passwordField = new Field(FieldType.STRING, (Object) password, super.getId());
        updateList();
    }

    public String getNotesField() {
        return notesField.getField().toString();
    }

    public void setNotesField(String notes) {
        this.notesField = new Field(FieldType.STRING, (Object) notes, super.getId());
        updateList();
    }

    private void updateList(){
        super.setFields(List.of(ssidField,passwordField,notesField));
    }

    @Override
    public String toString() {
        return "BasicWifiFactory{" +
                "ssidField=" + getSsidField() +
                ", passwordField=" + getPasswordField() +
                ", notesField=" + getNotesField() +
                '}';
    }
}
