package com.simonepugliese.Data;

public class LoginBasicItem extends Item{
    public LoginBasicItem(
            int id,
            String username,
            String password,
            String notes
    ) {
        super(id, ItemType.LOGINBASIC);
        createUsernameField(username);
        createPasswordField(password);
        createNotesField(notes);
    }

    private void createUsernameField(String username){
        super.getData().put("username", new Field(super.getId(), username));
    }

    private void createPasswordField(String password){
        super.getData().put("password", new Field(super.getId(), password));
    }

    private void createNotesField(String notes){
        super.getData().put("notes", new Field(super.getId(), notes));
    }

    public String getUsername(){
        return super.getData().get("username").getField().toString();
    }

    public void setUsername(String username){
        super.getData().get("username").setField(username);
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
