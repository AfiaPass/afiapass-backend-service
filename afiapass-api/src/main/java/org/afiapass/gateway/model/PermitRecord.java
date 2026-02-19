package org.afiapass.gateway.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Setter
@Getter
public class PermitRecord {
    @Id
    private UUID id;
    private String riderId;
    private String routeId;
    private double amount;
    private Instant issuedAt;
    private Instant expiresAt;
    private String stellarTxHash;
}
