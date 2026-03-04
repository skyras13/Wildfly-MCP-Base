package org.wildfly.mcp.api;

import com.example.mcp.Device;
import com.example.mcp.NetworkTools;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.*;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.sse.Sse;
import jakarta.ws.rs.sse.SseEventSink;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implements the MCP SSE transport protocol:
 * - Registers SSE clients and sends the endpoint event
 * - Processes JSON-RPC messages (initialize, tools/list, tools/call)
 * - Routes responses back through the SSE sink
 */
@ApplicationScoped
public class McpTransportEndpoint {

    @Inject
    private NetworkTools networkTools;

    private final ConcurrentHashMap<String, SseEventSink> sessions = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Sse> sseMap = new ConcurrentHashMap<>();

    public void registerSseClient(SseEventSink sink, Sse sse, UriInfo uriInfo) {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, sink);
        sseMap.put(sessionId, sse);

        // Tell the client where to POST messages, using the absolute base URI
        String messageUrl = uriInfo.getBaseUri().toString() + "mcp/message?sessionId=" + sessionId;
        sink.send(sse.newEventBuilder()
                .name("endpoint")
                .data(String.class, messageUrl)
                .build());
    }

    public void processMessage(String sessionId, String jsonRpcMessage) {
        SseEventSink sink = sessions.get(sessionId);
        Sse sse = sseMap.get(sessionId);
        if (sink == null || sse == null) return;

        try (JsonReader reader = Json.createReader(new StringReader(jsonRpcMessage))) {
            JsonObject request = reader.readObject();
            String method = request.getString("method", "");
            JsonValue id = request.containsKey("id") ? request.get("id") : JsonValue.NULL;

            // Notifications have no id and require no response
            if (method.startsWith("notifications/")) return;

            JsonObject response;
            switch (method) {
                case "ping":
                    response = buildResult(id, Json.createObjectBuilder().build());
                    break;
                case "initialize":
                    response = buildInitializeResponse(id);
                    break;
                case "tools/list":
                    response = buildToolsListResponse(id);
                    break;
                case "tools/call":
                    response = buildToolsCallResponse(id, request.getJsonObject("params"));
                    break;
                default:
                    response = buildError(id, -32601, "Method not found: " + method);
            }

            sink.send(sse.newEventBuilder()
                    .name("message")
                    .data(String.class, toJsonString(response))
                    .build());

        } catch (Exception e) {
            try {
                SseEventSink s = sessions.get(sessionId);
                Sse sv = sseMap.get(sessionId);
                if (s != null && sv != null) {
                    s.send(sv.newEventBuilder()
                            .name("message")
                            .data(String.class, toJsonString(buildError(JsonValue.NULL, -32700, "Parse error")))
                            .build());
                }
            } catch (Exception ignored) { }
        }
    }

    // ---- Response builders --------------------------------------------------

    private JsonObject buildInitializeResponse(JsonValue id) {
        return buildResult(id, Json.createObjectBuilder()
                .add("protocolVersion", "2024-11-05")
                .add("capabilities", Json.createObjectBuilder().add("tools", Json.createObjectBuilder()))
                .add("serverInfo", Json.createObjectBuilder()
                        .add("name", "network-mcp-server")
                        .add("version", "1.0.0"))
                .build());
    }

    private JsonObject buildToolsListResponse(JsonValue id) {
        JsonArray tools = Json.createArrayBuilder()
                .add(toolSchema("list_devices",
                        "Returns the full inventory of all network devices including their ID, IP, Status, and Health.",
                        Json.createObjectBuilder().build()))
                .add(toolSchema("get_device_logs",
                        "Takes a deviceId and returns the recent network logs for that router.",
                        Json.createObjectBuilder()
                                .add("deviceId", Json.createObjectBuilder()
                                        .add("type", "string")
                                        .add("description", "The ID of the router (e.g., RTR-003)"))
                                .build()))
                .add(toolSchema("remediate_device",
                        "Remediates a device by performing a given action (REBOOT or RESET).",
                        Json.createObjectBuilder()
                                .add("deviceId", Json.createObjectBuilder().add("type", "string").build())
                                .add("action", Json.createObjectBuilder().add("type", "string").build())
                                .build()))
                .add(toolSchema("get_network_summary",
                        "Returns a high-level summary of the network: total devices, UP/DOWN counts, and average health score.",
                        Json.createObjectBuilder().build()))
                .build();

        return buildResult(id, Json.createObjectBuilder().add("tools", tools).build());
    }

    private JsonObject buildToolsCallResponse(JsonValue id, JsonObject params) {
        String toolName = params.getString("name", "");
        try {
            String text;
            switch (toolName) {
                case "list_devices": {
                    List<Device> devices = networkTools.listDevices();
                    JsonArrayBuilder arr = Json.createArrayBuilder();
                    for (Device d : devices) {
                        arr.add(Json.createObjectBuilder()
                                .add("id", d.getId())
                                .add("ip", d.getIp())
                                .add("status", d.getStatus())
                                .add("health", d.getHealth()));
                    }
                    text = toJsonString(arr.build());
                    break;
                }
                default:
                    return buildError(id, -32601, "Tool not found: " + toolName);
            }
            JsonObject result = Json.createObjectBuilder()
                    .add("content", Json.createArrayBuilder()
                            .add(Json.createObjectBuilder().add("type", "text").add("text", text)))
                    .build();
            return buildResult(id, result);
        } catch (Exception e) {
            return buildError(id, -32603, "Tool error: " + e.getMessage());
        }
    }

    // ---- Helpers ------------------------------------------------------------

    private JsonObject toolSchema(String name, String description, JsonObject properties) {
        return Json.createObjectBuilder()
                .add("name", name)
                .add("description", description)
                .add("inputSchema", Json.createObjectBuilder()
                        .add("type", "object")
                        .add("properties", properties))
                .build();
    }

    private JsonObject buildResult(JsonValue id, JsonObject result) {
        return Json.createObjectBuilder()
                .add("jsonrpc", "2.0")
                .add("id", id)
                .add("result", result)
                .build();
    }

    private JsonObject buildError(JsonValue id, int code, String message) {
        return Json.createObjectBuilder()
                .add("jsonrpc", "2.0")
                .add("id", id != null ? id : JsonValue.NULL)
                .add("error", Json.createObjectBuilder().add("code", code).add("message", message))
                .build();
    }

    private String toJsonString(JsonStructure json) {
        StringWriter sw = new StringWriter();
        try (JsonWriter jw = Json.createWriter(sw)) {
            jw.write(json);
        }
        return sw.toString();
    }
}
