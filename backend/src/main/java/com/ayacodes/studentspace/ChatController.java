package com.ayacodes.studentspace;

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

@CrossOrigin(origins = "https://studentspace.onrender.com")
@RestController
public class ChatController {
    private final Queue<User> waitingUsers = new ConcurrentLinkedQueue<>();
    private final ChatroomManager roomManager;

    public ChatController(ChatroomManager roomManager) {
        this.roomManager = roomManager;
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
                .body(Map.of("error", "Unfamiliar username or badly formatted message"));
    }

    @GetMapping("/chat/{roomId}/end")
    public ResponseEntity<Map<String, String>> endChat(@PathVariable String roomId) throws IOException {
        Optional<ResponseEntity<Map<String, String>>> errorResponse = this.roomIssues(roomId);
        if (errorResponse.isPresent()) return errorResponse.get();

        roomManager.closeRoom(roomId, true);
        this.archiveAndUploadChat(roomId);
        return ResponseEntity.ok(Map.of("message", "Closed chatroom"));
    }

    @PostMapping("/chat/{roomId}/report")
    public ResponseEntity<Map<String, String>> reportAndEndChat(@PathVariable String roomId, @RequestBody String reportReason) throws IOException {
        Optional<ResponseEntity<Map<String, String>>> errorResponse = this.roomIssues(roomId);
        if (errorResponse.isPresent()) return errorResponse.get();

        roomManager.reportAndCloseRoom(roomId, reportReason);
        this.archiveAndUploadChat(roomId);
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

    public void archiveAndUploadChat(String roomId) throws IOException {
        File file = roomManager.generateArchiveLogFile(roomId);
        String accessToken = "sl.u.AF41sI9UWyng9pLZ2_ZI2ehXDe8KG9Xl8oOvvujXWeusr09HVANAFp3c9amJgOYwJrsYnjDFAqz_9fzH4h7spWH479nKFtKn6GYglPGUl5H-pjqdnxcAfjBK2Y4P5JkwAdtlgodKlPHqj_4pzN3c9EaYiJaBx8NQ92hkJ4DdA2O1v6bgxAmjqicpTa9VpKYiD2BKBX8PsFYJth4hzqN00NSBhDDZBUBCQ2NIBJBznHLx9Smd--30-ei1OP60XubpENzTgtUZbcTc-Xlla4PxelCCzNokbH4JSuI3CORNINPgQV3nvbkPcOWbgZyg3Kkf_kl6iPHZ9MFglFU7gLoOKQKlzqJy3YUOOoUdwBzwYEYH5nsVokAxAUc3gSKkuXIwKX7ooRTuPZ5D8FSAc04MVF7WzK2XkvXwL743eFnsJIqroiDX65xGV0i4vs0Uz38pXCfDRZT5NNqpOFBIHgEP28MJk6lG51bXlJDmIYY4rU_mmJNDSD_qzwcB3QGB52sX6fsfTJegw_1HQEpOH6jCBshhiG4qqn5JlYGUgYIsIl4kB4TmszERvLW3BMq-m2twzsYh8xXRoB-_-94gonAikXcfVNK2yKzwx_-5zfZ6taTDvdWmGuo9WnlTLBH6CZ3-dEJdxZKk3c-YCphdu-c3SPxwevpHKhqYzF-3XT5dXdeBTJmnRuYZCo2hJTxUd9RdKWxmEEvxwsTnA3nuEFYzf5_CS-EcnD6pafRalhCnnLzYE6s7kx7Rkv6NRQJivqfINpplwbQy8nyvh9v-8Xl6Z_61qKck3yFjazDe61FKYpdw7EvpdgOko7aEsw4Ge4P8MNhAICyJKgsIkOC91ri9EEliXJkwRKBfNJMBV8CcscAKUvzRhUIfDwcndIPMt5kocELU4WInEz5r_cxer5YLwcl7tdBalQj3yxvtv6LtsaS0KQQF6lJGOE4W_yxRZrB4q_hY8gscILtDKEG2nJl1vt-Xezrgbjwr9zkjmi8X7L2EDzG20e67rwQoPUtTVETgcdJO7GtB3WWQFeLPO7LEslgkcSLad17gqucXnX_aVmV-R_y1MOe_a0etyaZwWGji_XAKSnd0-S1vcPKcYcsSfRQydmr0jW8wo6MrXuln2LrQGp16ddSCRGBj2i31xZvYu4fdL3yQTC_beVhoaIpepbDGey_M_PLFj4dTbSPY-iHeL6-c6Al9s7Ds8o-yYgWP3BUfgCvDPW0N85zLU2msyVIQ8AgvNHqtLPQne14_onke4BxbRa-oFePnUPyD12rsmpnlpOSAyMPj9_VqGzYfYnfS";
        String dropboxUploadUrl = "https://content.dropboxapi.com/2/files/upload";

        HttpPost post = new HttpPost(dropboxUploadUrl);
        post.setHeader("Authorization", "Bearer " + accessToken);
        post.setHeader("Dropbox-API-Arg", "{\"path\": \"/Apps/StudentSpace/" +
                file.getName() + "\",\"mode\": \"add\",\"autorename\": true,\"mute\": false}");
        post.setHeader("Content-Type", "application/octet-stream");

        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] fileBytes = fileInputStream.readAllBytes();
            post.setEntity(new ByteArrayEntity(fileBytes));
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)) {

            int statusCode = response.getStatusLine().getStatusCode();
            String responseBody = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))
                    .lines().collect(Collectors.joining("\n"));

            if (statusCode == 200) {
                System.out.println("Upload successful: " + responseBody);
            } else {
                System.err.println("Upload failed with status: " + statusCode);
                System.err.println("Response: " + responseBody);
            }
            file.delete();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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