package com.simonepugliese.Logic;

public interface EncryptionService {

    String cript(String testoInChiaro, byte[] chiave) throws Exception;
    String decript(String testoCifratoB64, byte[] chiave) throws Exception;
}
