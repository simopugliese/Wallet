package com.simonepugliese.Logic;

import com.simonepugliese.Data.*;

import java.util.List;

public interface ItemRepository {

    void inizializeDB();

    void saveLoginBasic(LoginBasicItem login);
    List<LoginBasicItem> loadAllLoginBasic();

    void saveCreditCard(CreditCardItem carta);
    List<CreditCardItem> loadAllCreditCards();

    void saveWifiBasic(WifiBasicItem wifi);
    List<WifiBasicItem> loadAllWifisBasic();

    void deleteItem(int id, ItemType type);
}
