package com.ayacodes.studentspace.backend;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@RestController
public class ChatController {
    private final Queue<User> waitingUsers = new ConcurrentLinkedQueue<>();
    private final ChatroomManager roomManager;

    public ChatController(ChatroomManager roomManager) {
        this.roomManager = roomManager;
    }

    @PostMapping ("/start")
    public ResponseEntity<String> matchUserToRoom(@RequestBody User user) {
        Optional<ResponseEntity<String>> errorResponse = this.resolveUserIssue(user);
        if (errorResponse.isPresent()) return errorResponse.get();
        String roomId = roomManager.findAvailableRoom(user);
        this.waitingUsers.remove(user);
        return ResponseEntity.ok(roomId);
    }

    @GetMapping("/chat/{roomId}")
    public ResponseEntity<String> getAllMessages(@PathVariable String roomId) {
        Optional<ResponseEntity<String>> errorResponse = this.roomIssues(roomId);
        if (errorResponse.isPresent()) return errorResponse.get();
        String messagesString = roomManager.getMessagesString(roomId);
        return ResponseEntity.ok(this.timer(roomId) +
                Objects.requireNonNullElse(messagesString, "No messages yet"));
    }

    @PostMapping("/chat/{roomId}")
    public ResponseEntity<String> sendMessage(@PathVariable String roomId, @RequestBody RawMessage rawMessage) {
        Optional<ResponseEntity<String>> errorResponse = this.roomIssues(roomId);
        if (errorResponse.isPresent()) return errorResponse.get();
        if (roomManager.addMessage(roomId, rawMessage)) {
            return ResponseEntity.ok(this.timer(roomId) + "Message sent");
        }
        return ResponseEntity.badRequest().body("Unfamiliar username or badly formatted message");
    }

    @GetMapping("/chat/{roomId}/timer")
    public ResponseEntity<String> getTimeRemaining(@PathVariable String roomId) {
        Optional<ResponseEntity<String>> errorResponse = this.roomIssues(roomId);
        return errorResponse.orElseGet(() -> ResponseEntity.ok(this.timer(roomId)));
    }

    @GetMapping("/chat/{roomId}/end")
    public ResponseEntity<String> endChat(@PathVariable String roomId) {
        Optional<ResponseEntity<String>> errorResponse = this.roomIssues(roomId);
        if (errorResponse.isPresent()) return errorResponse.orElse(null);
        roomManager.closeRoom(roomId, true);
        return ResponseEntity.ok("Closed chatroom");
    }

    @PostMapping("/chat/{roomId}/report")
    public ResponseEntity<String> reportAndEndChat(@PathVariable String roomId, @RequestBody String reportReason) {
        Optional<ResponseEntity<String>> errorResponse = this.roomIssues(roomId);
        if (errorResponse.isPresent()) return errorResponse.orElse(null);
        roomManager.reportAndCloseRoom(roomId, reportReason);
        return ResponseEntity.ok("Submitted report and closed chatroom");
    }

    //Countdown timer till room expires
    private String timer(String roomId) {
        return "Time remaining:" + roomManager.minutesRemaining(roomId) + " minutes" + "\n";
    }

    private Optional<ResponseEntity<String>> resolveUserIssue(User user) {
        if (user.username.isBlank()) {
            return Optional.of(ResponseEntity.badRequest().body("Invalid username"));
        }
        if (!this.waitingUsers.add(user)) {
            return Optional.of(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Queue is full. Please try again later."));
        }
        return Optional.empty();
    }

    private Optional<ResponseEntity<String>> roomIssues(String roomId) {
        RoomStatus status = roomManager.getRoomStatus(roomId);
        return switch (status) {
            case OK ->
                    Optional.empty();
            case CLOSED ->
                    Optional.of(ResponseEntity.status(HttpStatus.GONE).body("This chat has ended"));
            case NOT_FOUND ->
                    Optional.of(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Room not found"));
        };
    }
}