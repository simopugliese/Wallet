package com.simonepugliese;

import com.simonepugliese.Logic.AesEncryptionService;
import com.simonepugliese.Logic.WalletService;
import com.simonepugliese.Persistence.SqliteItemRepository;

import java.util.List;

public class main {
    public static void main(String[] args) {
        WalletService ws = new WalletService(SqliteItemRepository.getInstance(), new AesEncryptionService());

        ws.saveUser("simone", "ciao");
        List<Object> ciao = ws.loadUser("simone");
        ciao.forEach(System.out::println);
        ws.unlockWallet("ciao", (byte[]) ciao.get(2));
    }
}
