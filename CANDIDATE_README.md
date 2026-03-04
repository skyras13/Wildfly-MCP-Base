# Network AIOps MCP Server — Coding Assessment

This is a coding assessment. You will be implementing backend tool logic inside a running WildFly 33 (Jakarta EE 10) application that exposes an **MCP (Model Context Protocol) server**.

> **Use of AI coding agents is allowed and encouraged.** Tools like Claude Code, Cursor, and Google Antigravity are fair game — just make sure you understand the code you submit.

---

## What You're Working With

The project is a Maven WAR application deployed on WildFly. It exposes MCP tools over **Server-Sent Events (SSE)** at:

```
http://localhost:8080/wildfly-mcp-server/api/mcp/sse
```

The MCP layer is already wired up. Your job is to implement the business logic inside `NetworkTools.java`.

### Key Files

| File | Purpose |
|---|---|
| `NetworkInventory.java` | `@Singleton` EJB holding 10 mock routers — read, don't modify |
| `Device.java` | The data model (id, ip, status, health) |
| `NetworkTools.java` | **Your work lives here** |

---

## Your Task

Open `NetworkTools.java`. You will find **three tools stubbed out with `TODO` comments**. Implement each one:

### Tool 2 — `get_device_logs(deviceId)`
Return a formatted log string for the given device. Logs should reflect the device's current status (DOWN devices should show error/warn entries, UP devices should show healthy entries). Return an error message if the device doesn't exist.

### Tool 3 — `remediate_device(deviceId, action)`
Accept a `deviceId` and an `action` (`REBOOT` or `RESET`). Call `inventory.updateDeviceStatus()` to apply the fix. Return a clear success or failure message.

### Tool 4 — `get_network_summary()`
Return a high-level summary of the entire network: total device count, how many are UP vs DOWN, and the average health score across all devices.

> **Tool 1 (`list_devices`) is already implemented.** Use it to verify your server is running correctly before tackling the others.

---

## Prerequisites

- **Docker Desktop** (that's it — JDK and Maven run inside the container)

---

## Build & Run

```bash
# 1. Build the WAR
docker run --rm -v "%cd%":/app -w /app maven:3.9-eclipse-temurin-17 mvn clean package

# 2. Build the WildFly image
docker build -t mcp-server .

# 3. Run the server
docker run -p 8080:8080 mcp-server
```

---

## Testing with MCP Inspector

You do **not** need to build a UI. Use the **MCP Inspector** to verify your tools.

### Launch the Inspector

You need Node.js installed locally for this step (it does not run in Docker).

```bash
npx @modelcontextprotocol/inspector
```

This opens a browser at `http://localhost:6274`.

### Connect to Your Server

1. Set the transport type to **SSE**
2. Enter the endpoint URL:
   ```
   http://localhost:8080/wildfly-mcp-server/api/mcp/sse
   ```
3. Click **Connect**

You should see all four tools listed. Start with `list_devices` to confirm everything is working, then test your implementations.

---

## Submission

Push your completed code to a repository and share the link. Your submission must include:

1. **Working implementations** of all three tools in `NetworkTools.java`
2. **A short video showing all the tools working** Video should show the MCP Inspector connected to the sever and showing correct results for all 3 tools you have made.

Good luck!
