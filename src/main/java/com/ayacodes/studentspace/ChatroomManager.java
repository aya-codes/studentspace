package com.ayacodes.studentspace;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChatroomManager {
    private Map<String, Chatroom> rooms = new HashMap<>();

    public Map<String, Chatroom> getRooms() {
        return rooms;
    }

    public void removeRoom(String id) {
        rooms.remove(id);
    }

    @Scheduled(fixedRate = 300000) // every 5 minutes
    public void removeExpiredRooms() {
        rooms.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    public Chatroom getRoom(String id) {
        return rooms.get(id);
    }

    public Chatroom findAvailableRoom(User user) {
        for (Chatroom room : rooms.values()) {
            if (room.isAvailable() && (room.topic.equals(user.topic))) {
                return this.addUserToRoom(room, user);
            }
        }
        return this.createRoom(user);
    }

    public Chatroom createRoom(User user) {
        Chatroom newRoom = new Chatroom();
        newRoom.topic = user.topic;
        rooms.put(newRoom.roomId, newRoom);
        return addUserToRoom(newRoom, user);
    }

    private Chatroom addUserToRoom(Chatroom room, User user) {
        room.addUser(user);
        user.matchedToRoom = true;
        return room;
    }
}