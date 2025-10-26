package com.simonepugliese.Logic;

public interface EncryptionService {

    String cifra(String testoInChiaro, byte[] chiave) throws Exception;
    String decifra(String testoCifratoB64, byte[] chiave) throws Exception;
}
