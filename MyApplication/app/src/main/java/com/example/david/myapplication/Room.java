package com.example.david.myapplication;

import java.util.ArrayList;
import java.util.List;

public class Room {

    public static final String CHILD_NAME = "rooms";
    public static final String PUBLIC_ROOM_ID = "public_room_id";

    private List<Message> messages = new ArrayList<>();

    public Room() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void addMessages() {
        this.messages = messages;
    }
}
