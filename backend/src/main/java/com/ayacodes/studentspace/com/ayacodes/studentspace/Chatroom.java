package com.ayacodes.studentspace;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Chatroom {
    public String roomId = UUID.randomUUID().toString();
    List<User> users = new ArrayList<>();
    Integer capacity = 2;
    Boolean atCapacity = false;
    Topic topic;
    public List<Message> messages = new ArrayList<>();
    Boolean isClosed = false;
    private static final Duration maxTimeOpen = Duration.ofMinutes(20);
    private Instant chatStartedAt;
    private Instant chatEndedAt;
    private int finalMessageCount;
    public boolean closedByUser;
    private boolean reportSubmitted;
    private Optional<String> reportReason;

    public Chatroom() {
        this.chatStartedAt = Instant.now();
    }

    public void setReport(String reportReason) {
        this.reportReason = Optional.of(reportReason);
        this.reportSubmitted = true;
    }

    public boolean getAtCapacity() {
        return atCapacity;
    }

    public ArchivedChatroom createArchive() {
        return new ArchivedChatroom(
                this.roomId,
                this.topic,
                maxTimeOpen,
                this.chatStartedAt,
                this.chatEndedAt,
                this.finalMessageCount,
                this.atCapacity,
                this.reportSubmitted,
                this.reportReason);
    }

    public void closeRoom() {
        this.isClosed = true;
        this.finalMessageCount = this.messages.size();
        this.addClosingMessage();
        this.chatEndedAt = Instant.now();
    }

    private void addClosingMessage() {
        Message finalMessage = new Message(
                "System",
                "This chat has been closed",
                Instant.now());
        this.addMessage(finalMessage);
    }

    public Duration timeElapsed() {
        Instant now = Instant.now();
        return Duration.between(this.chatStartedAt, now);
    }

    public Long getExpirationTime() {
        return chatStartedAt.plus(maxTimeOpen).toEpochMilli();
    }

    public boolean isExpired() {
        if (chatStartedAt == null) return false;
        boolean expired = this.timeElapsed().compareTo(maxTimeOpen) > 0;
        System.out.println("[DEBUG] Room " + roomId
                + " expired? " + expired
                + " (Started: " + chatStartedAt
                + ", Max: " + maxTimeOpen
                + ", Now: " + Instant.now() + ")");
        if (expired) this.closeRoom();
        return expired;
    }


    public Boolean isAvailable() {
        if (this.atCapacity) return false;
        if (users.size() < capacity) return true;
        this.atCapacity = true;
        return false;
    }

    public List<User> getUsers() {
        return users;
    }

    public Boolean addUser(User user) {
        if (user.topic.equals(this.topic) && !this.atCapacity) users.add(user);
        else return false;
        if (users.size() == this.capacity) {
            this.atCapacity = true;
            this.chatStartedAt = Instant.now();
        }
        return true;
    }

    public String getMessageString() {
        StringBuilder messagesString = new StringBuilder();
        for (Message message : this.messages) {
            messagesString.append(message.toString());
        }
        return messagesString.toString();
    }

    public boolean addRawMessage(RawMessage rawMessage) {
        if (rawMessage.sender().isBlank() || rawMessage.body().isBlank()) {
            return false;
        }
        Message message = new Message(rawMessage.sender(), rawMessage.body(), Instant.now());
        return this.addMessage(message);
    }

    //this doesn't check if room is closed
    public boolean addMessage(Message message) {
        if (message.sender().isBlank() || message.body().isBlank()) {
            return false;
        }
        for (User user : this.users) {
            if (user.username.equals(message.sender())) {
                return this.messages.add(message);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Chatroom{" +
                "id=" + this.roomId +
                ", users=" + this.users +
                ", atCapacity=" + this.atCapacity +
                ", topic=" + this.topic +
                ", messages=" + this.messages +
                '}';
    }
}