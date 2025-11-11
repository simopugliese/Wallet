package com.simonepugliese;

import com.simonepugliese.Core.WalletManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per la WalletFactory.
 * Verifica che la factory sia in grado di assemblare correttamente
 * il WalletManager con le sue dipendenze.
 */
class WalletFactoryTest {

    /**
     * Verifica che il metodo createWalletManager (con la nuova firma
     * che accetta una password) restituisca un'istanza valida.
     */
    @Test
    void createWalletManager_shouldReturnNonNullInstance() {
        // Azione
        WalletManager manager = WalletFactory.createWalletManager("password_di_test");

        // Assert
        assertNotNull(manager, "La Factory deve creare un'istanza non-nulla di WalletManager");
    }
}