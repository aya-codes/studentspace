package com.ayacodes.studentspace.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ArchivedChatroomTests {
    private Chatroom room;
    private ChatroomManager manager;

    @BeforeEach
    void setUp() {
        manager = new ChatroomManager();
        User userAlice = new User();
        userAlice.setUsername("alice");
        userAlice.setTopic(Topic.FRIENDSHIP);
        room = manager.createRoom(userAlice);
        User userBob = new User();
        userBob.setUsername("bob");
        userBob.setTopic(Topic.FRIENDSHIP);
        assertTrue(room.addUser(userBob));
        Message aliceMessage1 = new Message("alice","1", Instant.now());
        room.addMessage(aliceMessage1);
        Message bobMessage1 = new Message("bob","2", Instant.now());
        room.addMessage(bobMessage1);
        Message aliceMessage2 = new Message("alice","3", Instant.now());
        room.addMessage(aliceMessage2);
        Message bobMessage2 = new Message("bob","4", Instant.now());
        room.addMessage(bobMessage2);
    }

    @Test
    void checkArchive() {
        ArchivedChatroom archive = manager.closeRoom(room.roomId, true);
        assertTrue(archive.closedByUser());
        assertEquals(4, archive.finalMessageCount());
        assertFalse(archive.reportSubmitted());
        assertNull(archive.reportReason());
    }

    @Test
    void checkReportAndArchive() {
        String reportReason = "report reason";
        ArchivedChatroom archive = manager.reportAndCloseRoom(room.roomId, reportReason);
        assertTrue(archive.closedByUser());
        assertEquals(4, archive.finalMessageCount());
        assertTrue(archive.reportSubmitted());
        assertTrue(archive.reportReason().isPresent());
        assertEquals(reportReason, archive.reportReason().get());
    }
}
