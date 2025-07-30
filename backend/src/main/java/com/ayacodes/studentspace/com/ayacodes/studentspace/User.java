package com.ayacodes.studentspace;

public class User {
    String username;
    Topic topic;
    Boolean matchedToRoom = false;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public Boolean getMatchedToRoom() {
        return matchedToRoom;
    }

    public void setMatchedToRoom(Boolean matchedToRoom) {
        this.matchedToRoom = matchedToRoom;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", topic=" + topic +
                ", matchedToRoom=" + matchedToRoom +
                '}';
    }
}