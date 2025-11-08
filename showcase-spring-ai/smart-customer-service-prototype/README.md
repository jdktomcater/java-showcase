# SmartCS Prototype (Spring Boot + Milvus + OpenAI)

This is a minimal prototype project to validate the **RAG-based intelligent客服** flow for a payment system.
Components included:
- Spring Boot application (Java) exposing a `/api/chat` endpoint
- Milvus (via Docker Compose) for vector storage
- `data_loader.py` (Python) to create embeddings using OpenAI and load sample FAQ into Milvus
- Docker Compose to run Milvus and the app (app built locally)

**Notes**
- You must provide your OpenAI API key in `application.yml` or environment variable `OPENAI_API_KEY`.
- The prototype uses placeholders for OpenAI calls. You need to uncomment or implement actual HTTP calls as required.
- This is a PoC skeleton: adapt to production (security, auth, error handling) before use.

## Quick start (dev)

1. Ensure Docker and Java 17+ are installed.
2. Start Milvus:
   ```bash
   docker-compose up -d
   ```
3. Load example FAQ into Milvus (needs Python, `pymilvus`, `openai`):
   ```bash
   python3 data_loader.py
   ```
4. Build and run Spring app:
   ```bash
   ./mvnw spring-boot:run
   ```
5. Example request:
   ```bash
   curl -X POST http://localhost:8080/api/chat -H 'Content-Type: application/json' -d '{"user":"merchant_1","message":"订单123为什么失败？","orderId":"202511010001"}'
   ```

