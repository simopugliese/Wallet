package com.simonepugliese.Data;

public class CreditCardItem extends Item{
    public CreditCardItem(
            int id,
            String owner,
            String bank,
            String number,
            int cvv,
            String expiration,
            String notes
    ) {
        super(id, ItemType.CREDITCARD);
        createOwnerField(owner);
        createBankField(bank);
        createNumberField(number);
        createCvvField(cvv);
        createExpirationField(expiration);
        createNotesField(notes);
    }

    private void createOwnerField(String owner){
        super.getData().put("owner", new Field(super.getId(), owner));
    }

    private void createBankField(String bank){
        super.getData().put("bank", new Field(super.getId(), bank));
    }

    private void createNumberField(String number){
        super.getData().put("number", new Field(super.getId(), number));
    }

    private void createCvvField(int cvv){
        super.getData().put("cvv", new Field(super.getId(), cvv));
    }

    private void createExpirationField(String expiration){
        super.getData().put("expiration", new Field(super.getId(), expiration));
    }

    private void createNotesField(String notes){
        super.getData().put("notes", new Field(super.getId(), notes));
    }

    public String getOwner(){
        return super.getData().get("owner").getField().toString();
    }

    public void setOwner(String owner){
        super.getData().get("owner").setField(owner);
    }

    public String getBank(){
        return super.getData().get("bank").getField().toString();
    }

    public void setBank(String bank){
        super.getData().get("bank").setField(bank);
    }

    public String getNumber(){
        return super.getData().get("number").getField().toString();
    }

    public void setNumber(String number){
        super.getData().get("number").setField(number);
    }

    public int getCvv(){
        return (int) super.getData().get("cvv").getField();
    }

    public void setCvv(int cvv){
        super.getData().get("cvv").setField(cvv);
    }

    public String getExpiration(){
        return super.getData().get("expiration").getField().toString();
    }

    public void setExpiration(String expiration){
        super.getData().get("expiration").setField(expiration);
    }

    public String getNotes(){
        return super.getData().get("notes").getField().toString();
    }

    public void setNotes(String notes){
        super.getData().get("notes").setField(notes);
    }
}

