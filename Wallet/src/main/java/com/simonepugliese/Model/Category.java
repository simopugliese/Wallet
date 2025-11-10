package com.simonepugliese.Model;

/**
 * Defines the top-level category for a wallet {@link Entry}.
 * <p>
 * This replaces the old inheritance-based ItemType. It is used for
 * grouping, displaying icons, and suggesting field templates in the UI.
 * It references categories from the old model like APP
 * and LOGIN.
 */
public enum Category {
    /**
     * For website or service logins (e.g., username, password, URL).
     */
    LOGIN,

    /**
     * For credit or debit cards (e.g., number, CVV, expiry).
     */
    CREDIT_CARD,

    /**
     * For Wi-Fi network credentials (e.g., SSID, password).
     */
    WIFI,

    /**
     * For application-specific credentials (e.g., license keys, PINs).
     */
    APP,

    /**
     * For secure notes with sensitive text.
     */
    SECURE_NOTE,

    /**
     * For identity documents (e.g., passport, driver's license).
     */
    IDENTITY,

    /**
     * A generic entry for any other purpose.
     */
    GENERIC
}