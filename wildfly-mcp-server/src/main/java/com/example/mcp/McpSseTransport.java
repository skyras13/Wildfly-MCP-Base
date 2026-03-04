package com.example.mcp;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import org.wildfly.mcp.api.McpTransportEndpoint;

/**
 * Exposes the SSE endpoint for MCP communication.
 * This class maps the MCP Transport Layer to HTTP using JAX-RS and Server-Sent Events.
 */
@ApplicationScoped
@Path("/mcp")
public class McpSseTransport {

    /**
     * Hypothetical integration entrypoint with the wildfly-mcp-api to delegate
     * SSE connections and JSON-RPC messages.
     */
    @Context
    private McpTransportEndpoint mcpEndpoint;

    @GET
    @Path("/sse")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void configureSseConnection(@Context SseEventSink sink, @Context Sse sse) {
        // Route the SSE Sink into the MCP Engine
        if (mcpEndpoint != null) {
            mcpEndpoint.registerSseClient(sink, sse);
        } else {
            // Baseline mocked response if engine is unavailable
            sink.send(sse.newEventBuilder()
               .name("endpoint")
               .data(String.class, "mcp endpoint ready")
               .build());
        }
    }

    @POST
    @Path("/message")
    @Consumes(MediaType.APPLICATION_JSON)
    public void receiveMessage(String jsonRpcMessage) {
        // Route incoming standard JSON-RPC HTTP POST messages to the MCP Engine
        if (mcpEndpoint != null) {
            mcpEndpoint.processMessage(jsonRpcMessage);
        }
    }
}
