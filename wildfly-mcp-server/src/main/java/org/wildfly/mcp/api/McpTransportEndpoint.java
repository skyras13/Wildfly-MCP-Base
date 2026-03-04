package org.wildfly.mcp.api;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

/**
 * Mock representation of the internal MCP Engine endpoint.
 */
@ApplicationScoped
public class McpTransportEndpoint {

    public void registerSseClient(SseEventSink sink, Sse sse) {
        sink.send(sse.newEventBuilder()
                .name("endpoint")
                .data(String.class, "mcp endpoint active (stubbed)")
                .build());
    }

    public void processMessage(String jsonRpcMessage) {
        // Parse the JSON-RPC message, execute matching @McpTool, and send response
        System.out.println("Received MCP message: " + jsonRpcMessage);
    }
}
