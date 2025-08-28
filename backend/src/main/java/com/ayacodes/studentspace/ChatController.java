package com.ayacodes.studentspace;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;


import java.util.*;

import java.util.concurrent.ConcurrentLinkedQueue;

import static com.ayacodes.studentspace.RoomStatus.WAITING;

@CrossOrigin(origins = "https://chatspace-iepy.onrender.com")
@RestController
public class ChatController {
    private final Queue<User> waitingUsers = new ConcurrentLinkedQueue<>();
    private final ChatroomManager roomManager;
    private final DropboxUploader dropboxUploader;

    public ChatController(ChatroomManager roomManager, DropboxUploader dropboxUploader) {
        this.roomManager = roomManager;
        this.dropboxUploader = dropboxUploader;
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> matchUserToRoom(@RequestBody User user) {
        Optional<ResponseEntity<Map<String, String>>> errorResponse = resolveUserIssue(user);
        if (errorResponse.isPresent()) return errorResponse.get();

        String roomId = roomManager.findAvailableRoom(user);
        if (roomId == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to assign room"));
        }
        this.waitingUsers.remove(user);
        Map<String, String> response = new HashMap<>();
        if (roomManager.getRoomStatus(roomId).equals(WAITING)) {
            response.put("status", "waiting");
        }
        else response.put("status", "ok");
        response.put("roomId", roomId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{roomId}")
    public ResponseEntity<Map<String, String>> getRoomStatus(@PathVariable String roomId) {
        Optional<ResponseEntity<Map<String, String>>> errorResponse = this.roomIssues(roomId);
        if (errorResponse.isPresent()) return errorResponse.get();
        System.out.println("Room ID: " + roomId + " Status: " + roomManager.getRoomStatus(roomId));
        if (roomManager.getRoomStatus(roomId) == WAITING) {
            return new ResponseEntity<>(Map.of("status", "waiting"), HttpStatus.OK);
        }
        return new ResponseEntity<>(Map.of("status", "ok"), HttpStatus.OK);
    }

    @GetMapping("/chat/{roomId}")
    public ResponseEntity<Map<String, String>> getAllMessages(@PathVariable String roomId) {
        Optional<ResponseEntity<Map<String, String>>> errorResponse = this.roomIssues(roomId);
        if (errorResponse.isPresent()) return errorResponse.get();

        Map<String, String> response = new HashMap<>();
        response.put("messages",
                Objects.requireNonNullElse(roomManager.getMessagesString(roomId),
                        "No messages yet"));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/chat/{roomId}")
    public ResponseEntity<Map<String, String>> sendMessage(@PathVariable String roomId, @RequestBody RawMessage rawMessage) {
        Optional<ResponseEntity<Map<String, String>>> errorResponse = this.roomIssues(roomId);
        if (errorResponse.isPresent()) return errorResponse.get();
        System.out.println("Received message: " + rawMessage);
        if (roomManager.addMessage(roomId, rawMessage)) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Message sent");
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest()
                .body(Map.of("error", "Bad Message"));
    }

    @GetMapping("/chat/{roomId}/end")
    public ResponseEntity<Map<String, String>> endChat(@PathVariable String roomId) throws IOException {
        Optional<ResponseEntity<Map<String, String>>> errorResponse = this.roomIssues(roomId);
        if (errorResponse.isPresent()) return errorResponse.get();

        roomManager.closeRoom(roomId, true);
        File fileToUpload = roomManager.generateArchiveLogFile(roomId);
        dropboxUploader.archiveAndUploadChat(fileToUpload);
        return ResponseEntity.ok(Map.of("message", "Closed chatroom"));
    }

    @PostMapping("/chat/{roomId}/report")
    public ResponseEntity<Map<String, String>> reportAndEndChat(@PathVariable String roomId, @RequestBody String reportReason) throws IOException {
        Optional<ResponseEntity<Map<String, String>>> errorResponse = this.roomIssues(roomId);
        if (errorResponse.isPresent()) return errorResponse.get();

        roomManager.reportAndCloseRoom(roomId, reportReason);
        File fileToUpload = roomManager.generateArchiveLogFile(roomId);
        dropboxUploader.archiveAndUploadChat(fileToUpload);
        return ResponseEntity.ok(Map.of("message", "Submitted report and closed chatroom"));
    }

    //Returns time of room expiration
    @GetMapping("/chat/{roomId}/expiry")
    private ResponseEntity<Map<String, String>> getExpirationTime(@PathVariable String roomId) {
        Optional<ResponseEntity<Map<String, String>>> errorResponse = this.roomIssues(roomId);
        if (errorResponse.isPresent()) return errorResponse.get();
        if (roomManager.getRoomStatus(roomId).equals(WAITING)) {
            return ResponseEntity.ok(Map.of("status", "waiting"));
        }
        return ResponseEntity.ok(Map.of("expiry", roomManager.expirationTime(roomId)));
    }


    private Optional<ResponseEntity<Map<String, String>>> resolveUserIssue(User user) {
        if (user.username.isBlank()) {
            return Optional.of(ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid username")));
        }
        if (!this.waitingUsers.add(user)) {
            return Optional.of(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error", "Queue is full. Please try again later.")));
        }
        return Optional.empty();
    }

    private Optional<ResponseEntity<Map<String, String>>> roomIssues(String roomId) {
        RoomStatus status = roomManager.getRoomStatus(roomId);
        return switch (status) {
            case OK, WAITING -> Optional.empty();
            case CLOSED -> Optional.of(ResponseEntity.status(HttpStatus.GONE)
                    .body(Map.of("error", "This chat has ended")));
            case NOT_FOUND -> Optional.of(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Room not found")));
        };
    }
}