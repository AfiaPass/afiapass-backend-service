# AfiaPass 🚀🛡️

[![Java 21](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Stellar](https://img.shields.io/badge/Blockchain-Stellar-black.svg)](https://stellar.org/)
[![Soroban](https://img.shields.io/badge/Smart%20Contracts-Soroban-orange.svg)](https://soroban.stellar.org/)

> The Unified Logistics & Tax Infrastructure for Nigerian Roads

## 📖 Executive Summary
AfiaPass is a **"Logistics Trust Layer"** that issues digital, blockchain-verified transit permits. It allows logistics companies and food platforms to pay unified levies programmatically, ensuring that funds reach the correct government and union wallets while providing riders with an unforgeable "Proof of Payment" to present at checkpoints.

## 📑 Table of Contents
- [Target Audience](#-target-audience)
- [Core Features & Functional Requirements](#-core-features--functional-requirements)
- [Technical Stack](#-technical-stack)
- [System Architecture & Latency Management](#️-system-architecture--latency-management)
- [Success Metrics (KPIs)](#-success-metrics-kpis)
- [Roadmap](#-roadmap)

---

## 🎯 Target Audience
* **Third-Party Platforms:** Food delivery apps (like ChowDeck, Glovo), e-commerce sites, and haulage companies.
* **Government/Unions:** Local Government Areas (LGAs) and transport unions seeking transparent revenue collection.
* **End Users:** Delivery riders and truck drivers navigating Nigerian roads.

---

## ✨ Core Features & Functional Requirements

### 1. Smart Levy Splitter (Soroban Contract)
* **Requirement:** Automatically distribute a single payment into multiple predefined Stellar wallets (e.g., LGA, State, Union, Service Fee).
* **Logic:** Must handle `i128` precision to ensure zero-loss rounding during Naira stablecoin (NGNC/NGNT) splits.

### 2. Digital Permit Issuance (NFT/Metadata)
* **Requirement:** Generate a unique, time-bound token (AfiaPass) upon successful payment.
* **Data Payload:** Rider ID, Vehicle Registration, Route ID, Timestamp, and Cryptographic Signature.

### 3. Offline-First Verification (The "Checkpoint" Feature)
* **Requirement:** Allow verification of a permit without active internet.
* **Solution:** Use SEP-10 styled JWT signatures. The officer’s app validates the signature against the AfiaPass Public Key locally.

### 4. Developer SDK (Java-First)
* **Requirement:** A robust Java wrapper for the Stellar SDK to allow easy integration for startups.
* **Features:** Non-blocking transaction submission via Project Loom, automatic RPC failover, and balance monitoring.

---

## 🛠️ Technical Stack

| Component | Technology |
| :--- | :--- |
| **Blockchain** | Stellar Network (Mainnet) |
| **Smart Contracts** | Soroban (Rust-based) |
| **Backend** | Java 21 (Spring Boot / Project Loom for high concurrency) |
| **API** | REST / GraphQL with Webhooks for transaction confirmation |
| **Stablecoin** | NGNC or NGNT (Naira Anchors) |

---

## 🏗️ System Architecture & Latency Management

To ensure the 5-second Stellar ledger close doesn't stall rapid logistics workflows:

1. **Optimistic Issuance:** The system immediately issues a "Pending" permit locally signed by AfiaPass.
2. **Asynchronous Settlement:** The Java backend handles the Stellar transaction in the background.
3. **Conflict Resolution:** If the blockchain transaction fails (e.g., due to insufficient funds), the JWT is revoked in the next sync cycle.

---

## 📊 Success Metrics (KPIs)

* **Verification Speed:** Under 2 seconds for an officer to scan and verify a permit.
* **Uptime:** 99.9% availability via multi-RPC failover strategy.
* **Cost Efficiency:** Total transaction fees must remain below ₦10 per permit (Stellar fees are currently < ₦1).

---

## 🗺️ Roadmap

- [ ] **Phase 1 (MVP):** Deploy Soroban contract to Testnet; build Java wrapper for basic split-payments.
- [ ] **Phase 2 (Integration):** Pilot with Drive-Thru Afia for "DailyDrop" deliveries in a single Lagos LGA.
- [ ] **Phase 3 (Scaling):** Open API for public sign-ups and introduce the "Offline Verification" mobile kit for officers.