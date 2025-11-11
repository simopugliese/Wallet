package com.simonepugliese;

import com.simonepugliese.Core.ICriptor;
import com.simonepugliese.Core.IEntryRepository;
import com.simonepugliese.Core.WalletManager;
import com.simonepugliese.Persistence.EntryRepository;
import com.simonepugliese.Persistence.EntryCriptor;

/**
 * Factory class responsible for assembling the application's
 * core components (DI - Dependency Injection).
 */
public class WalletFactory {

    /**
     * Creates and configures the main {@link WalletManager}.
     * This is the only instance the UI (e.g., JavaFX App)
     * should need to hold.
     * @param masterPassword The master password
     *
     * @return A fully configured WalletManager.
     */
    public static WalletManager createWalletManager(String masterPassword) {
        // 1. Create the concrete persistence (repository)
        IEntryRepository repository = new EntryRepository();

        // 2. Create the concrete encryption (criptor)
        ICriptor criptor = new EntryCriptor(masterPassword);

        // 3. Inject them into the facade
        return new WalletManager(repository, criptor);
    }
}