package com.ayacodes.studentspace;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class ChatroomMessagingTests {

    @Test
    void messageSendingSuccessful() {
        ChatroomManager manager = new ChatroomManager();
        User userAlice = new User();
        userAlice.username = "alice";
        userAlice.topic = Topic.FRIENDS;
        Chatroom room = manager.createRoom(userAlice);
        User userBob = new User();
        userBob.username = "bob";
        userBob.topic = Topic.FRIENDS;
        assertTrue(room.addUser(userBob));

        Message messageFromAlice = new Message();
        messageFromAlice.username = "alice";
        messageFromAlice.body = "first successful message";
        assertTrue(room.addMessage(messageFromAlice));
        assertEquals(1, room.messages.size());

        Message messageFromBob = new Message();
        messageFromBob.username = "bob";
        messageFromBob.body = "second successful message";
        assertTrue(room.addMessage(messageFromBob));
        assertEquals(2, room.messages.size());
    }


    @Test
    void messageFromUnknownUserFail() {
        ChatroomManager manager = new ChatroomManager();
        User userAlice = new User();
        userAlice.username = "alice";
        userAlice.topic = Topic.FRIENDS;
        Chatroom room = manager.createRoom(userAlice);
        User userBob = new User();
        userBob.username = "bob";
        userBob.topic = Topic.FRIENDS;
        assertTrue(room.addUser(userBob));

        Message messageFromUnknownUser = new Message();
        messageFromUnknownUser.username = "notAlice";
        messageFromUnknownUser.body = "hello";
        assertFalse(room.addMessage(messageFromUnknownUser));
        assertEquals(0, room.messages.size());

    }


    @Test
    void messageFromEmptyOrBlankUserFail() {
        ChatroomManager manager = new ChatroomManager();
        User userAlice = new User();
        userAlice.username = "alice";
        userAlice.topic = Topic.FRIENDS;
        Chatroom room = manager.createRoom(userAlice);
        User userBob = new User();
        userBob.username = "bob";
        userBob.topic = Topic.FRIENDS;
        assertTrue(room.addUser(userBob));

        Message messageFromEmptyUsername = new Message();
        messageFromEmptyUsername.username = "";
        messageFromEmptyUsername.body = "hello";
        assertFalse(room.addMessage(messageFromEmptyUsername));
        assertEquals(0, room.messages.size());

        Message messageFromBlankUsername = new Message();
        messageFromBlankUsername.username = "     ";
        messageFromBlankUsername.body = "hello";
        assertFalse(room.addMessage(messageFromBlankUsername));
        assertEquals(0, room.messages.size());
    }


    @Test
    void emptyOrBlankMessageFail() {
        ChatroomManager manager = new ChatroomManager();
        User userAlice = new User();
        userAlice.username = "alice";
        userAlice.topic = Topic.FRIENDS;
        Chatroom room = manager.createRoom(userAlice);
        User userBob = new User();
        userBob.username = "bob";
        userBob.topic = Topic.FRIENDS;
        assertTrue(room.addUser(userBob));

        Message blankMessageFromBob = new Message();
        blankMessageFromBob.username = "bob";
        blankMessageFromBob.body = "       "; //blank message should fail
        assertFalse(room.addMessage(blankMessageFromBob));
        assertEquals(0, room.messages.size());

        Message emptyMessageFromBob = new Message();
        emptyMessageFromBob.username = "bob";
        emptyMessageFromBob.body = ""; //empty message should fail
        assertFalse(room.addMessage(emptyMessageFromBob));
        assertEquals(0, room.messages.size());
    }
}
