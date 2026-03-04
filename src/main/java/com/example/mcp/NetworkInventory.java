package com.example.mcp;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@Startup
public class NetworkInventory {

    private final Map<String, Device> devices = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        // Initialize mock list of 10 routers as defined
        for (int i = 1; i <= 10; i++) {
            String id = "RTR-" + String.format("%03d", i);
            String ip = "192.168.10." + i;
            String status = (i % 3 == 0) ? "DOWN" : "UP";
            int health = (i % 3 == 0) ? 45 : 100;
            
            devices.put(id, new Device(id, ip, status, health));
        }
    }

    public List<Device> getDevices() {
        return new ArrayList<>(devices.values());
    }

    public Device getDevice(String id) {
        return devices.get(id);
    }

    public boolean updateDeviceStatus(String id, String action) {
        Device device = devices.get(id);
        if (device != null) {
            if ("REBOOT".equalsIgnoreCase(action) || "RESET".equalsIgnoreCase(action)) {
                device.setStatus("UP");
                device.setHealth(100);
                return true;
            }
        }
        return false;
    }
}
