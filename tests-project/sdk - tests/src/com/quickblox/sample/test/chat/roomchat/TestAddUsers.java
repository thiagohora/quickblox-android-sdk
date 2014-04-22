package com.quickblox.sample.test.chat.roomchat;

import android.util.Log;

import com.quickblox.module.chat.QBRoomChat;

import org.jivesoftware.smack.AbstractConnectionListener;

import java.util.Arrays;
import java.util.Collection;

public class TestAddUsers extends RoomChatTestCase {

    public void testName() throws Exception {
        String roomName = "new";

        service.addConnectionListener(new AbstractConnectionListener() {
            @Override
            public void connectionClosedOnError(Exception e) {
                super.connectionClosedOnError(e);
            }
        });

        QBRoomChat room = service.getRoomChatManager().createRoom(roomName);
        room.create(true, false);

        Collection<String> hostedRooms = service.getRoomChatManager().getHostedRooms();
        Log.d("asdas3", Arrays.toString(hostedRooms.toArray()));

        room.leave();
        room.destroy();

        hostedRooms = service.getRoomChatManager().getHostedRooms();
        Log.d("asdas3", Arrays.toString(hostedRooms.toArray()));
    }
}
