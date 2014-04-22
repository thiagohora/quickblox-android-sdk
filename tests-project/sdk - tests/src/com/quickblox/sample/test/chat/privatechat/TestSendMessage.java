package com.quickblox.sample.test.chat.privatechat;

import com.quickblox.module.chat.QBPrivateChat;
import com.quickblox.module.chat.listeners.QBMessageListener;

import org.jivesoftware.smack.packet.Message;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestSendMessage extends PrivateChatTestCase {

    private static final long PACKET_DELIVERY_TIMEOUT = 10;

    public void testSendText() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        final String messageText = "test";

        serviceFaker.getPrivateChatManager().createChat(user.getId(), new QBMessageListener<QBPrivateChat>() {

            @Override
            public void processMessage(QBPrivateChat sender, Message message) {
                final int participantId = user.getId();
                assertEquals(participantId, sender.getParticipant());
                assertEquals(messageText, message.getBody());
                testPassed = true;
                signal.countDown();
            }
        });

        QBPrivateChat chat = service.getPrivateChatManager().createChat(participant.getId(), null);
        chat.sendMessage(messageText);

        signal.await(PACKET_DELIVERY_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(true, testPassed);
    }

    public void testSendMessage() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        final String messageText = "test";
        final String propertyKey = "key";
        final String propertyValue = "value";

        serviceFaker.getPrivateChatManager().createChat(user.getId(), new QBMessageListener<QBPrivateChat>() {

            @Override
            public void processMessage(QBPrivateChat sender, Message message) {
                final int participantId = user.getId();
                assertEquals(participantId, sender.getParticipant());
                assertEquals(messageText, message.getBody());
                assertEquals(Message.Type.chat, message.getType());
                assertEquals(propertyValue, message.getProperty(propertyKey));
                testPassed = true;
                signal.countDown();
            }
        });

        QBPrivateChat chat = service.getPrivateChatManager().createChat(participant.getId(), null);

        Message message = chat.createMessage();
        message.setBody(messageText);
        message.setType(Message.Type.groupchat);
        message.setProperty(propertyKey, propertyValue);
        chat.sendMessage(message);

        signal.await(PACKET_DELIVERY_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(true, testPassed);
    }

    public void testSendNullText() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);

        serviceFaker.getPrivateChatManager().createChat(user.getId(), new QBMessageListener<QBPrivateChat>() {

            @Override
            public void processMessage(QBPrivateChat sender, Message message) {
                final int participantId = user.getId();
                assertEquals(participantId, sender.getParticipant());
                assertEquals(null, message.getBody());
                testPassed = true;
                signal.countDown();
            }
        });

        QBPrivateChat chat = service.getPrivateChatManager().createChat(participant.getId(), null);
        chat.sendMessage((String) null);

        signal.await(PACKET_DELIVERY_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(true, testPassed);
    }

    public void testSendNullMessage() throws Exception {
        try {
            QBPrivateChat chat = service.getPrivateChatManager().createChat(participant.getId(), null);
            chat.sendMessage((Message) null);
            fail("Missed exception");
        } catch (NullPointerException e) {
            // Ok
        }
    }
}
