package com.ayacodes.studentspace;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Chatroom {
    Integer id;
    List<User> users = new ArrayList<User>();
    Integer capacity = 2;
    Boolean atCapacity = false;
    Topic topic;
    List<Message> messages = new ArrayList<>();

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
        if (users.size() == capacity) atCapacity = true;
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
                "id=" + id +
                ", users=" + users +
                ", atCapacity=" + atCapacity +
                ", topic=" + topic +
                ", messages=" + messages +
                '}';
    }
}