package com.ayacodes.studentspace;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChatroomCreationTests {

    @Test
    void addUserToRoomSuccessful() {
        ChatroomManager manager = new ChatroomManager();
        User userAlice = new User();
        userAlice.setUsername("alice");
        userAlice.setTopic(Topic.FRIENDSHIP);
        Chatroom room = manager.createRoom(userAlice);
        assertFalse(room.getAtCapacity());
        assertTrue(room.isAvailable());
        assertEquals(1, room.getUsers().size());
        assertEquals("alice", room.getUsers().get(0).getUsername());
    }

    @Test
    void topicMismatchAddUserFail() {
        ChatroomManager manager = new ChatroomManager();
        User userAlice = new User();
        userAlice.setUsername("alice");
        userAlice.setTopic(Topic.FRIENDSHIP);
        Chatroom room = manager.createRoom(userAlice);
        assertEquals(1, room.getUsers().size());
        assertEquals("alice", room.getUsers().get(0).getUsername());

        User userBob = new User();
        userBob.setUsername("bob");
        userBob.setTopic(Topic.STRESS);
        assertFalse(room.addUser(userBob));
        assertFalse(room.getAtCapacity());
        assertTrue(room.isAvailable());
        assertEquals(1, room.getUsers().size());
        assertEquals("alice", room.getUsers().get(0).getUsername());

        userBob.setTopic(Topic.FRIENDSHIP);
        assertTrue(room.addUser(userBob));
        assertTrue(room.getAtCapacity());
        assertFalse(room.isAvailable());
        assertEquals(2, room.getUsers().size());
    }

    @Test
    void atCapacityAddUserFail() {
        ChatroomManager manager = new ChatroomManager();
        User userAlice = new User();
        userAlice.setUsername("alice");
        userAlice.setTopic(Topic.FRIENDSHIP);
        Chatroom room = manager.createRoom(userAlice);
        assertEquals(1, room.getUsers().size());
        assertEquals("alice", room.getUsers().get(0).getUsername());

        User userBob = new User();
        userBob.setUsername("bob");
        userBob.setTopic(Topic.FRIENDSHIP);
        assertTrue(room.addUser(userBob));
        assertEquals(2, room.getUsers().size());
        assertTrue(room.getAtCapacity());

        User userTom = new User();
        userTom.setUsername("tom");
        userTom.setTopic(Topic.FRIENDSHIP);
        assertFalse(room.addUser(userTom));
        assertEquals(2, room.getUsers().size());
    }
}
