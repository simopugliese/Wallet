package com.simonepugliese.vecchio.core.model.Item;

import java.util.List;

public class CreditCardFactory extends ItemFactory{
    private Field ownerField = new NullField();
    private Field bankField = new NullField();
    private Field numberField = new NullField();
    private Field cvvField = new NullField();
    private Field expirationField = new NullField();
    private Field notesField = new NullField();

    public CreditCardFactory(Object id, String owner, String bank, String number, int cvv, String expiration, String notes) {
        super(id, ItemType.CREDITCARD);
        setOwnerField(owner);
        setBankField(bank);
        setNumberField(number);
        setCvvField(cvv);
        setExpirationField(expiration);
        setNotesField(notes);
    }

    @Override
    public Object getId() {
        return super.getId();
    }

    public String getOwnerField() {
        return ownerField.getField().toString();
    }

    public void setOwnerField(String owner) {
        this.ownerField = new Field(FieldType.STRING, (Object) owner, super.getId());
        updateList();
    }

    public String getBankField() {
        return bankField.getField().toString();
    }

    public void setBankField(String bank) {
        this.bankField = new Field(FieldType.STRING, (Object) bank, super.getId());
        updateList();
    }

    public String getNumberField() {
        return numberField.getField().toString();
    }

    public void setNumberField(String number) {
        this.numberField = new Field(FieldType.STRING, (Object) number, super.getId());
        updateList();
    }

    public String getCvvField() {
        return cvvField.getField().toString();
    }

    public void setCvvField(int cvv) {
        this.cvvField = new Field(FieldType.INT, (Object) cvv, super.getId());
        updateList();
    }

    public String getExpirationField() {
        return expirationField.getField().toString();
    }

    public void setExpirationField(String expiration) {
        this.expirationField = new Field(FieldType.STRING, (Object) expiration, super.getId());
        updateList();
    }

    public String getNotesField(){
        return notesField.getField().toString();
    }

    public void setNotesField(String notes){
        this.notesField = new Field(FieldType.STRING, (Object) notes, super.getId());
        updateList();
    }

    private void updateList(){
        super.setFields(List.of(ownerField,bankField,numberField,cvvField,expirationField,notesField));
    }

    @Override
    public String toString() {
        return "CreditCardFactory{" +
                "ownerField=" + getOwnerField() +
                ", bankField=" + getBankField() +
                ", numberField=" + getNumberField() +
                ", cvvField=" + getCvvField() +
                ", expirationField=" + getExpirationField() +
                ", notesField=" + getNotesField() +
                '}';
    }
}
