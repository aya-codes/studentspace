package com.ayacodes.studentspace;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> matchUserToRoom(@RequestBody User user) {
        if (user.username.isBlank()) return ResponseEntity.badRequest().body("Invalid username");
        if (!waitingUsers.add(user)) return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Queue is full. Please try again later.");
        Chatroom room = roomManager.findAvailableRoom(user);
        waitingUsers.remove(user);
        return ResponseEntity.ok("Room id: " + room.roomId);
    }

    @GetMapping("/chat/{roomId}")
    public ResponseEntity<String> getAllMessages(@PathVariable String roomId) {
        Chatroom room = roomManager.getRoom(roomId);
        if (room.isExpired()) return ResponseEntity.status(HttpStatus.GONE).body("Room has expired");
        if (room == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found");
        if (room.messages.isEmpty()) return ResponseEntity.ok("No messages yet");
        return ResponseEntity.ok(room.getMessageString());
    }

    @PostMapping("/chat/{roomId}")
    public ResponseEntity<String> sendMessage(@PathVariable String roomId, @RequestBody Message message) {
        Chatroom room = roomManager.getRoom(roomId);
        if (room.isExpired()) return ResponseEntity.status(HttpStatus.GONE).body("Room has expired");
        if (room == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found");
        if (room.addMessage(message)) return ResponseEntity.ok("Message sent");
        return ResponseEntity.badRequest().body("Unfamiliar username");
    }

}