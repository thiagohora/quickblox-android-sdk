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

public class TestSendMessage extends SignalingTestCase {

    private static final long PACKET_DELIVERY_TIMEOUT = 10;

    public void testSendText() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        final String messageText = "test";

        serviceFaker.getSignalingManager().createSignaling(user.getId(), new QBSignalingListener() {
            @Override
            public void processMessage(QBSignaling signaling, Message message) {
                final int participantId = user.getId();
                assertEquals(participantId, signaling.getParticipant());
                assertEquals(messageText, message.getBody());
                testPassed = true;
                signal.countDown();
            }
        });

        QBSignaling signaling = service.getSignalingManager().createSignaling(participant.getId(), null);
        signaling.sendMessage(messageText);

        signal.await(PACKET_DELIVERY_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(true, testPassed);
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
                assertEquals(participantId, signaling.getParticipant());
                assertEquals(messageText, message.getBody());
                assertEquals(Message.Type.headline, message.getType());
                assertEquals(propertyValue, message.getProperty(propertyKey));
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
        assertEquals(true, testPassed);
    }

    public void testSendNullText() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);

        serviceFaker.getSignalingManager().createSignaling(user.getId(), new QBSignalingListener() {

            @Override
            public void processMessage(QBSignaling signaling, Message message) {
                final int participantId = user.getId();
                assertEquals(participantId, signaling.getParticipant());
                assertEquals(null, message.getBody());
                testPassed = true;
                signal.countDown();
            }
        });

        QBSignaling signaling = service.getSignalingManager().createSignaling(participant.getId(), null);
        signaling.sendMessage((String) null);

        signal.await(PACKET_DELIVERY_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(true, testPassed);
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
