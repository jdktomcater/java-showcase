# Showcase System Design

## SkyWalking Tracing

This compose setup is ready for SkyWalking APM. It uses the official OAP/UI
containers and mounts the Java agent into each service container.

### 1) Download the SkyWalking agent

Download the SkyWalking binary package (9.5.0) and copy the `agent` directory
into `showcase-system-design/skywalking/agent`, so this file exists:

```
showcase-system-design/skywalking/agent/skywalking-agent.jar
```

### 2) Start containers

```
docker-compose up -d
```

### 3) Open the UI

```
http://localhost:8089
```

### Notes
- Agent options are injected via `JAVA_TOOL_OPTIONS`.
- Service names are set with `SW_AGENT_NAME`.
- The OAP server runs in memory mode for demo usage.
