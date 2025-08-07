package com.ayacodes.studentspace;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;


public class ChatroomMessagingTests {
    private Chatroom room;

    @BeforeEach
    void setUp() {
        ChatroomManager manager = new ChatroomManager();
        User userAlice = new User();
        userAlice.setUsername("alice");
        userAlice.setTopic(Topic.FRIENDSHIP);
        room = manager.createRoom(userAlice);
        User userBob = new User();
        userBob.setUsername("bob");
        userBob.setTopic(Topic.FRIENDSHIP);
        assertTrue(room.addUser(userBob));
    }

    @Test
    void messageSendingSuccessful() {
        Message messageFromAlice = new Message("alice","first successful message", Instant.now());
        assertTrue(room.addMessage(messageFromAlice));
        assertEquals(1, room.messages.size());

        Message messageFromBob = new Message("bob","second successful message", Instant.now());
        assertTrue(room.addMessage(messageFromBob));
        assertEquals(2, room.messages.size());
    }


    @Test
    void messageFromUnknownUserFail() {
        Message messageFromUnknownUser = new Message("notAlice","hello",Instant.now());
        assertFalse(room.addMessage(messageFromUnknownUser));
        assertEquals(0, room.messages.size());
    }


    @Test
    void messageFromEmptyOrBlankUserFail() {
        Message messageFromEmptyUsername = new Message("","hello",Instant.now());
        assertFalse(room.addMessage(messageFromEmptyUsername));
        assertEquals(0, room.messages.size());

        Message messageFromBlankUsername = new Message("     ","hello",Instant.now());
        assertFalse(room.addMessage(messageFromBlankUsername));
        assertEquals(0, room.messages.size());
    }


    @Test
    void emptyOrBlankMessageFail() {
        Message blankMessageFromBob = new Message("bob","     ",Instant.now());//blank message should fail
        assertFalse(room.addMessage(blankMessageFromBob));
        assertEquals(0, room.messages.size());

        Message emptyMessageFromBob = new Message("bob","",Instant.now());//empty message should fail
        assertFalse(room.addMessage(emptyMessageFromBob));
        assertEquals(0, room.messages.size());
    }

    @Test
    void checkMessageLog() {
        Message aliceMessage1 = new Message("alice","1", Instant.now());
        room.addMessage(aliceMessage1);
        Message bobMessage1 = new Message("bob","2", Instant.now());
        room.addMessage(bobMessage1);
        Message aliceMessage2 = new Message("alice","3", Instant.now());
        room.addMessage(aliceMessage2);
        Message bobMessage2 = new Message("bob","4", Instant.now());
        room.addMessage(bobMessage2);
        String messagesString = room.getMessageString();
        System.out.println(messagesString);
        String expectedMessageString = """
                alice: 1
                bob: 2
                alice: 3
                bob: 4
                """;
        assertEquals(expectedMessageString, messagesString);
    }
}