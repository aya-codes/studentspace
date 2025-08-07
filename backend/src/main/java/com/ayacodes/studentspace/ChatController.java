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
        String accessToken = "sl.u.AF4ibCisqV7LPhpXpA0FCExgWcy8WWlrhABPwrhxRGhjiVPbxVEfSZdlWbcER54m-3smpsI4MnT5Qaontqqucja2ws8mKh8zY3A33yZ2mQErm1P-IfO5D3t6y-Prqpnv6soU1g8fI87LuHrJC_pQpM0zwxnxMKzdre5880ZE8Qwy8CWsASbeA96oizAsDtLP3hAu-bqiYXtovAl9P5zaZmid-IQ_tThms8C3VSNuCxTef91nU1rDJOc__yPHrmcSWlbKy_v5W41str5Z25BQVDvQusXxVnE0AFeGvODUw3vkbErg8uTQ6NmGFdPBGgCe4MK17TN9QuQRQPv8ipwcb3EGrhyOhBKoNKNFDpSpIrkDshplEpVru9gtVMlQv3JjnlnuNxJrA3zySlwnF_AYSZIAHSk1V_uVXXAany0pgKUVpFIZI1t9CAaxygwFiCdLvBZfrG7Bjr8xkmvotAQMrVseyhnLZ0nhYvdNcVvyi3d5UXEUoSESfkXCV6esonYP8Lw6XMWfHU84g-V7qgBdlI0U5vZV3Dpn3LSC6Yj57ApMlrgtKQ6U7-sW-EqV-2yKNwUf18MnedzKjjMImph-sOmXumcBrH_zz6kQmGf_5MC46Ny46D5andsaEKUTLYi599gkFjCNKQJRLjp3fZR82YKI6NKbd_96_IMy5c0xVUnH6Z_vFcuHUxGAghMiSjAJSYsvjzVrBcvABR3S_wbUAIbAN8wDwC77DYG3wdC8p5n_l7DF6xSWXIa_o_xQ6nr2UNs1I4anTL4MeDZFPLZxw500VvPJTtbLSliKRQMTFdA7Oskwcl0MLaF-a9pU9WEVTOM5urO_78G9BYXQLwavNFmNCb09ovhgzRPRyE4UFHmtGFC5WL1j-GX014sN0Qknzt-RwRWJn4_GrUK1sJ27XcRUNpg6cJIm5iQccNfgTmtu_WV_WDyFgiziSJMKkWXlUtbGbcSkmWZcNGsD49QkzqCJsc-XXRdu87uoTfmTRXmAT5dwmkoeBNrGYpVkr4j7AlAHjCpMU3L7-CEkyTD7KRUjoR960CG5vL8afL2d0_4cf_z2jQpbpCeVTYOB6BofFoMtNP5rcnXf30hH9LFj7b_gH2MT3XcZkh3hvQQSlHC6eHxxxm1E5Hysyku3UNXSNegGx2J_yF-Okz-EKUxll79nhTRFJ2MCwvzlOmOi6_CtlYv4I-7UlU68t0dRPnasXkxXLzCKPQdRFD1EXDyW-k25cp9Becz1-ZHF_8c3APmOQR_a25c2utiBWZAxAUFUeGsCxwzpRhs3ld825jdfRDKf";
        String dropboxUploadUrl = "https://content.dropboxapi.com/2/files/upload";

        HttpPost post = new HttpPost(dropboxUploadUrl);
        post.setHeader("Authorization", "Bearer " + accessToken);
        post.setHeader("Dropbox-API-Arg", "{\"path\": \"/chat_archives/" +
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