package org.afiapass.infrastructure.security.jwt;

import org.stellar.sdk.KeyPair;

public class KeyManager {
    private final KeyPair platformKeyPair;

    public KeyManager() {
        // Strictly load from environment variables, never hardcode.
        String secretSeed = System.getenv("AFIAPASS_SECRET_SEED");
        if (secretSeed == null || secretSeed.isBlank()) {
            throw new IllegalStateException("Critical Security Error: AFIAPASS_SECRET_SEED environment variable is missing.");
        }
        this.platformKeyPair = KeyPair.fromSecretSeed(secretSeed);
    }

    public KeyPair getPlatformKeyPair() {
        return platformKeyPair;
    }
}