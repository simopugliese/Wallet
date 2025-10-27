package com.simonepugliese.Logic;

import com.simonepugliese.Data.*;

import java.util.List;

public interface ItemRepository {

    void inizializeDB();

    void saveUser(String username, byte[] password, byte[] salt);
    List<Object> loadUser(String username);

    void saveLoginBasic(LoginBasicItem login);
    List<LoginBasicItem> loadAllLoginBasic();

    void saveCreditCard(CreditCardItem carta);
    List<CreditCardItem> loadAllCreditCards();

    void saveWifiBasic(WifiBasicItem wifi);
    List<WifiBasicItem> loadAllWifisBasic();

    void deleteItem(int id, ItemType type);
}
