package com.simonepugliese.Logic;

import com.simonepugliese.Data.CreditCardItem;
import com.simonepugliese.Data.LoginBasicItem;
import com.simonepugliese.Data.WifiBasicItem;
import com.simonepugliese.Persistence.SqliteItemRepository;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
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

    public boolean sbloccaWallet(String masterPassword, byte[] salt) {
        if (masterPassword == null || masterPassword.isEmpty()) {
            return false;
        }
        try {
            this.chiaveDiSessione = derivaChiaveDaPassword(masterPassword, salt);
            // Qui dovresti provare a decifrare un dato di test per confermare la password
            System.out.println("Wallet sbloccato. Chiave generata in memoria.");
            return true;
        } catch (Exception e) {
            System.err.println("Errore derivazione chiave: " + e.getMessage());
            return false;
        }
    }

    public void bloccaWallet() {
        java.util.Arrays.fill(chiaveDiSessione, (byte) 0);
        this.chiaveDiSessione = null;
        System.out.println("Wallet bloccato. Chiave rimossa dalla memoria.");
    }

    private boolean isSbloccato() {
        if (this.chiaveDiSessione == null) {
            System.err.println("Errore di sicurezza: il Wallet è bloccato.");
            return false;
        }
        return true;
    }

    public void salvaUtente(String username, String password, byte[] salt){

    }

    public void salvaLogin(LoginBasicItem login) throws Exception {
        if (!isSbloccato()) return;

        String passwordInChiaro = login.getPassword();
        String passwordCifrata = encryptionService.cript(passwordInChiaro, chiaveDiSessione);
        login.setPassword(passwordCifrata);
        repository.saveLoginBasic(login);
    }

    public List<LoginBasicItem> caricaLogins() throws Exception {
        if (!isSbloccato()) return List.of();

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


    private byte[] derivaChiaveDaPassword(String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        int iterazioni = 65536;
        int lunghezzaChiave = 256;

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterazioni, lunghezzaChiave);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

        return factory.generateSecret(spec).getEncoded();
    }
}