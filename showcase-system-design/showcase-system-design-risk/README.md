# Distributed Payment System Demo

A simplified **financial-grade payment deduction system** implemented with **Spring Boot**, designed to demonstrate how real-world payment systems (e.g. PayPay) handle:

* Idempotency
* Account freezing
* State machine–driven payments
* Distributed concurrency control
* Event-driven eventual consistency

This project focuses on **correctness, recoverability, and auditability**, not just performance.

---

## 🏗️ Architecture Overview

**Core Components**

* Payment Service (Spring Boot)
* MySQL (Account / Payment / Ledger)
* Redis (Redisson)
* Kafka (Event-driven consistency)

```
Client
  → API Gateway
    → Payment Service
        → Account (freeze / commit / rollback)
        → Channel (simulated)
        → Kafka (payment-result / compensation)
```

---

## 🔑 Key Design Concepts

### 1. Idempotency

* Redis (Redisson) prevents concurrent duplicate requests
* Database unique constraint provides persistence-level safety

### 2. Account Freezing Model

* total_balance
* available_balance
* frozen_balance

All deductions must **freeze first**, never directly reduce total balance.

### 3. Payment State Machine

```
INIT → PROCESSING → SUCCESS / FAIL
```

State transitions are monotonic and irreversible.

### 4. Distributed Concurrency Control

* Redisson expirable semaphore limits concurrent payment processing
* Prevents resource exhaustion and downstream overload

### 5. Event-Driven Final Consistency

* Kafka publishes payment result events after DB commit
* Compensation consumers handle timeout and uncertain states

---

## 🧾 Database Schema

See: `docs/db-schema.sql`

Key tables:

* payment_order
* account
* ledger (append-only)

---

## 🚀 How to Run

```bash
docker-compose up -d
mvn spring-boot:run
```

---

## 💬 Why This Project

This demo mirrors real-world payment systems where:

* **Money must never be lost**
* Failures must be recoverable
* All operations must be traceable and auditable

---

## 📌 Disclaimer

This is a demo project for learning and interview preparation purposes.
