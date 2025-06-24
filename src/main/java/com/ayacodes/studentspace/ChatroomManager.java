package com.ayacodes.studentspace;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ChatroomManager {
    private Map<String, Chatroom> rooms = new HashMap<>();
    private static Integer nextId = 0;

    public Map<String, Chatroom> getRooms() {
        return rooms;
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
        String roomId = UUID.randomUUID().toString();
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