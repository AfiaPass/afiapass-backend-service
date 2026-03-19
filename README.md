# 🚀 AfiaPass Backend Monorepo

Welcome to the central backend repository for **AfiaPass**, the underlying transit permit and tax-routing infrastructure powering **the future of last mile delivery**.

This repository utilizes a **Maven Multi-Module Architecture**. This design enforces a strict separation of concerns by keeping our core blockchain cryptography completely isolated from our REST API and database layers.

---

## 🏗️ Architecture Overview

The project is split into two distinct Maven modules managed by a single Parent POM:

| Module | Framework | Purpose |
| :--- | :--- | :--- |
| **`afiapass-sdk`** | Pure Java 21 | The Blockchain Engine. Handles Stellar SDK interactions, Soroban smart contract invocation, and SEP-10 JWT cryptography. **Contains zero Spring Boot dependencies.** |
| **`afiapass-api`** | Spring Boot 3 | The Web Gateway. Handles HTTP REST requests from the Drive-Thru Afia mobile app, persists `PermitRecord` data to MySQL, and orchestrates the SDK. |

### 📁 Directory Structure
```text
afiapass-backend/
├── pom.xml                 # The Parent POM (Manages versions and links modules)
├── README.md               # This file
│
├── afiapass-sdk/           # Module 1: Core Blockchain Library
│   ├── pom.xml             # SDK Dependencies (Stellar, Nimbus JWT)
│   └── src/main/java/      
│
└── afiapass-api/           # Module 2: Spring Boot API Gateway
    ├── pom.xml             # API Dependencies (Spring Web, Data JPA, MySQL)
    ├── .env                # Local environment variables
    └── src/main/java/      

⚡ Getting Started
Prerequisites

    Java 21+

    Maven 3.9+

    MySQL (Local or Docker)

Global Build Instructions

Because this is a multi-module project, you must run build commands from this root directory. Maven will automatically compile the SDK first, and then inject it into the API Gateway.

1. Clean and Build the Entire Monorepo:
```
    Bash
    
    ./mvnw clean install
```
(Note: Use -DskipTests if you want to bypass unit tests during rapid development).

2. Run the Spring Boot Server:
Navigate into the API module to start the server:
```
    Bash
    
    cd afiapass-api
    ./mvnw spring-boot:run
```


## 🛠️ Technology Stack
* **Java 21**
* **Official Stellar Java SDK** (`network.stellar:stellar-sdk`)
* **Nimbus JOSE + JWT** (For offline token generation)
* **Lombok** (Boilerplate reduction)

---

## 🧱 Dependency Rules
**Strict Rule:** Do not introduce `spring-boot-starter-*` dependencies into this module's `pom.xml`. 

All business logic here must remain as Plain Old Java Objects (POJOs). If a class needs configuration (like the Soroban RPC URL), it should be passed in via a standard Java constructor, allowing the host application (like our API Gateway) to manage the actual environment variables.
