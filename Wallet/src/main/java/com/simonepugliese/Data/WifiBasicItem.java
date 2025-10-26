package com.simonepugliese.Data;

public class WifiBasicItem extends Item{
    public WifiBasicItem(
            int id,
            String ssid,
            String password,
            String notes
    ) {
        super(id, ItemType.WIFIBASIC);
        createSsidField(ssid);
        createPasswordField(password);
        createNotesField(notes);
    }

    private void createSsidField(String ssid){
        super.getData().put("ssid", new Field(super.getId(), ssid));
    }

    private void createPasswordField(String password){
        super.getData().put("password", new Field(super.getId(), password));
    }

    private void createNotesField(String notes){
        super.getData().put("notes", new Field(super.getId(), notes));
    }

    public String getSsid(){
        return super.getData().get("ssid").getField().toString();
    }

    public void setSsid(String ssid){
        super.getData().get("ssid").setField(ssid);
    }

    public String getPassword(){
        return super.getData().get("password").getField().toString();
    }

    public void setPassword(String password){
        super.getData().get("password").setField(password);
    }

    public String getNotes(){
        return super.getData().get("notes").getField().toString();
    }

    public void setNotes(String notes){
        super.getData().get("notes").setField(notes);
    }
}
