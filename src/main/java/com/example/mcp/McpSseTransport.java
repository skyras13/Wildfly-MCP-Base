package com.example.mcp;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.OPTIONS;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;
import org.wildfly.mcp.api.McpTransportEndpoint;

/**
 * Exposes the SSE endpoint for MCP communication.
 * This class maps the MCP Transport Layer to HTTP using JAX-RS and Server-Sent
 * Events.
 */
@ApplicationScoped
@Path("/mcp")
public class McpSseTransport {

    @Inject
    private McpTransportEndpoint mcpEndpoint;

    @GET
    @Path("/sse")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    public void configureSseConnection(@Context SseEventSink sink, @Context Sse sse, @Context UriInfo uriInfo) {
        // Route the SSE Sink into the MCP Engine
        if (mcpEndpoint != null) {
            mcpEndpoint.registerSseClient(sink, sse, uriInfo);
        } else {
            // Baseline mocked response if engine is unavailable
            sink.send(sse.newEventBuilder()
                    .name("endpoint")
                    .data(String.class, "mcp endpoint ready")
                    .build());
        }
    }

    @OPTIONS
    @Path("/sse")
    public Response optionsForSse() {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .build();
    }

    @POST
    @Path("/message")
    @Consumes(MediaType.APPLICATION_JSON)
    public void receiveMessage(@QueryParam("sessionId") String sessionId, String jsonRpcMessage) {
        // Route incoming standard JSON-RPC HTTP POST messages to the MCP Engine
        if (mcpEndpoint != null) {
            mcpEndpoint.processMessage(sessionId, jsonRpcMessage);
        }
    }

    @OPTIONS
    @Path("/message")
    public Response optionsForMessage() {
        return Response.ok()
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
                .header("Access-Control-Allow-Credentials", "true")
                .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                .build();
    }
}
