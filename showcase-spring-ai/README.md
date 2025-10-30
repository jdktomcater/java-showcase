# CS Bot Service (Telegram)

## What is this
Minimal Spring Boot service to integrate Telegram as the primary customer support channel, with webhook receiver and starter SQL schema.

## Prerequisites
- Java 11+
- Maven 3.8+
- Public HTTPS endpoint for Telegram webhook (or tunnel like ngrok)

## Run
```bash
export TELEGRAM_BOT_TOKEN=<your_bot_token>
# or pass as JVM opt: -Dtelegram.bot.token=...

mvn -q -f cs-bot-service/pom.xml spring-boot:run
```
Health check:
```bash
curl -s http://localhost:8080/healthz
```

## Set Telegram Webhook
```bash
# Replace <PUBLIC_URL> with your https endpoint
curl -X POST "https://api.telegram.org/bot$TELEGRAM_BOT_TOKEN/setWebhook" \
  -d url="https://<PUBLIC_URL>/telegram/webhook/$TELEGRAM_BOT_TOKEN"
```

## SQL schema
See `sql/cs_ddl.sql` for:
- `telegram_user_binding`: chat↔platform binding
- `cs_session`, `cs_message`: conversation storage
- `cs_ticket`: ticket lifecycle
- `kb_document`, `kb_chunk`, `kb_chunk_embedding`: optional RAG KB

## Next steps
- Implement Dialog Manager to route messages to RAG or internal tools
- Add binding flow (one-time code) and authZ checks before tool calls
- Connect to ticket/agent system, stream agent replies back to Telegram
- Add observability (trace, metrics, audit)

