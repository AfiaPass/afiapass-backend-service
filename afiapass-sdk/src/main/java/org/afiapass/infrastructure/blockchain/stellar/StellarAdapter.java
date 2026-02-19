package org.afiapass.infrastructure.blockchain.stellar;

import org.afiapass.core.ports.outbound.BlockchainProvider;
import org.afiapass.infrastructure.security.jwt.KeyManager;
import org.stellar.sdk.*;
import org.stellar.sdk.responses.sorobanrpc.SendTransactionResponse;
import org.stellar.sdk.scval.Scv;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StellarAdapter implements BlockchainProvider {

    private final KeyManager keyManager;
    private final SorobanServer sorobanServer;
    private final Network network;
    private final String contractId;

    private final ExecutorService loomExecutor = Executors.newVirtualThreadPerTaskExecutor();

    public StellarAdapter(KeyManager keyManager, String rpcUrl, Network network, String contractId) {
        this.keyManager = keyManager;
        this.sorobanServer = new SorobanServer(rpcUrl);
        this.network = network;
        this.contractId = contractId;
    }

    @Override
    public CompletableFuture<String> payLevy(String riderId, String routeId, double amount) {
        return CompletableFuture.supplyAsync(() -> executeSorobanContract(riderId, routeId, amount), loomExecutor);
    }

    private String executeSorobanContract(String riderId, String routeId, double amount) {
        try {
            String sourceAccountId = keyManager.getPlatformKeyPair().getAccountId();

            // Fetch the current sequence number for the platform account
            Account sourceAccount = (Account) sorobanServer.getAccount(sourceAccountId);

            // Convert the Naira amount to Stroops (Stellar's smallest unit: 10^-7)
            // Assuming amount is in NGNC, 500 NGN = 5,000,000,000 Stroops
            long stroops = (long) (amount * 10_000_000L);

            // Build the Soroban Smart Contract Invocation using the modern builder
            InvokeHostFunctionOperation invokeOp = InvokeHostFunctionOperation.invokeContractFunctionOperationBuilder(
                    contractId,
                    "issue_permit_and_split",
                    Arrays.asList(
                            Scv.toString(riderId),
                            Scv.toString(routeId),
                            Scv.toInt128(BigInteger.valueOf(stroops))
                    )
            ).build();

            Transaction transaction = new Transaction.Builder(sourceAccount, network)
                    .addOperation(invokeOp)
                    .setTimeout(30)
                    .setBaseFee(10000)
                    .build();

            transaction.sign(keyManager.getPlatformKeyPair());
            SendTransactionResponse response = sorobanServer.sendTransaction(transaction);

            // Soroban RPC returns PENDING if the transaction is successfully enqueued
            if (SendTransactionResponse.SendTransactionStatus.PENDING.equals(response.getStatus())) {
                return response.getHash();
            } else {
                throw new RuntimeException("Blockchain transaction failed to enqueue. Status: "
                        + response.getStatus() + " | Error: " + response.getErrorResultXdr());
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to execute Soroban contract for rider: " + riderId, e);
        }
    }
}