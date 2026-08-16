package org.afiapass.infrastructure.blockchain.stellar;

import org.afiapass.core.ports.outbound.BlockchainProvider;
import org.afiapass.infrastructure.security.jwt.KeyManager;
import org.stellar.sdk.*;
import org.stellar.sdk.responses.sorobanrpc.SendTransactionResponse;
import org.stellar.sdk.scval.Scv;

import java.math.BigDecimal;
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
    public CompletableFuture<String> payLevy(String riderId, String routeId, BigDecimal amount) {
        return CompletableFuture.supplyAsync(() -> executeSorobanContract(riderId, routeId, amount), loomExecutor);
    }

    private String executeSorobanContract(String riderId, String routeId, BigDecimal amount) {
        try {
            // 1. Fetch the account sequence using the pure Stellar KeyPair
            KeyPair platformKey = keyManager.getStellarKeyPair();
            String sourceAccountId = platformKey.getAccountId();
            TransactionBuilderAccount sourceAccount = sorobanServer.getAccount(sourceAccountId);

            // 2. Convert the Naira amount to Stroops
            // 1 NGNC = 10,000,000 stroops (10^7)
            BigDecimal stroopsPerUnit = new BigDecimal("10000000");
            BigInteger stroops = amount.multiply(stroopsPerUnit).toBigInteger();

            // 3. Build the Raw Invocation
            InvokeHostFunctionOperation invokeOp = InvokeHostFunctionOperation.invokeContractFunctionOperationBuilder(
                    contractId,
                    "issue_permit_and_split",
                    Arrays.asList(
                            Scv.toString(riderId),
                            Scv.toString(routeId),
                            Scv.toInt128(stroops)
                    )
            ).build();

            // 4. Build the initial un-prepared transaction
            Transaction rawTx = new Transaction.Builder(sourceAccount, network)
                    .addOperation(invokeOp)
                    .setTimeout(30)
                    .setBaseFee(10000)
                    .build();

            // 5. THE CRITICAL STEP: Simulate and Prepare
            // This contacts the RPC to calculate the CPU/Mem footprint and attach the SorobanData to the tx
            Transaction preparedTx = sorobanServer.prepareTransaction(rawTx);

            // 6. Sign the prepared transaction using the Stellar key
            preparedTx.sign(platformKey);

            // 7. Send to the network
            SendTransactionResponse response = sorobanServer.sendTransaction(preparedTx);

            if (SendTransactionResponse.SendTransactionStatus.PENDING.equals(response.getStatus())) {
                return response.getHash();
            } else {
                throw new RuntimeException("Blockchain transaction failed to enqueue. Error: " + response.getErrorResultXdr());
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to execute Soroban contract for rider: " + riderId, e);
        }
    }
}