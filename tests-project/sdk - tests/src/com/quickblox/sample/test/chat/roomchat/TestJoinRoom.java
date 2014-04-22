package com.quickblox.sample.test.chat.roomchat;

import com.quickblox.module.chat.QBRoomChat;

public class TestJoinRoom extends RoomChatTestCase {

    public static final String NONEXISTENT_ROOM_NAME = "nonexistent room";

    public void testJoinNonExistentRoom() throws Exception {
        QBRoomChat room = service.getRoomChatManager().createRoom(NONEXISTENT_ROOM_NAME);
        room.join();
        assertEquals(false, room.isJoined());
    }

    public void testJoinAfterCreation() throws Exception {
        QBRoomChat room = service.getRoomChatManager().createRoom(ROOM_NAME);
        room.create(true, false);
        assertEquals(true, room.isJoined());
        room.leave();
    }
}
