package com.example.mcp;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.wildfly.mcp.api.McpTool;
import org.wildfly.mcp.api.ToolDescription;
import org.wildfly.mcp.api.ToolArg;

import java.util.List;

@ApplicationScoped
public class NetworkTools {

    @Inject
    private NetworkInventory inventory;

    // -------------------------------------------------------------------------
    // TOOL 1: list_devices — ALREADY IMPLEMENTED (do not modify)
    // Returns the full device inventory. Use this to verify your MCP setup works.
    // -------------------------------------------------------------------------
    @McpTool(name = "list_devices")
    @ToolDescription("Returns the full inventory of all network devices including their ID, IP, Status, and Health.")
    public List<Device> listDevices() {
        return inventory.getDevices();
    }

    // -------------------------------------------------------------------------
    // TOOL 2: get_device_logs — TODO: Implement this method
    //
    // Given a deviceId, return a formatted log string for that device.
    // Requirements:
    // - If the device is not found, return a meaningful error message.
    // - If the device is DOWN, the logs should reflect error/warn conditions
    // (e.g., interface down, BGP neighbor lost).
    // - If the device is UP, the logs should reflect healthy conditions.
    // -------------------------------------------------------------------------
    @McpTool(name = "get_device_logs")
    @ToolDescription("Takes a deviceId and returns the recent network logs for that router.")
    public String getDeviceLogs(
            @ToolArg(name = "deviceId", description = "The ID of the router (e.g., RTR-003)") String deviceId) {

        // TODO: Implement this tool
        throw new UnsupportedOperationException("get_device_logs not yet implemented.");
    }

    // -------------------------------------------------------------------------
    // TOOL 3: remediate_device — TODO: Implement this method
    //
    // Given a deviceId and an action (REBOOT or RESET), attempt to bring the
    // device back online.
    // Requirements:
    // - Call inventory.updateDeviceStatus(deviceId, action) to apply the fix.
    // - Return a clear success message if the action worked.
    // - Return a clear failure message if the device was not found or the
    // action was invalid.
    // -------------------------------------------------------------------------
    @McpTool(name = "remediate_device")
    @ToolDescription("Remediates a device by performing a given action (REBOOT or RESET), returning a success or failure message.")
    public String remediateDevice(
            @ToolArg(name = "deviceId", description = "The ID of the router to fix (e.g., RTR-003)") String deviceId,
            @ToolArg(name = "action", description = "Action to perform: REBOOT or RESET") String action) {

        // TODO: Implement this tool
        throw new UnsupportedOperationException("remediate_device not yet implemented.");
    }

    // -------------------------------------------------------------------------
    // TOOL 4: get_network_summary — TODO: Implement this method
    //
    // Returns a high-level summary of the entire network's health.
    // Requirements:
    // - Count the total number of devices.
    // - Count how many are UP and how many are DOWN.
    // - Calculate the average health score across all devices.
    // - Return the result as a formatted string or JSON.
    // -------------------------------------------------------------------------
    @McpTool(name = "get_network_summary")
    @ToolDescription("Returns a high-level summary of the network: total devices, how many are UP/DOWN, and the average health score.")
    public String getNetworkSummary() {

        // TODO: Implement this tool
        throw new UnsupportedOperationException("get_network_summary not yet implemented.");
    }
}
