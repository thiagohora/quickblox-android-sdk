package com.quickblox.sample.test.chat.roomchat;

import com.quickblox.module.chat.QBRoomChat;
import com.quickblox.module.chat.QBRoomChatManager;

import java.util.Collection;

public class TestHostedRooms extends RoomChatTestCase {

    public void testGetHostedRooms() throws Exception {
        QBRoomChatManager manager = service.getRoomChatManager();

        Collection<String> hostedRooms = manager.getHostedRooms();
        int initialSize = hostedRooms.size();

        QBRoomChat room = manager.createRoom("room2");
        room.create(false, true);

        hostedRooms = manager.getHostedRooms();
        int finalSize = hostedRooms.size();

        assertEquals(initialSize, finalSize - 1);

        room.destroy();
    }
}
