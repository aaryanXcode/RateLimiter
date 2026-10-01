package com.server.ratelimiter.domain;

public class User {
    public User(String name, Long id, String ip, String mail) {
        this.gmail = mail;
        this.id = id;
        this.ipAddress = ip;
        this.name = name;
    }
    String name;
    Long id;
    String ipAddress;
    String gmail;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public String getGmail() {
        return gmail;
    }
    public void setGmail(String gmail) {
        this.gmail = gmail;
    }
}
