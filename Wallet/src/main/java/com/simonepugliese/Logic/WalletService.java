package com.simonepugliese.Logic;

import com.simonepugliese.Data.CreditCardItem;
import com.simonepugliese.Data.LoginBasicItem;
import com.simonepugliese.Data.WifiBasicItem;

import com.simonepugliese.Logic.AesEncryptionUtils;

import java.util.List;
import java.util.stream.Collectors;


public class WalletService {
    private final ItemRepository repository;
    private final EncryptionService encryptionService;

    private byte[] chiaveDiSessione;

    public WalletService(ItemRepository repository, EncryptionService encryptionService) {
        this.repository = repository;
        this.encryptionService = encryptionService;
    }

    public boolean unlockWallet(String masterPassword, byte[] salt) {
        if (masterPassword == null || masterPassword.isEmpty()) {
            return false;
        }
        try {
            this.chiaveDiSessione = AesEncryptionUtils.deriveKeyFromPassword(masterPassword, salt);
            System.out.println("Wallet sbloccato. Chiave generata in memoria.");
            return true;
        } catch (Exception e) {
            System.err.println("Errore derivazione chiave: " + e.getMessage());
            return false;
        }
    }

    public void lockWallet() {
        java.util.Arrays.fill(chiaveDiSessione, (byte) 0);
        this.chiaveDiSessione = null;
        System.out.println("Wallet bloccato. Chiave rimossa dalla memoria.");
    }

    private boolean isUnlocked() {
        if (this.chiaveDiSessione == null) {
            System.err.println("Errore di sicurezza: il Wallet è bloccato.");
            return false;
        }
        return true;
    }

    public void saveUser(String username, String password){
        byte[] salt = AesEncryptionUtils.generateSalt();
        try {
            repository.saveUser(username, AesEncryptionUtils.deriveKeyFromPassword(password, salt) , salt);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Object> loadUser(String username){
        return repository.loadUser(username);
    }

    public List<LoginBasicItem> LoadLoginsBasic() throws Exception {
        if (!isUnlocked()) return List.of();

        List<LoginBasicItem> loginsCifrati = repository.loadAllLoginBasic();

        return loginsCifrati.stream().map(login -> {
            try {
                String passwordCifrata = login.getPassword();
                String passwordInChiaro = encryptionService.decript(passwordCifrata, chiaveDiSessione);
                login.setPassword(passwordInChiaro);
                return login;
            } catch (Exception e) {
                System.err.println("Impossibile decifrare login ID: " + login.getId());
                return null;
            }
        }).filter(l -> l != null).collect(Collectors.toList());
    }

    // Puoi fare lo stesso per Wifi (cifrando `password`)
    // e Carte (cifrando `number` e `cvv`)

}