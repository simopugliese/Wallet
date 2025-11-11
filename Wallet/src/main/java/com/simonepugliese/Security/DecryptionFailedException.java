package com.simonepugliese.Security;

/**
 * A custom runtime exception thrown when a decryption operation fails.
 * <p>
 * This typically occurs for one of two reasons:
 * <ol>
 * <li>The master password provided is incorrect.</li>
 * <li>The encrypted data is corrupt or has been tampered with.</li>
 * </ol>
 * <p>
 * This extends {@link RuntimeException} to avoid forcing callers to
 * handle it with checked exception blocks, allowing it to propagate up
 * to a higher-level handler (like the UI).
 */
public class DecryptionFailedException extends RuntimeException {

    /**
     * Constructs a new DecryptionFailedException with the specified detail
     * message and cause.
     *
     * @param message the detail message.
     * @param cause   the original exception (e.g., AEADBadTagException) that caused the failure.
     */
    public DecryptionFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}