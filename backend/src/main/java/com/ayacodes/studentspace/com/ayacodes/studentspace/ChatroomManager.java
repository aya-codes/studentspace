package com.ayacodes.studentspace;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import static com.ayacodes.studentspace.RoomStatus.*;

@Service
public class ChatroomManager {
    private final Map<String, Chatroom> rooms = new HashMap<>();
    private final Map<String, ArchivedChatroom> archivedRooms = new HashMap<>();
    private final ArchiveLogger archiveLogger = new ArchiveLogger();

    public ChatroomManager() {
        System.out.println("RoomManager created: " + this);
    }

    public RoomStatus getRoomStatus(String roomId) {
        if (rooms.isEmpty()) {
            System.out.println("RoomManager is empty");
            return NOT_FOUND;
        }
        Chatroom room = rooms.get(roomId);
        if (room == null) {
            System.out.println("Couldnt find room with id: " + roomId);
            return NOT_FOUND;
        }
        if (room.isExpired()) {
            System.out.println("Room is expired");
            this.closeRoom(roomId, false);
        }
        if (room.isClosed) {
            System.out.println("Room is closed");
            return CLOSED;
        }
        if (!room.atCapacity) {
            System.out.println("Room is not yet at capacity");
            return WAITING;
        }
        System.out.println("Room ready");
        return OK;
    }

    public File generateArchiveLogFile() throws IOException {
        File file = new File("chat_log.txt");

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (ArchivedChatroom archived : archivedRooms.values()) {
                writer.write("=== Chatroom: " + archived.roomId() + " ===\n");
                writer.write("Topic: " + archived.topic().name() + "\n");
                writer.write("Max Time Open: " + archived.maxTimeOpen() + "\n");
                writer.write("Started At: " + archived.chatStartedAt() + "\n");
                writer.write("Ended At: " + archived.chatEndedAt() + "\n");
                writer.write("Final Message Count: " + archived.finalMessageCount() + "\n");
                writer.write("Closed By User: " + archived.closedByUser() + "\n");
                writer.write("Report Submitted: " + archived.reportSubmitted() + "\n");
                if (archived.reportSubmitted() && archived.reportReason() != null) {
                    writer.write("Report Reason: " + archived.reportReason().get() + "\n");
                }
                else writer.write("Report Reason: None\n");
                writer.write("\n");
            }
        }
        return file;
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
        archiveLogger.logData(archivedRoom);
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

    public Map<String, ArchivedChatroom> getArchivedRooms() {
        return archivedRooms;
    }
}