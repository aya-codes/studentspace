package com.ayacodes.studentspace;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Chatroom {
    public String roomId;
    List<User> users = new ArrayList<>();
    Integer capacity = 2;
    Boolean atCapacity = false;
    Topic topic;
    List<Message> messages = new ArrayList<>();
    private LocalDateTime chatStartedAt;
    //a chat starts when it reaches 2 users
    //if I want to make group chats later,
    //      I will have to differentiate between minimum users to start a chat and capacity reached
    private static final Duration maxTimeOpen = Duration.ofMinutes(30);

    public boolean isExpired() {
        Duration timeElapsed = Duration.between(chatStartedAt, LocalDateTime.now());
        return timeElapsed.compareTo(maxTimeOpen) > 0;
    }

    public Boolean isAvailable() {
        if (atCapacity) return false;
        if (users.size() < capacity) return true;
        atCapacity = true;
        return false;
    }

    public List<User> getUsers() {
        return users;
    }

    public Boolean addUser(User user) {
        if (user.topic.equals(this.topic) && !atCapacity) users.add(user);
        else return false;
        if (users.size() == capacity) {
            atCapacity = true;
            chatStartedAt = LocalDateTime.now();
        }
        return true;
    }

    public String getMessageString() {
        StringBuilder messagesString = new StringBuilder();
        for (Message message : messages) {
            messagesString.append(message.toString());
        }
        return messagesString.toString();
    }

    public Boolean addMessage(Message message) {
        if (message.username.isBlank() || message.body.isBlank()) return false;
        LocalDateTime timeOfRequest = LocalDateTime.now();
        message.setTimestamp(timeOfRequest);
        for (User user : users) {
            if (user.username.equals(message.username)) {
                return this.messages.add(message);
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Chatroom{" +
                "id=" + roomId +
                ", users=" + users +
                ", atCapacity=" + atCapacity +
                ", topic=" + topic +
                ", messages=" + messages +
                '}';
    }
}