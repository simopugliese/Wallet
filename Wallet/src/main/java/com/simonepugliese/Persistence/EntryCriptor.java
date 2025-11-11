package com.simonepugliese.Persistence;

import com.simonepugliese.Core.ICriptor;
import com.simonepugliese.Model.Entry;
import com.simonepugliese.Model.Field;
import com.simonepugliese.Security.CryptoUtils;

/**
 * Concrete implementation of the {@link ICriptor} strategy.
 * <p>
 * It iterates over the fields of an Entry and encrypts/decrypts
 * them based on their 'sensitive' flag.
 * <p>
 * Implements AutoCloseable to securely zero-out the password from memory.
 */
public final class EntryCriptor implements ICriptor, AutoCloseable {

    private final char[] masterPassword;

    /**
     * Constructs a new EntryCriptor.
     * @param masterPassword The master password.
     */
    public EntryCriptor(String masterPassword) {
        this.masterPassword = masterPassword.toCharArray();
    }

    @Override
    public Entry encrypt(Entry entry) {
        // Iterate over the map values (the Fields) and encrypt sensitive ones
        for (Field field : entry.getFields().values()) {
            if (field.isSensitive()) {
                field.setValue(CryptoUtils.encrypt(field.getValue(), masterPassword));
            }
        }
        return entry;
    }

    @Override
    public Entry decrypt(Entry entry) {
        // Iterate and decrypt sensitive ones
        for (Field field : entry.getFields().values()) {
            if (field.isSensitive()) {
                field.setValue(CryptoUtils.decrypt(field.getValue(), masterPassword));
            }
        }
        return entry;
    }

    @Override
    public void close() {
        java.util.Arrays.fill(masterPassword, '\0');
    }
}