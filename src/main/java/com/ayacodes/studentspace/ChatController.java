package com.ayacodes.studentspace;

import org.springframework.web.bind.annotation.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@RestController
public class ChatController {
    private Queue<User> waitingUsers = new ConcurrentLinkedQueue<>();
    private final ChatroomManager roomManager;

    public ChatController(ChatroomManager roomManager) {
        this.roomManager = roomManager;
    }

    @PostMapping ("/start")
    public String matchUserToRoom(@RequestBody User user) {
        waitingUsers.add(user);
        Chatroom room = roomManager.findAvailableRoom(user);
        if (room.atCapacity) {
            waitingUsers.remove(user);
            return "Your chatroom is ready! Its id is " + room.id;
        }
        return "Please hold while we find someone for you to chat with...";
    }

    @GetMapping("/chat/{roomId}")
    public String getRoom(@PathVariable int roomId) {
        Chatroom room = roomManager.getRoom(roomId);
        if (room == null) return "I can't find a room with id: " + roomId;
        if (room.messages.isEmpty()) return "No messages yet";
        return room.getMessageString();
    }

    @PostMapping("/chat/{roomId}")
    public String sendMessage(@PathVariable int roomId, @RequestBody Message message) {
        Chatroom room = roomManager.getRoom(roomId);
        if (room == null) return "I can't find a room with id: " + roomId;
        if (room.addMessage(message)) return "Message sent";
        return "Username is not familiar";
    }

}