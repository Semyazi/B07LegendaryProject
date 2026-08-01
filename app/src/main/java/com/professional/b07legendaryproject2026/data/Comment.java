package com.professional.b07legendaryproject2026.data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;

public class Comment implements Comparable<Comment> {

    private final String id;
    private final String msg;
    private final Instant timestamp; // Timestamp in UTC for sorting
    private String username;

    public Comment(String id, String msg, String username) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.msg = msg;
        this.timestamp = Instant.now();
        this.username = username;
    }

    public Comment(String id, String msg, Instant timestamp, String username) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.msg = msg;
        this.timestamp = timestamp;
        this.username = username;
    }

    //Get methods
    public String getId() {
        return id;
    }
    public String getMsg() {
        return msg;
    }
    public Instant getTimestamp() {
        return timestamp;
    }
    public String getUsername() {
        return username;
    }

    //Set methods
    public void setUsername(String username) { this.username = username;}

    //Timestamp Formatting Helper method
    public String getTimestampInDeviceTimezone() {
        if (timestamp == null) {
            return "";
        }
        ZoneId deviceZone = ZoneId.systemDefault();
        DateTimeFormatter formatter = DateTimeFormatter
                .ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT);
        return timestamp.atZone(deviceZone).format(formatter);
    }

    //Overridden methods
    @Override
    public String toString() {
        return username + ": Comment{" + "message='" + msg + '\'' + ", timestamp=" + getTimestampInDeviceTimezone() + '}';
    }

    //Comment ids are unique so just need to check ids for equality
    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Comment)) {
            return false;
        }
        Comment other = (Comment) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public int compareTo(Comment other) {
        int timestampCompare = this.timestamp.compareTo(other.timestamp);
        if (timestampCompare != 0) {
            return timestampCompare;
        }
        // Tie-breaker if Timestamps are equal
        // Handle null IDs
        if (this.id == null && other.id == null) return 0;
        if (this.id == null) return -1;
        if (other.id == null) return 1;
        return this.id.compareTo(other.id);
    }
}



