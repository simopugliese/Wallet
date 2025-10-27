package com.simonepugliese;

import com.simonepugliese.Logic.AesEncryptionService;
import com.simonepugliese.Logic.WalletService;
import com.simonepugliese.Persistence.SqliteItemRepository;

public class main {
    public static void main(String[] args) {
        WalletService ws = new WalletService(SqliteItemRepository.getInstance(), new AesEncryptionService());

    }
}
