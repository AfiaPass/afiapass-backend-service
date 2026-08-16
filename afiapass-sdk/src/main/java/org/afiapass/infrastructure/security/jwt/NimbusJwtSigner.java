package org.afiapass.infrastructure.security.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.afiapass.core.domain.data.models.Permit;
import org.afiapass.core.ports.outbound.TokenSigner;

import java.util.Date;

public class NimbusJwtSigner implements TokenSigner {

    private final KeyManager keyManager;

    public NimbusJwtSigner(KeyManager keyManager) {
        this.keyManager = keyManager;
    }

    @Override
    public String generateOfflineToken(Permit permit) {
        try {
            // 1. Initialize the Ed25519 signer
            // Note: keyManager.getPlatformKeyPair() must return a Nimbus OctetKeyPair
            JWSSigner signer = new Ed25519Signer(keyManager.getPlatformKeyPair());

            // 2. Create JWS Header with EdDSA algorithm
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.EdDSA).build();

            // 3. Build the claims (Strictly aligned with the frontend AfiaPassPayload interface)
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(permit.stellarTxHash())
                    .issuer(keyManager.getPlatformPublicKey()) // Scanner verifies this exact key
                    .jwtID(permit.id().toString())
                    .claim("riderId", permit.riderId())        // Explicitly named for the scanner
                    .claim("routeId", permit.routeId())        // Changed from "route" to "routeId"
                    .claim("amount", permit.amount().toPlainString())
                    .issueTime(Date.from(permit.issuedAt()))
                    .expirationTime(Date.from(permit.expiresAt()))
                    .build();

            // 4. Sign the JWT
            SignedJWT signedJWT = new SignedJWT(header, claimsSet);
            signedJWT.sign(signer);

            return signedJWT.serialize();

        } catch (Exception e) {
            // Consider wrapping this in a custom TokenSigningException in the future
            throw new RuntimeException("Failed to sign offline permit token", e);
        }
    }
}