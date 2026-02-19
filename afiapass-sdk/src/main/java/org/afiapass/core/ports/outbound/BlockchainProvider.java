package org.afiapass.core.ports.outbound;

import java.util.concurrent.CompletableFuture;

public interface BlockchainProvider {
    CompletableFuture<String> payLevy(String riderId, String routeId, double amount);
}
