package com.quickblox.sample.test.chat.roomchat;

import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.QBRoomChat;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.sample.test.BaseTestCase;
import com.quickblox.sample.test.TestConfig;

import org.jivesoftware.smack.XMPPException;

import java.util.Collection;

public class RoomChatTestCase extends BaseTestCase {

    protected static final String ROOM_NAME = "autotest";
    protected static final long ROOM_CREATION_TIMEOUT = 15;

    protected QBChatService service;
    protected QBUser user;
    protected boolean testPassed;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        user = new QBUser(TestConfig.USER_LOGIN, TestConfig.USER_PASSWORD);
        user.setId(TestConfig.USER_ID);
        QBChatService.init(context);

        service = QBChatService.getInstance();
        service.login(user);

        // removeAllRooms(service);

        testPassed = false;
    }

    @Override
    protected void tearDown() throws Exception {
        service.logout();
        service.destroy();
        super.tearDown();
    }
/*
    protected boolean containsRoom(QBChatService service, String room) throws XMPPException {
        Collection<String> roomNames = service.getRoomChatManager().getHostedRooms();
        for (String roomName : roomNames) {
            if (roomName.equals(room)) {
                return true;
            }
        }
        return false;
    }

    protected void removeAllRooms(QBChatService service) throws XMPPException {
        Collection<String> hostedRooms = service.getRoomChatManager().getHostedRooms();
        for (String hostedRoom : hostedRooms) {
            QBRoomChat room = service.getRoomChatManager().createRoom(hostedRoom);
            room.join();
            room.destroy();
        }
    }
    */
}

