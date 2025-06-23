package com.ayacodes.studentspace;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatroomCreationTests {

    @Test
    void addUserToRoomSuccessful() {
        ChatroomManager manager = new ChatroomManager();
        User userAlice = new User();
        userAlice.username = "alice";
        userAlice.topic = Topic.FRIENDS;
        Chatroom room = manager.createRoom(userAlice);
        assertFalse(room.atCapacity);
        assertTrue(room.isAvailable());
        assertEquals(1, room.getUsers().size());
        assertEquals("alice", room.getUsers().get(0).getUsername());
    }

    @Test
    void topicMismatchAddUserFail() {
        ChatroomManager manager = new ChatroomManager();
        User userAlice = new User();
        userAlice.username = "alice";
        userAlice.topic = Topic.FRIENDS;
        Chatroom room = manager.createRoom(userAlice);
        assertEquals(1, room.getUsers().size());
        assertEquals("alice", room.getUsers().get(0).getUsername());

        User userBob = new User();
        userBob.username = "bob";
        userBob.topic = Topic.STRESS;
        assertFalse(room.addUser(userBob));
        assertFalse(room.atCapacity);
        assertTrue(room.isAvailable());
        assertEquals(1, room.getUsers().size());
        assertEquals("alice", room.getUsers().get(0).getUsername());

        userBob.topic = Topic.FRIENDS;
        assertTrue(room.addUser(userBob));
        assertTrue(room.atCapacity);
        assertFalse(room.isAvailable());
        assertEquals(2, room.getUsers().size());
    }

    @Test
    void atCapacityAddUserFail() {
        ChatroomManager manager = new ChatroomManager();
        User userAlice = new User();
        userAlice.username = "alice";
        userAlice.topic = Topic.FRIENDS;
        Chatroom room = manager.createRoom(userAlice);
        assertEquals(1, room.getUsers().size());
        assertEquals("alice", room.getUsers().get(0).getUsername());

        User userBob = new User();
        userBob.username = "bob";
        userBob.topic = Topic.FRIENDS;
        assertTrue(room.addUser(userBob));
        assertEquals(2, room.getUsers().size());
        assertTrue(room.atCapacity);

        User userTom = new User();
        userTom.username = "tom";
        userTom.topic = Topic.FRIENDS;
        assertFalse(room.addUser(userTom));
        assertEquals(2, room.getUsers().size());
    }
}
