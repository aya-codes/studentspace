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
        if (user.username.isBlank() || !this.waitingUsers.add(user)) {
            return this.resolveUserIssue(user);
        }
        Chatroom room = this.roomManager.findAvailableRoom(user);
        this.waitingUsers.remove(user);
        return ResponseEntity.ok(room.roomId);
    }

    @GetMapping("/chat/{roomId}")
    public ResponseEntity<String> getAllMessages(@PathVariable String roomId) {
        Chatroom room = this.roomManager.getRoom(roomId);
        if (room==null || room.isExpired()) {
            return this.resolveRoomIssue(room);
        }
        if (room.messages.isEmpty()) {
            return ResponseEntity.ok(this.timer(room) + "No messages yet");
        }
        return ResponseEntity.ok(this.timer(room) + room.getMessageString());
    }

    @PostMapping("/chat/{roomId}")
    public ResponseEntity<String> sendMessage(@PathVariable String roomId, @RequestBody Message message) {
        Chatroom room = this.roomManager.getRoom(roomId);
        if (room==null || room.isExpired()) {
            return this.resolveRoomIssue(room);
        }
        if (room.addMessage(message)) {
            return ResponseEntity.ok(this.timer(room) + "Message sent");
        }
        return ResponseEntity.badRequest().body("Unfamiliar username or badly formatted message");
    }

    @GetMapping("/chat/{roomId}/timer")
    public ResponseEntity<String> getTimeRemaining(@PathVariable String roomId) {
        Chatroom room = this.roomManager.getRoom(roomId);
        if (room==null || room.isExpired()) {
            return this.resolveRoomIssue(room);
        }
        else return ResponseEntity.ok(this.timer(room));
    }

    @GetMapping("/chat/{roomId}/end")
    public ResponseEntity<String> endChat(@PathVariable String roomId) {
        Chatroom room = roomManager.getRoom(roomId);
        if (room == null || room.isExpired()) {
            return this.resolveRoomIssue(room);
        }
        roomManager.closeRoom(roomId);
        return ResponseEntity.ok("Closed chatroom");
    }

    //Countdown timer till room expires
    private String timer(Chatroom room) {
        return "Time remaining:" + room.minutesRemaining() + " minutes" + "\n";
    }

    private ResponseEntity<String> resolveUserIssue(User user) {
        if (user.username.isBlank()) return ResponseEntity.badRequest().body("Invalid username");
        else return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Queue is full. Please try again later.");
    }

    private ResponseEntity<String> resolveRoomIssue(Chatroom room) {
        if (room == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found");
        else return ResponseEntity.status(HttpStatus.GONE).body("This chat has ended");
    }

}