# 🥤 Vending Machine Backend Microservices

A backend system for a vending machine built using **Spring Boot**, **Docker**, and **PostgreSQL** using a **microservices** architecture.

## 🧱 Microservices Overview

- **user-service** (port `8081`) — User registration, login, deposit, reset
- **product-service** (port `8082`) — Manage vending machine products
- **purchase-service** (port `8083`) — Purchase logic and change calculation
- **PostgreSQL DBs** (ports `5433`, `5434`) — Separate DBs for each service

---

## 🚀 How to Run

### 🔧 Prerequisites

Ensure the following are installed:

- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

---

### ▶️ Run with Docker Compose

From the project root directory:

```bash
docker-compose down -v         # Optional: clean previous volumes
docker-compose up --build      # Build and run all services
