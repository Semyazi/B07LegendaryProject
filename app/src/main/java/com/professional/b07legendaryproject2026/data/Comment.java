package com.professional.b07legendaryproject2026.data;

import java.time.Instant;
public class Comment {

    private String id;
    private final String msg;
    private final Instant timestamp; // Timestamp in UTC for sorting
    private String username;

    public Comment(String id, String msg, String username) {
        this.id = id;
        this.msg = msg;
        this.timestamp = Instant.now();
        this.username = username;
    }

    public Comment(String id, String msg, Instant timestamp, String username) {
        this.id = id;
        this.msg = msg;
        this.timestamp = timestamp;
        this.username = username;
    }

    //Get methods
    public String getId() { return id;}
    public String getMsg() {
        return msg;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
    public String getUsername() { return username; }

    //Set methods
    public void setUsername(String username) { this.username = username;}
}

