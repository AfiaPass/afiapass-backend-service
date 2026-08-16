package org.afiapass.infrastructure.security.jwt;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import org.stellar.sdk.KeyPair;
import org.stellar.sdk.StrKey;

public class KeyManager {

    // Nimbus expects an OctetKeyPair for Ed25519 signing
    private final OctetKeyPair platformKeyPair;

    // We store the "G..." address to use as the JWT Issuer (iss)
    private final String platformPublicKey;

    // The pure Stellar KeyPair used by the Blockchain Adapter
    private final KeyPair stellarKeyPair;

    public KeyManager(String platformSecretSeed) {
        // The secret seed is now injected, improving testability and configuration flexibility.
        if (platformSecretSeed == null || platformSecretSeed.isBlank()) {
            throw new IllegalStateException("Critical Security Error: AFIAPASS_SECRET_SEED environment variable is missing.");
        }

        // 1. Validate and load the Stellar KeyPair to the class field
        this.stellarKeyPair = KeyPair.fromSecretSeed(secretSeed);
        this.platformPublicKey = this.stellarKeyPair.getAccountId();

        // 2. Extract the raw 32-byte arrays needed for standard Ed25519 cryptography
        byte[] publicKeyBytes = this.stellarKeyPair.getPublicKey();
        byte[] privateKeyBytes = StrKey.decodeEd25519SecretSeed(platformSecretSeed.toCharArray());

        // 3. Build the Nimbus OctetKeyPair
        this.platformKeyPair = new OctetKeyPair.Builder(
                Curve.Ed25519,
                Base64URL.encode(publicKeyBytes)
        )
                .d(Base64URL.encode(privateKeyBytes))
                .build();
    }

    public OctetKeyPair getPlatformKeyPair() {
        return platformKeyPair;
    }

    public String getPlatformPublicKey() {
        return platformPublicKey;
    }

    // Exposed for the StellarAdapter to use for Soroban signing
    public KeyPair getStellarKeyPair() {
        return stellarKeyPair;
    }
}