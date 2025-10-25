package com.simonepugliese.core.model.Item;

import java.util.List;

public class CreditCardFactory extends ItemFactory{
    private Field ownerField;
    private Field bankField;
    private Field numberField;
    private Field cvvField;
    private Field expirationField;

    public CreditCardFactory(Object id, String owner, String bank, int number, int cvv, String expiration) {
        super((Object) id, ItemType.CREDITCARD);
        setOwnerField(owner);
        setBankField(bank);
        setNumberField(number);
        setCvvField(cvv);
        setExpirationField(expiration);
    }

    @Override
    public Object getId() {
        return super.getId();
    }

    public String getOwner() {
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

    public int getNumberField() {
        return (int) numberField.getField();
    }

    public void setNumberField(int number) {
        this.numberField = new Field(FieldType.INT, (Object) number, super.getId());
        updateList();
    }

    public int getCvvField() {
        return (int) cvvField.getField();
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

    private void updateList(){
        super.setFields(List.of(ownerField,bankField,numberField,cvvField,expirationField));
    }
}
