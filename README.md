
# 🚀 AfiaPass Backend Monorepo

Welcome to the central backend repository for AfiaPass. This system provides the underlying transit permit issuing, SEP-10 cryptography, and tax-routing infrastructure powering the future of last-mile delivery.

This repository utilizes a **Maven Multi-Module Architecture**. This design enforces a strict separation of concerns by keeping our core blockchain cryptography completely isolated from our REST API and database layers, ensuring a robust foundation for distributed systems.

---

## 🏗️ Architecture & High-Level System Design

AfiaPass operates as a hybrid distributed system. It bridges traditional high-concurrency Web2 commerce platforms (like Drive-Thru Afia) with Web3 decentralized trust networks (Stellar/Soroban).

### The System Components

1. **Client Applications (Mobile/Web):** Requests permits, handles payments, and displays the Offline Ed25519 JWT (QR Code) for checkpoints.
2. **Spring Boot API (`afiapass-api`):** The orchestration layer. It manages relational state, user sessions, standard business logic, and API rate limiting.
3. **Blockchain Java SDK (`afiapass-sdk`):** The cryptographic engine. It acts as the stateless bridge to the Stellar network, handling transaction building, Soroban contract invocation, and token signing.
4. **Relational Database (MySQL):** The system of record for off-chain data (user profiles, standard transactional history, API logs).
5. **Stellar Network (Soroban):** The immutable ledger for digital provenance, tax-routing smart contracts, and decentralized permit verification.

### Data Flow & Interaction Diagram

```text
+-------------------+       REST / JSON        +---------------------------+
|                   | -----------------------> |                           |
|                   |                          |   afiapass-api            |
|   Clients         | <----------------------- |   (Spring Boot Gateway)   |
|                   |   JWT Permit Response    |                           |
+-------------------+                          +---------------------------+
         |                                           |               |
         | (Offline QR Scan)                         | (POJOs)       | (JPA/SQL)
         v                                           v               v
+-------------------+                          +---------------------------+
|                   |                          |                           |
|   Checkpoint      |                          |   afiapass-sdk            |
|   Scanner App     |                          |   (Pure Java Crypto)      |
|                   |                          |                           |
+-------------------+                          +---------------------------+
                                                     |               |
                                     (RPC / XDR)     |               | (SEP-10 / Ed25519)
                                                     v               v
                                               +-----------+   +-------------+
                                               | Stellar   |   | Offline JWT |
                                               | Network   |   | Generation  |
                                               +-----------+   +-------------+

```

### Core Workflows

**1. Permit Issuance & Tax Routing**

* The mobile client requests a transit permit via a REST POST request to the API.
* The API records the request intent in MySQL.
* The API passes the transaction details to the `afiapass-sdk`.
* The SDK invokes the Soroban smart contract to execute the tax-routing logic and record the issuance on-chain.
* Upon Stellar confirmation, the SDK signs a deterministic, time-bound Ed25519 JWT containing the transaction hash and route details.
* The API returns this JWT to the client to be rendered as a QR code.

**2. Offline Checkpoint Verification**

* Because road checkpoints lack reliable internet, verification relies heavily on asymmetric cryptography rather than API calls.
* The scanner app reads the Ed25519 JWT from the rider's screen.
* The app mathematically verifies the signature against the AfiaPass Platform's known Public Key entirely offline, instantly validating the permit's authenticity.

---

## 📁 Directory Structure

```text
afiapass-backend/
├── pom.xml                 # The Parent POM (Manages versions and links modules)
├── README.md               # This file
│
├── afiapass-sdk/           # Module 1: Core Blockchain Library
│   ├── pom.xml             # SDK Dependencies (Stellar SDK, Nimbus JWT)
│   └── src/main/java/      
│
└── afiapass-api/           # Module 2: Spring Boot API Gateway
    ├── pom.xml             # API Dependencies (Spring Web, Data JPA, MySQL)
    ├── .env                # Local environment variables (Ignored by Git)
    └── src/main/java/      

```

---

## ⚡ Getting Started

### Prerequisites

* **Java 21+** (Eclipse Temurin or Amazon Corretto recommended)
* **Maven 3.9+**
* **MySQL 8+** (Running locally or via Docker)

### 1. Environment Configuration

Before starting the application, you must configure your environment variables.
Create a `.env` file inside the `afiapass-api/` directory (or export these to your system):

```env
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/afiapass_dev
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=your_password

# Stellar & Soroban Configuration
STELLAR_NETWORK=TESTNET
SOROBAN_RPC_URL=https://soroban-testnet.stellar.org
AFIAPASS_PLATFORM_PRIVATE_KEY=S_YOUR_SECRET_KEY_HERE

```

### 2. Global Build Instructions

Because this is a multi-module project, you must run build commands from the **root directory**. Maven will automatically compile the SDK first, and then inject it into the API Gateway.

**Clean and Build the Entire Monorepo:**

```bash
./mvnw clean install

```

*(Note: Use `-DskipTests` if you need to bypass unit tests during rapid local iteration).*

### 3. Run the Spring Boot Server

Once built, navigate into the API module to start the server:

```bash
cd afiapass-api
./mvnw spring-boot:run

```

The API will be available at `http://localhost:8080`.

---

## 🧱 Module Guidelines & Dependency Rules

### The `afiapass-sdk` Rules

* **Technology:** Java 21, Official Stellar Java SDK (`network.stellar:stellar-sdk`), Nimbus JOSE + JWT, Lombok.
* **Strict Boundary:** Do **not** introduce `spring-boot-starter-*` dependencies into this module's `pom.xml`.
* **Why?** All business and cryptographic logic here must remain as Plain Old Java Objects (POJOs). If a class needs configuration (like the Soroban RPC URL), it must be passed in via a standard Java constructor. This ensures our blockchain logic remains hyper-portable and theoretically usable in any Java application, not just Spring.

### The `afiapass-api` Rules

* **Technology:** Spring Boot 3, Spring Web, Spring Data JPA, MySQL Driver.
* **Architecture:** Follow standard Controller -> Service -> Repository patterns for REST API design.
* **Dependency:** This module explicitly depends on `afiapass-sdk` in its `pom.xml` to access the blockchain utilities.

