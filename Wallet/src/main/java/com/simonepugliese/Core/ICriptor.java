package com.simonepugliese.Core;

import com.simonepugliese.Model.Entry;

/**
 * Public contract for an encryption/decryption strategy.
 * This defines the operations needed to secure and restore an Entry.
 */
public interface ICriptor {

    /**
     * Encrypts all sensitive fields within the given Entry.
     * This method modifies the Entry object in place.
     *
     * @param entry The Entry object with plaintext sensitive data.
     * @return The same Entry object, now with sensitive data encrypted.
     */
    Entry encrypt(Entry entry);

    /**
     * Decrypts all sensitive fields within the given Entry.
     * This method modifies the Entry object in place.
     *
     * @param entry The Entry object with encrypted sensitive data (as loaded from persistence).
     * @return The same Entry object, now with sensitive data in plaintext.
     */
    Entry decrypt(Entry entry);
}