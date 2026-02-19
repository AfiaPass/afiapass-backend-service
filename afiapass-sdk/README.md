📦 AfiaPass Core SDK

The AfiaPass SDK is the "Truth Engine" of the platform. It is a pure Java 21 library designed using Hexagonal Architecture to isolate core logistics business rules from infrastructure volatility.

By maintaining a strict separation between domain logic and technical implementation, this SDK remains highly testable, maintainable, and framework-agnostic.
🏗️ Architectural Blueprint

The SDK follows the Ports & Adapters pattern, split into two primary layers:
1. 🛡️ The Core (The Hexagon)

This is the "Inside" of the application. It contains the business rules that define what a Permit is and how it should be issued. It has zero dependencies on external libraries like Stellar or Spring.

    domain/: Contains the "Source of Truth" models—Permit, Rider, and Route.

    exceptions/: Business-specific error types (e.g., InsufficientFundsException, InvalidRouteException).

    ports/: The boundaries of the hexagon.

        Inbound Ports: Interfaces like IssuePermitUseCase that define what the outside world can ask the SDK to do.

        Outbound Ports: Interfaces like BlockchainProvider and TokenSigner that define what the SDK needs from the outside world.

2. 🔌 The Infrastructure (The Adapters)

This is the "Outside" of the application. It contains the concrete implementations of the Outbound Ports.

    blockchain/: The Stellar Adapter. Contains the SorobanContractClient which handles the heavy lifting of XDR construction and Soroban smart contract invocation.

    security/: The Identity Adapter. Implements NimbusJwtSigner to generate the Ed25519-signed SEP-10 JWT tokens used for offline verification.

📁 Project Structure
```text

afiapass-sdk/src/main/java/org/afiapass/
├── core/                   <-- The "Hexagon" (Pure Java Logic)
│   ├── domain/             <-- Source of Truth: Permit, Rider, Route models
│   ├── exceptions/         <-- Business logic error handling
│   └── ports/              <-- Inbound/Outbound interfaces (Boundaries)
│
└── infrastructure/         <-- The "Adapters" (External Implementations)
├── blockchain/         <-- Stellar SDK & Soroban contract orchestration
└── security/           <-- Cryptographic JWT & Ed25519 signing logic
```


🛠️ Development Principles

    No Framework Bloat: Do not add Spring Boot dependencies to this module. It must remain a lightweight JAR.

    Dependency Inversion: The core must never depend on the infrastructure. Infrastructure must depend on the ports defined in the core.

    Precision Math: All financial transactions must use i128 (mapped to BigInteger or long in Java) to ensure zero-loss tax splitting.

⚡ Integration

This module is designed to be imported by a "Host" application (like the afiapass-api gateway).

Maven Dependency:
```
XML

<dependency>
    <groupId>org.afiapass.backend</groupId>
    <artifactId>afiapass-sdk</artifactId>
    <version>${project.version}</version>
</dependency>
```

🧪 Testing

Because the core is isolated, you can test the entire logistics flow using mock adapters:
```
Bash

mvn test -pl afiapass-sdk
```