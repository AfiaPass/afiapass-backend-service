# 🌐 AfiaPass API Gateway

The **AfiaPass API Gateway** is a high-performance Spring Boot 3 microservice that orchestrates the pure Java **AfiaPass SDK**. It acts as the "Interface" layer, providing a secure, RESTful entry point for third-party platforms, government dashboards, and mobile applications to interact with the Stellar Soroban network without needing to handle blockchain keys directly.

---

## ⚙️ Key Features

### 🔌 RESTful Blockchain Orchestration
* **Seamless Abstraction**: Wraps complex Soroban contract invocations, SEP-10 offline JWT generation, and SDK key management into simple REST endpoints.
* **Target Ecosystem**: Designed for immediate integration by logistics platforms (Drive-Thru Afia), government monitoring portals, and lightweight verification scanners.

### 🚀 Enterprise-Grade Scalability
* **Project Loom (Virtual Threads)**: Leverages Java 21's lightweight virtual threads to handle thousands of concurrent blockchain requests without thread exhaustion or blocking.
* **Stateless Architecture**: Fully containerized and ready for horizontal scaling across Kubernetes (EKS) clusters.

### ⚡ High-Speed Caching & Persistence
* **Redis Integration**: Caches verified permits to allow sub-millisecond response times for road officials scanning QR codes in the field, reducing Stellar RPC node loads.
* **Database (MySQL/PostgreSQL)**: Maintains an off-chain ledger of transaction histories, API rate limits, and analytics data for government reporting.

---

## 🧭 How AfiaPass Gateway Works

### 🎫 Permit Issuance Workflow
1. **Client Request**: A logistics vendor (e.g., Drive-Thru Afia) sends a `POST /v1/permits` request containing rider and route details.
2. **Validation**: The Gateway validates the incoming JSON payload and checks the vendor's API key.
3. **SDK Invocation**: The Gateway triggers the AfiaPass SDK via a Virtual Thread to execute the Soroban tax-split contract.
4. **Data Persistence**: Upon blockchain success, the transaction hash and generated SEP-10 JWT are saved to the database.
5. **Response**: The final Permit payload is returned to the client to be rendered as a QR code.

### 🔍 Verification & Analytics Workflow
* **`GET /v1/verify/{id}`**: A road official scans a QR code. The Gateway checks Redis first for instant validation. If missing, it uses the SDK's `TokenVerifier` to cryptographically prove the permit offline, then caches the result.
* **`GET /v1/analytics`**: Government portals can query aggregate data directly from the database without needing to scrape the blockchain.

---

## 📁 Project Structure

This module relies on the `afiapass-sdk` provided by the parent monorepo.

```text
afiapass-api/
├── README.md
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── .env                  # Environment Variables
│
├── src/main/java/org/afiapass/gateway/
│   ├── api/                   # REST Controllers & OpenAPI Definitions
│   │   ├── PermitController.java
│   │   └── AnalyticsController.java
│   │
│   ├── blockchain/            # SDK Wrapper & Orchestration Logic
│   │   ├── config/StellarConfig.java
│   │   └── services/GatewayPermitService.java
│   │
│   ├── repository/            # Spring Data JPA Repositories
│   │   ├── PermitRecord.java
│   │   └── PermitRecordRepository.java
│   │
│   └── AfiapassApplication.java # Spring Boot Main Class
│
└── src/main/resources/
    ├── application.properties # Core Spring Boot properties
    └── db/migration/          # Flyway database migrations (Future)

```

⚡ Getting Started
Prerequisites

    Java 21+

    Maven 3.9+

    Docker & Docker Compose (For Redis/DB)

Installation & Execution

1. Clone the Parent Repository
Because this API relies on the AfiaPass SDK, you must clone the entire backend monorepo.
```
Bash

git clone [https://github.com/TheTwoHorsemen/afiapass-backend.git](https://github.com/TheTwoHorsemen/afiapass-backend.git)
cd afiapass-backend
```

2. Compile the Monorepo
Run this from the root folder to compile the SDK and make it available to the API.
```
Bash

./mvnw clean install -DskipTests
```

3. Configure Environment
Navigate into the API module and set up your environment variables.
```
Bash

cd afiapass-api
cp .env.example .env
```

4. Start Infrastructure (Database & Redis)
Use Docker Compose to spin up the required caching and database layers locally.
```
Bash

docker-compose up -d
```

5. Run the Application
Start the Spring Boot server.
```
Bash

./mvnw spring-boot:run
```

🛡️ Security & Architecture Constraints

    Key Isolation: The Gateway never exposes the Platform's Stellar Secret Key via the API. All signing happens securely and internally within the encapsulated SDK layer.

    Rate Limiting: Built-in Redis-backed API rate limiting to prevent DDoS attacks and accidental spam from third-party vendor integrations.


***

This perfectly captures the high-tech requirements of Drive-Thru Afia while correctly pointing developers to the parent monorepo to run `mvn clean install` first!

