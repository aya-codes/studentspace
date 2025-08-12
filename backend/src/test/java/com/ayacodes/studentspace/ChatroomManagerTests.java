package com.ayacodes.studentspace;

import org.junit.jupiter.api.Test;
import java.io.*;
import java.time.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ChatroomManagerTests {

    @Test
    public void testGenerateArchiveLogFile() throws IOException {
        ChatroomManager manager = new ChatroomManager();

        // Create a sample archived chatroom
        ArchivedChatroom archived = new ArchivedChatroom(
                "room1",
                Topic.TEST,
                Duration.ofMinutes(30),
                Instant.parse("2025-07-31T10:00:00Z"),
                Instant.parse("2025-07-31T10:25:00Z"),
                new ArrayList<Message>(),
                2,
                12,
                true,
                true,
                Optional.of("Inappropriate behavior")
        );

        // Add to manager
        manager.getArchivedRooms().put("room1", archived);

        // Generate log file
        File logFile = manager.generateArchiveLogFile(archived.roomId());

        assertTrue(logFile.exists(), "Log file should be created");

        String content = new String(java.nio.file.Files.readAllBytes(logFile.toPath()));
        assertTrue(content.contains("room1"));
        assertTrue(content.contains("TEST"));
        assertTrue(content.contains("Final Message Count: 12"));
        assertTrue(content.contains("Report Reason: Inappropriate behavior"));
    }
}

