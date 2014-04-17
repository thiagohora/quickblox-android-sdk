package com.quickblox.sample.test.chat.signaling;

import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.QBSignaling;
import com.quickblox.module.chat.listeners.QBSignalingListener;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.sample.test.BaseTestCase;
import com.quickblox.sample.test.TestConfig;
import com.quickblox.sample.test.faker.ChatServiceFaker;

import org.jivesoftware.smack.packet.Message;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestSendMessage extends BaseTestCase {

    private static final long PACKET_DELIVERY_TIMEOUT = 10;

    private QBChatService service;
    private QBChatService serviceFaker;

    private QBUser user;
    private QBUser participant;

    private boolean testPassed;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        QBChatService.setDebugEnabled(true);

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

    public void testSendText() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        final String messageText = "test";

        serviceFaker.getSignalingManager().createSignaling(user.getId(), new QBSignalingListener() {
            @Override
            public void processMessage(QBSignaling signaling, Message message) {
                final int participantId = user.getId();
                assertEquals(signaling.getParticipant(), participantId);
                assertEquals(message.getBody(), messageText);
                testPassed = true;
                signal.countDown();
            }
        });

        QBSignaling signaling = service.getSignalingManager().createSignaling(participant.getId(), null);
        signaling.sendMessage(messageText);

        signal.await(PACKET_DELIVERY_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(testPassed, true);
    }

    public void testSendMessage() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        final String messageText = "test";
        final String propertyKey = "key";
        final String propertyValue = "value";

        serviceFaker.getSignalingManager().createSignaling(user.getId(), new QBSignalingListener() {

            @Override
            public void processMessage(QBSignaling signaling, Message message) {
                final int participantId = user.getId();
                assertEquals(signaling.getParticipant(), participantId);
                assertEquals(message.getBody(), messageText);
                assertEquals(message.getType(), Message.Type.headline);
                assertEquals(message.getProperty(propertyKey), propertyValue);
                testPassed = true;
                signal.countDown();
            }
        });

        QBSignaling signaling = service.getSignalingManager().createSignaling(participant.getId(), null);

        Message message = signaling.createMessage();
        message.setBody(messageText);
        message.setType(Message.Type.groupchat);
        message.setProperty(propertyKey, propertyValue);
        signaling.sendMessage(message);

        signal.await(PACKET_DELIVERY_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(testPassed, true);
    }

    public void testSendNullText() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);

        serviceFaker.getSignalingManager().createSignaling(user.getId(), new QBSignalingListener() {

            @Override
            public void processMessage(QBSignaling signaling, Message message) {
                final int participantId = user.getId();
                assertEquals(signaling.getParticipant(), participantId);
                assertEquals(message.getBody(), null);
                testPassed = true;
                signal.countDown();
            }
        });

        QBSignaling signaling = service.getSignalingManager().createSignaling(participant.getId(), null);
        signaling.sendMessage((String) null);

        signal.await(PACKET_DELIVERY_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(testPassed, true);
    }

    public void testSendNullMessage() throws Exception {
        try {
            QBSignaling signaling = service.getSignalingManager().createSignaling(participant.getId(), null);
            signaling.sendMessage((Message) null);
            fail("Missed exception");
        } catch (NullPointerException e) {
            // Ok
        }
    }
}
