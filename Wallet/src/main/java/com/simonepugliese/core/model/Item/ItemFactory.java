package com.simonepugliese.core.model.Item;

import java.util.List;

public abstract class ItemFactory extends Item{
    public ItemFactory(Object id, ItemType itemType) {
        super(id, itemType);
    }

    @Override
    public Object getId() {
        return super.getId();
    }

    @Override
    public List<Field> getFields(){
        return super.getFields();
    }

    @Override
    public void setFields(List<Field>  fieldList){
        super.setFields(fieldList);
    }
}
