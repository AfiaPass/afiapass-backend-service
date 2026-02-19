package org.afiapass.core.domain.data.models;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a digital transit permit issued on the Stellar network.
 */
public record Permit(
        UUID id,
        String riderId,
        String routeId,
        double amount,
        Instant issuedAt,
        Instant expiresAt,
        String stellarTxHash
) {
    public boolean isValid() {
        return Instant.now().isBefore(expiresAt);
    }
}