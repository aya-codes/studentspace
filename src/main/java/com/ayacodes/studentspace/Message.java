package com.ayacodes.studentspace;

import java.time.LocalDateTime;

public class Message {
    String username;
    String body;
    LocalDateTime timestamp;

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getBody() {
        return body;
    }
    public void setBody(String body) {
        this.body = body;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return this.username + ": " + this.body + "\n";
    }
}