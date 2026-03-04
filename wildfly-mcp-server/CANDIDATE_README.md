# Network AIOps MCP Server - Engineering Interview

Welcome to the AIOps MCP Server project! This is a baseline Maven project for a WildFly 33+ (Jakarta EE 10) application that implements a Model Context Protocol (MCP) Server.

## Environment Overview

- **Framework:** Jakarta EE 10 / WildFly 33+
- **Transport:** Server-Sent Events (SSE) / JSON-RPC mapping for MCP
- **State Management:** A singleton `NetworkInventory` EJB holds an array of 10 virtual routers, each with an ID, IP, Status (`UP`/`DOWN`), and Health percentage.

## Your Task

This project contains the architectural plumbing for the Server. The tools have been defined with their method signatures and MCP annotations in `NetworkTools.java`, but the logic for `remediate_device` and `get_device_logs` has been stripped out. 

Your objective is to:
1. Review the `NetworkInventory.java` class to understand the mock data structure.
2. Implement the missing backend logic in `NetworkTools.java` for the tools:
   - `get_device_logs`: Should return a valid log string based on the device's current status and health.
   - `remediate_device`: Should accept a `deviceId` and an `action` (e.g., `REBOOT` or `RESET`), update the device state in the `NetworkInventory`, and return an appropriate status message.
3. Build out the frontend UI in `src/main/webapp/index.html` via JavaScript to actively connect to the MCP Server endpoints (`/api/mcp/sse` and `/api/mcp/message`) to execute the tools and display the data.
4. Ensure the project builds successfully with Maven.
5. Deploy the server locally using the provided Docker setup.

### Prerequisites

- JDK 17+
- Apache Maven 3.8+
- Docker (Desktop or Engine)

### Build and Deploy with Docker

```bash
# 1. Build the application WAR file
mvn clean package

# 2. Build the WildFly Docker image
docker build -t mcp-server .

# 3. Run the container on port 8080
docker run -p 8080:8080 mcp-server
```

Once running, you can access your UI at: `http://localhost:8080/wildfly-mcp-server/`

Good luck, and please ask if you have any questions about the environment!
