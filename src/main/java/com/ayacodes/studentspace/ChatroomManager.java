package com.ayacodes.studentspace;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ChatroomManager {
    private Map<Integer, Chatroom> rooms = new HashMap<>();
    private static Integer nextId = 0;

    public Map<Integer, Chatroom> getRooms() {
        return rooms;
    }

    public Chatroom getRoom(int id) {
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
        newRoom.id = nextId++;
        newRoom.topic = user.topic;
        rooms.put(newRoom.id, newRoom);
        return addUserToRoom(newRoom, user);
    }

    private Chatroom addUserToRoom(Chatroom room, User user) {
        room.addUser(user);
        user.matchedToRoom = true;
        return room;
    }
}