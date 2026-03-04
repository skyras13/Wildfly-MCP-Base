package com.example.mcp;

public class Device {
    private String id;
    private String ip;
    private String status; // UP or DOWN
    private int health; // 0-100

    public Device() {}

    public Device(String id, String ip, String status, int health) {
        this.id = id;
        this.ip = ip;
        this.status = status;
        this.health = health;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
}
