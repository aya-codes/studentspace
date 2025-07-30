package com.ayacodes.studentspace;

import java.time.Instant;

public record Message (
    String sender,
    String body,
    Instant timestamp) {

    @Override
    public String toString() {
        return this.sender + ": " + this.body + "\n";
    }
}