package com.quickblox.sample.test.chat.roomchat;

import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.module.chat.QBRoomChat;

import org.jivesoftware.smack.XMPPException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestCreateRoom extends RoomChatTestCase {

    private QBRoomChat roomChat;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        roomChat = service.getRoomChatManager().createRoom(ROOM_NAME);
    }

    @Override
    protected void tearDown() throws Exception {
        roomChat.destroy();
        super.tearDown();
    }

    public void testMembersOnlyPersistentRoomSync() throws Exception {
        roomChat.create(true, true);
        assertEquals(containsRoom(service, ROOM_NAME), true);
    }

    public void testMembersOnlyPersistentRoomAsync() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        roomChat.create(true, true, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                signal.countDown();
            }
        });
        signal.await(ROOM_CREATION_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(containsRoom(service, ROOM_NAME), true);
    }

    public void testPublicPersistentRoomSync() throws Exception {
        roomChat.create(false, true);
        assertEquals(containsRoom(service, ROOM_NAME), true);
    }

    public void testPublicPersistentRoomAsync() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        roomChat.create(false, true, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                signal.countDown();
            }
        });
        signal.await(ROOM_CREATION_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(containsRoom(service, ROOM_NAME), true);
    }

    public void testMembersOnlyTemporaryRoomSync() throws Exception {
        roomChat.create(true, false);
        assertEquals(containsRoom(service, ROOM_NAME), true);
    }

    public void testMembersOnlyTemporaryRoomAsync() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        roomChat.create(true, false, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                signal.countDown();
            }
        });
        signal.await(ROOM_CREATION_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(containsRoom(service, ROOM_NAME), true);
    }

    public void testPublicTemporaryRoomSync() throws Exception {
        roomChat.create(false, false);
        assertEquals(containsRoom(service, ROOM_NAME), true);
    }

    public void testPublicTemporaryRoomAsync() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        roomChat.create(false, false, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                signal.countDown();
            }
        });
        signal.await(ROOM_CREATION_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(containsRoom(service, ROOM_NAME), true);
    }

    public void testCreateTwice() throws Exception {
        roomChat.create(true, true);
        roomChat.leave();
        try {
            roomChat.create(true, true);
        } catch (XMPPException e) {
            assertEquals(e.getMessage(), "Creation failed - Missing acknowledge of room creation.");
        }
    }

    public void testNullRoomName() throws Exception {
        try {
            service.getRoomChatManager().createRoom(null);
            fail("Missed exception");
        } catch (NullPointerException e) {
            // Ok
        }
    }

    public void testEmptyRoomName() throws Exception {
        try {
            service.getRoomChatManager().createRoom("");
            fail("Missed exception");
        } catch (IllegalArgumentException e) {
            // Ok
        }
    }
}
