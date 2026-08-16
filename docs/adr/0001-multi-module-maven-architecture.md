# 1. Multi-Module Maven Architecture

Date: 2026-08-16

## Status
Accepted

## Context
AfiaPass needs to interface with traditional Web2 HTTP clients while securely managing Web3 cryptography (Stellar XDR, offline Ed25519 JWT generation, Soroban simulation). Mixing Spring Boot's massive dependency tree with core cryptographic logic creates bloated, fragile, and hard-to-test codebases.

## Decision
We implemented a multi-module Maven architecture consisting of:
1. `afiapass-sdk`: A pure Java 21 module containing zero Spring Boot dependencies. It acts as the stateless bridge to the Stellar network and handles offline token signing using Nimbus JOSE.
2. `afiapass-api`: A standard Spring Boot 3 Gateway that orchestrates the SDK, handles REST traffic, and persists state via Spring Data JPA and Flyway migrations.

## Consequences
- **Positive:** The blockchain logic is highly portable and isolated.
- **Positive:** Cryptographic testing can be done entirely outside the Spring context.
- **Negative:** Requires strict discipline to prevent Web framework dependencies from bleeding into the `afiapass-sdk` `pom.xml`.