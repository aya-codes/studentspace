package com.ayacodes.studentspace;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Chatroom {
    public String roomId = UUID.randomUUID().toString();
    List<User> users = new ArrayList<>();
    Integer capacity = 2;
    Boolean atCapacity = false;
    Topic topic;
    List<Message> messages = new ArrayList<>();
    Boolean isClosed = false;
    private LocalDateTime chatStartedAt;
    //a chat starts when it reaches 2 users
    //if I want to make group chats later,
    //      I will have to differentiate between minimum users to start a chat and capacity reached
    private static final Duration maxTimeOpen = Duration.ofMinutes(30);
    //chat expires after 30 minutes, might change this
    private static LocalDateTime chatEndedAt;

    public void closeRoom() {
        this.addClosingMessage();
        this.isClosed = true;
        this.chatEndedAt = LocalDateTime.now();
    }

    private void addClosingMessage() {
        Message finalMessage = new Message();
        finalMessage.username = "System";
        finalMessage.timestamp = LocalDateTime.now();
        finalMessage.body = "This chat has been closed";
        this.addMessage(finalMessage);
    }

    public Duration timeElapsed() {
        LocalDateTime now = LocalDateTime.now();
        return Duration.between(this.chatStartedAt, now);
    }

    public String minutesRemaining() {
        Duration timeRemaining = maxTimeOpen.minus(this.timeElapsed());
        return timeRemaining.toMinutes() + " minutes";
    }

    public boolean isExpired() {
        if (chatStartedAt == null) return false;
        if (this.isClosed) return true;
        boolean expired = this.timeElapsed().compareTo(maxTimeOpen) > 0;
        if (expired) {
            this.isClosed = true;
            this.chatEndedAt = LocalDateTime.now();
        }
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
            this.chatStartedAt = LocalDateTime.now();
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

    public Boolean addMessage(Message message) {
        if (message.username.isBlank() || message.body.isBlank()) return false;
        LocalDateTime timeOfRequest = LocalDateTime.now();
        message.setTimestamp(timeOfRequest);
        for (User user : this.users) {
            if (user.username.equals(message.username)) {
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