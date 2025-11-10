package com.simonepugliese;

import com.simonepugliese.Core.WalletManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link WalletFactory}.
 *
 * <p>Verifies that the factory can successfully assemble
 * and return the main {@link WalletManager} facade.</p>
 */
class WalletFactoryTest {

    /**
     * Tests that the factory successfully creates a non-null WalletManager.
     * This implicitly tests that all dependencies can be resolved.
     */
    @Test
    void createWalletManager_shouldReturnNonNullInstance() {
        WalletManager manager = WalletFactory.createWalletManager();
        assertNotNull(manager, "WalletFactory should successfully create a WalletManager");
    }
}