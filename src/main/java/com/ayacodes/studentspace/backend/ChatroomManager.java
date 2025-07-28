package com.ayacodes.studentspace.backend;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static com.ayacodes.studentspace.backend.RoomStatus.*;

@Service
public class ChatroomManager {
    private final Map<String, Chatroom> rooms = new HashMap<>();
    private final Map<String, ArchivedChatroom> archivedRooms = new HashMap<>();

    public RoomStatus getRoomStatus(String roomId) {
        if (rooms.isEmpty()) return NOT_FOUND;
        Chatroom room = rooms.get(roomId);
        if (room == null) return NOT_FOUND;
        if (room.isExpired()) this.closeRoom(roomId, false);
        if (room.isClosed) return CLOSED;
        if (!room.atCapacity) return WAITING;
        return OK;
    }

    public ArchivedChatroom reportAndCloseRoom(String roomId, String reportReason) {
        Chatroom room = rooms.get(roomId);
        room.setReport(reportReason);
        return this.closeRoom(roomId, true);
    }

    public ArchivedChatroom closeRoom(String roomId, boolean closedByUser) {
        Chatroom room = rooms.get(roomId);
        room.closedByUser = closedByUser;
        room.closeRoom();
        ArchivedChatroom archivedRoom = room.createArchive();
        archivedRooms.put(roomId, archivedRoom);
        return archivedRoom;
    }

    @Scheduled(fixedRate = 300000) // every 5 minutes
    public void removeExpiredRooms() {
        rooms.entrySet().removeIf(entry -> entry.getValue().isClosed);
    }

    public String findAvailableRoom(User user) {
        for (Chatroom room : rooms.values()) {
            if (room.isAvailable() && (room.topic.equals(user.topic))) {
                this.addUserToRoom(room, user);
                return room.roomId;
            }
        }
        Chatroom newRoom = this.createRoom(user);
        return newRoom.roomId;
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

    public boolean addMessage(String roomId, RawMessage rawMessage) {
        Chatroom room = rooms.get(roomId);
        return room.addRawMessage(rawMessage);
    }

    public String expirationTime(String roomId) {
        Chatroom room = rooms.get(roomId);
        return room.getExpirationTime().toString();
    }

    public String getMessagesString(String roomId) {
        Chatroom room = rooms.get(roomId);
        if (room.messages.isEmpty()) return null;
        return room.getMessageString();
    }
}