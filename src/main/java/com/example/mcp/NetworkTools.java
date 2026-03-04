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

    @McpTool(name = "list_devices")
    @ToolDescription("Returns the full inventory of all network devices including their ID, IP, Status, and Health.")
    public List<Device> listDevices() {
        return inventory.getDevices();
    }

    @McpTool(name = "get_device_logs")
    @ToolDescription("Takes a deviceId and returns the recent network logs for that router.")
    public String getDeviceLogs(@ToolArg(name = "deviceId", description = "The ID of the router (e.g., RTR-001)") String deviceId) {
        Device device = inventory.getDevice(deviceId);
        if (device == null) {
            return "Error: Device " + deviceId + " not found.";
        }
        
        StringBuilder logs = new StringBuilder();
        logs.append("--- LOGS FOR ").append(deviceId).append(" ---\n");
        logs.append("[INFO] System booted successfully.\n");
        if ("DOWN".equals(device.getStatus())) {
            logs.append("[ERROR] Interface GigabitEthernet0/0 went DOWN.\n");
            logs.append("[WARN] BGP neighbor connection lost.\n");
        } else {
            logs.append("[INFO] All interfaces UP.\n");
            logs.append("[INFO] OSPF Adjacency optimal.\n");
        }
        logs.append("---------------------------------\n");
        return logs.toString();
    }

    @McpTool(name = "remediate_device")
    @ToolDescription("Remediates a device by performing a given action, returning a success or failure message.")
    public String remediateDevice(
            @ToolArg(name = "deviceId", description = "The ID of the router") String deviceId,
            @ToolArg(name = "action", description = "Action to perform: REBOOT or RESET") String action) {
        
        boolean success = inventory.updateDeviceStatus(deviceId, action);
        
        if (success) {
            return "Success: Device " + deviceId + " was successfully " + action.toLowerCase() + "ed and is now UP.";
        } else {
            return "Failure: Could not perform " + action + " on device " + deviceId + ". Verify the ID and action.";
        }
    }
}
