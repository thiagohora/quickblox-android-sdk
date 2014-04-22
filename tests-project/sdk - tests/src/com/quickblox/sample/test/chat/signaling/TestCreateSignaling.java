package com.quickblox.sample.test.chat.signaling;

import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.QBPrivateChat;
import com.quickblox.module.chat.QBPrivateChatManager;
import com.quickblox.module.chat.QBSignaling;
import com.quickblox.module.chat.QBSignalingManager;
import com.quickblox.module.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.module.chat.listeners.QBSignalingManagerListener;
import com.quickblox.module.chat.utils.QBChatUtils;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.sample.test.BaseTestCase;
import com.quickblox.sample.test.TestConfig;
import com.quickblox.sample.test.faker.ChatServiceFaker;

import org.jivesoftware.smack.packet.Message;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestCreateSignaling extends SignalingTestCase {

    private static final long SIGNALING_CREATION_TIMEOUT = 10;

    public void testCreateLocally() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        QBSignalingManager manager = service.getSignalingManager();

        manager.addSignalingManagerListener(new QBSignalingManagerListener() {

            @Override
            public void signalingCreated(QBSignaling signaling, boolean createdLocally) {
                assertEquals(true, createdLocally);
                final int participantId = participant.getId();
                assertEquals(participantId, signaling.getParticipant());
                signal.countDown();
            }
        });

        QBSignaling signaling = manager.createSignaling(participant.getId(), null);

        signal.await(SIGNALING_CREATION_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(manager.getSignaling(participant.getId()), signaling);
    }

    public void testCreateOnIncomingMessage() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        QBSignalingManager manager = service.getSignalingManager();

        manager.addSignalingManagerListener(new QBSignalingManagerListener() {

            @Override
            public void signalingCreated(QBSignaling signaling, boolean createdLocally) {
                assertEquals(false, createdLocally);
                final int participantId = participant.getId();
                assertEquals(participantId, signaling.getParticipant());

                testPassed = true;

                signal.countDown();
            }
        });

        QBSignaling signaling = serviceFaker.getSignalingManager().createSignaling(user.getId(), null);
        final String message = "test";
        signaling.sendMessage(message);

        signal.await(SIGNALING_CREATION_TIMEOUT, TimeUnit.SECONDS);
        assertNotNull(manager.getSignaling(participant.getId()));
        assertEquals(true, testPassed);
    }

    public void testCreateChatTwice() throws Exception {
        QBPrivateChatManager manager = service.getPrivateChatManager();
        QBPrivateChat firstChat = manager.createChat(participant.getId(), null);
        QBPrivateChat secondChat = manager.createChat(participant.getId(), null);
        assertNotSame(firstChat, secondChat);
    }

    public void testAddNullManagerListener() throws Exception {
        service.getPrivateChatManager().addPrivateChatManagerListener(null);
        testCreateOnIncomingMessage();
    }
}
