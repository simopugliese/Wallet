package com.simonepugliese.Logic;

import com.simonepugliese.Data.*;

import java.util.List;

public interface ItemRepository {

    void inizializzaDB();

    void salvaLoginsBasic(LoginBasicItem login);
    List<LoginBasicItem> caricaTuttiLoginBasic();

    void salvaCreditCards(CreditCardItem carta);
    List<CreditCardItem> caricaTutteCreditCards();

    void salvaWifisBasic(WifiBasicItem wifi);
    List<WifiBasicItem> caricaTuttiWifisBasic();

    void deleteItem(int id, ItemType type);
}
