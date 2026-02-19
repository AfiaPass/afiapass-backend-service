package org.afiapass.infrastructure.security.jwt;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.afiapass.core.domain.data.models.Permit;
import org.afiapass.core.ports.outbound.TokenSigner;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static java.util.Arrays.fill;

public class NimbusJwtSigner implements TokenSigner {

    private final KeyManager keyManager;

    public NimbusJwtSigner(KeyManager keyManager) {
        this.keyManager = keyManager;
    }

    @Override
    public String generateOfflineToken(Permit permit) {
        char[] secretChars = null;
        byte[] secretBytes = null;
        ByteBuffer byteBuffer = null;

        try {
            // 1. Get the secret seed
            secretChars = keyManager.getPlatformKeyPair().getSecretSeed();

            // 2. Securely convert char[] to byte[] WITHOUT creating an immutable String
            CharBuffer charBuffer = CharBuffer.wrap(secretChars);
            byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);
            secretBytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(secretBytes);

            // 3. Initialize the signer
            JWSSigner signer = new MACSigner(secretBytes);

            // 4. Build the claims
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(permit.riderId())
                    .issuer("AfiaPass-Truth-Engine")
                    .jwtID(permit.id().toString())
                    .claim("route", permit.routeId())
                    .claim("amount", permit.amount())
                    .claim("txHash", permit.stellarTxHash())
                    .issueTime(Date.from(permit.issuedAt()))
                    .expirationTime(Date.from(permit.expiresAt()))
                    .build();

            // 5. Sign the JWT
            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader(JWSAlgorithm.HS256),
                    claimsSet
            );
            signedJWT.sign(signer);

            return signedJWT.serialize();

        } catch (Exception e) {
            throw new RuntimeException("Failed to sign offline permit token", e);
        } finally {
            // 6. GUARANTEED MEMORY WIPE
            // This runs no matter what, even if the signing fails!
            if (secretChars != null) fill(secretChars, '\0');
            if (secretBytes != null) fill(secretBytes, (byte) 0);
            if (byteBuffer != null && byteBuffer.hasArray()) {
                fill(byteBuffer.array(), (byte) 0);
            }
        }
    }
}