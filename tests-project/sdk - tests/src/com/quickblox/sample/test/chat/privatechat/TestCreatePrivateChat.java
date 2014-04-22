package com.quickblox.sample.test.chat.privatechat;

import com.quickblox.module.chat.QBPrivateChat;
import com.quickblox.module.chat.QBPrivateChatManager;
import com.quickblox.module.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.module.chat.utils.QBChatUtils;

import org.jivesoftware.smack.packet.Message;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestCreatePrivateChat extends PrivateChatTestCase {

    private static final long CHAT_CREATION_TIMEOUT = 10;

    public void testCreateLocally() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        QBPrivateChatManager manager = service.getPrivateChatManager();

        manager.addPrivateChatManagerListener(new QBPrivateChatManagerListener() {
            @Override
            public void chatCreated(QBPrivateChat chat, boolean createdLocally) {
                assertEquals(createdLocally, true);
                final int participantId = participant.getId();
                assertEquals(participantId, chat.getParticipant());
                signal.countDown();
            }
        });

        QBPrivateChat chat = manager.createChat(participant.getId(), null);

        signal.await(CHAT_CREATION_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(manager.getChat(participant.getId()), chat);
    }

    public void testCreateOnIncomingChatMessage() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        QBPrivateChatManager manager = service.getPrivateChatManager();

        manager.addPrivateChatManagerListener(new QBPrivateChatManagerListener() {
            @Override
            public void chatCreated(QBPrivateChat chat, boolean createdLocally) {
                assertEquals(false, createdLocally);
                final int participantId = participant.getId();
                assertEquals(participantId, chat.getParticipant());

                testPassed = true;

                signal.countDown();
            }
        });

        QBPrivateChat chat = serviceFaker.getPrivateChatManager().createChat(user.getId(), null);
        final String message = "chat test";
        chat.sendMessage(message);

        signal.await(CHAT_CREATION_TIMEOUT, TimeUnit.SECONDS);
        assertNotNull(manager.getChat(participant.getId()));
        assertEquals(true, testPassed);
    }

    public void testCreateOnIncomingNormalMessage() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        QBPrivateChatManager manager = service.getPrivateChatManager();

        assertNull(manager.getChat(participant.getId()));

        manager.addPrivateChatManagerListener(new QBPrivateChatManagerListener() {
            @Override
            public void chatCreated(QBPrivateChat chat, boolean createdLocally) {
                assertEquals(false, createdLocally);
                final int participantId = participant.getId();
                assertEquals(participantId, chat.getParticipant());

                testPassed = true;

                signal.countDown();
            }
        });

        final String to = QBChatUtils.getChatLoginFull(user.getId());
        final String from = QBChatUtils.getChatLoginFull(participant.getId());
        final String messageBody = "normal test";

        Message message = new Message(to);
        message.setFrom(from);
        message.setBody(messageBody);

        serviceFaker.sendPacket(message);

        signal.await(CHAT_CREATION_TIMEOUT, TimeUnit.SECONDS);
        assertNotNull(manager.getChat(participant.getId()));
        assertEquals(true, testPassed);
    }

    public void testNormalMessagesNotIncluded() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        QBPrivateChatManager manager = service.getPrivateChatManager();
        manager.setNormalIncluded(false);

        assertNull(manager.getChat(participant.getId()));

        final String to = QBChatUtils.getChatLoginFull(user.getId());
        final String from = QBChatUtils.getChatLoginFull(participant.getId());
        final String messageBody = "normal test";

        Message message = new Message(to);
        message.setFrom(from);
        message.setBody(messageBody);

        serviceFaker.sendPacket(message);

        signal.await(CHAT_CREATION_TIMEOUT, TimeUnit.SECONDS);
        assertNull(manager.getChat(participant.getId()));
    }

    public void testCreateTwice() throws Exception {
        QBPrivateChatManager manager = service.getPrivateChatManager();
        QBPrivateChat firstChat = manager.createChat(participant.getId(), null);
        QBPrivateChat secondChat = manager.createChat(participant.getId(), null);
        assertNotSame(firstChat, secondChat);
    }

    public void testAddNullManagerListener() throws Exception {
        service.getPrivateChatManager().addPrivateChatManagerListener(null);
        testCreateOnIncomingChatMessage();
    }
}
