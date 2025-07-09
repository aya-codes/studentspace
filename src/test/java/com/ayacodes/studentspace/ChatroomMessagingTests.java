package com.ayacodes.studentspace;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;


public class ChatroomMessagingTests {

    @Test
    void messageSendingSuccessful() {
        ChatroomManager manager = new ChatroomManager();
        User userAlice = new User();
        userAlice.username = "alice";
        userAlice.topic = Topic.FRIENDSHIP;
        Chatroom room = manager.createRoom(userAlice);
        User userBob = new User();
        userBob.username = "bob";
        userBob.topic = Topic.FRIENDSHIP;
        assertTrue(room.addUser(userBob));

        Message messageFromAlice = new Message("alice","first successful message", Instant.now());
        assertTrue(room.addMessage(messageFromAlice));
        assertEquals(1, room.messages.size());

        Message messageFromBob = new Message("bob","second successful message", Instant.now());
        assertTrue(room.addMessage(messageFromBob));
        assertEquals(2, room.messages.size());
    }


    @Test
    void messageFromUnknownUserFail() {
        ChatroomManager manager = new ChatroomManager();
        User userAlice = new User();
        userAlice.username = "alice";
        userAlice.topic = Topic.FRIENDSHIP;
        Chatroom room = manager.createRoom(userAlice);
        User userBob = new User();
        userBob.username = "bob";
        userBob.topic = Topic.FRIENDSHIP;
        assertTrue(room.addUser(userBob));

        Message messageFromUnknownUser = new Message("notAlice","hello",Instant.now());
        assertFalse(room.addMessage(messageFromUnknownUser));
        assertEquals(0, room.messages.size());

    }


    @Test
    void messageFromEmptyOrBlankUserFail() {
        ChatroomManager manager = new ChatroomManager();
        User userAlice = new User();
        userAlice.username = "alice";
        userAlice.topic = Topic.FRIENDSHIP;
        Chatroom room = manager.createRoom(userAlice);
        User userBob = new User();
        userBob.username = "bob";
        userBob.topic = Topic.FRIENDSHIP;
        assertTrue(room.addUser(userBob));

        Message messageFromEmptyUsername = new Message("","hello",Instant.now());
        assertFalse(room.addMessage(messageFromEmptyUsername));
        assertEquals(0, room.messages.size());

        Message messageFromBlankUsername = new Message("     ","hello",Instant.now());
        assertFalse(room.addMessage(messageFromBlankUsername));
        assertEquals(0, room.messages.size());
    }


    @Test
    void emptyOrBlankMessageFail() {
        ChatroomManager manager = new ChatroomManager();
        User userAlice = new User();
        userAlice.username = "alice";
        userAlice.topic = Topic.FRIENDSHIP;
        Chatroom room = manager.createRoom(userAlice);
        User userBob = new User();
        userBob.username = "bob";
        userBob.topic = Topic.FRIENDSHIP;
        assertTrue(room.addUser(userBob));

        Message blankMessageFromBob = new Message("bob","     ",Instant.now());//blank message should fail
        assertFalse(room.addMessage(blankMessageFromBob));
        assertEquals(0, room.messages.size());

        Message emptyMessageFromBob = new Message("bob","",Instant.now());//empty message should fail
        assertFalse(room.addMessage(emptyMessageFromBob));
        assertEquals(0, room.messages.size());
    }
}
