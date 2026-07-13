package com.professional.b07legendaryproject2026;

import java.time.Instant;
public class Comment {
    private final String msg;
    private final Instant timestamp; // Timestamp in UTC for sorting

    public Comment(String msg) {
        this.msg = msg;
        this.timestamp = Instant.now();

    }

    public Comment(String msg, Instant timestamp) {
        this.msg = msg;
        this.timestamp = timestamp;

    }

    //Get methods
    public String getMsg() {
        return msg;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
