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

public class TestCreateSignaling extends BaseTestCase {

    private static final long SIGNALING_CREATION_TIMEOUT = 10;

    private QBChatService service;
    private QBChatService serviceFaker;

    private QBUser user;
    private QBUser participant;

    private boolean testPassed;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        user = new QBUser(TestConfig.USER_LOGIN, TestConfig.USER_PASSWORD);
        user.setId(TestConfig.USER_ID);
        participant = new QBUser(TestConfig.PARTICIPANT_NAME, TestConfig.PARTICIPANT_PASSWORD);
        participant.setId(TestConfig.PARTICIPANT_ID);
        QBChatService.init(context);

        service = QBChatService.getInstance();
        service.login(user);

        serviceFaker = ChatServiceFaker.newInstance();
        serviceFaker.login(participant);

        testPassed = false;
    }

    @Override
    protected void tearDown() throws Exception {
        service.logout();
        serviceFaker.logout();
        service.destroy();
        super.tearDown();
    }

    public void testCreateLocally() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        QBSignalingManager manager = service.getSignalingManager();

        manager.addSignalingManagerListener(new QBSignalingManagerListener() {

            @Override
            public void signalingCreated(QBSignaling signaling, boolean createdLocally) {
                assertEquals(createdLocally, true);
                final int participantId = participant.getId();
                assertEquals(signaling.getParticipant(), participantId);
                signal.countDown();
            }
        });

        QBSignaling signaling = manager.createSignaling(participant.getId(), null);

        signal.await(SIGNALING_CREATION_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(signaling, manager.getSignaling(participant.getId()));
    }

    public void testCreateOnIncomingMessage() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        QBSignalingManager manager = service.getSignalingManager();

        manager.addSignalingManagerListener(new QBSignalingManagerListener() {

            @Override
            public void signalingCreated(QBSignaling signaling, boolean createdLocally) {
                assertEquals(createdLocally, false);
                final int participantId = participant.getId();
                assertEquals(signaling.getParticipant(), participantId);

                testPassed = true;

                signal.countDown();
            }
        });

        QBSignaling signaling = serviceFaker.getSignalingManager().createSignaling(user.getId(), null);
        final String message = "test";
        signaling.sendMessage(message);

        signal.await(SIGNALING_CREATION_TIMEOUT, TimeUnit.SECONDS);
        assertNotNull(manager.getSignaling(participant.getId()));
        assertEquals(testPassed, true);
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
