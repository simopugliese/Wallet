package com.simonepugliese.Manager;

import com.simonepugliese.Criptor.Criptor;
import com.simonepugliese.Item.Item;
import com.simonepugliese.Saver.Saver;

import java.util.ArrayList;
import java.util.List;

public class Manager {
    private Criptor criptor;
    private Saver saver;

    public Manager(Criptor criptor, Saver saver) {
        this.criptor = criptor;
        this.saver = saver;
    }

    public void criptaPoiSalva(Item item){
        Item itemCriptato = criptor.cripta(item);
        saver.salva(itemCriptato);
    }

    public List<Item> caricaPoiDecripta(){
        List<Item> itemCaricatiCriptati = new ArrayList<>();
        itemCaricatiCriptati = saver.carica();

        List<Item> itemDecriptati = new ArrayList<>();

        itemCaricatiCriptati.forEach(item -> {
            Item itemDecriptato = criptor.decripta(item);
            itemDecriptati.add(itemDecriptato);
        });

        return itemDecriptati;
    }
}
