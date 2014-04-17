package com.quickblox.sample.test.chat.privatechat;

import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.QBPrivateChat;
import com.quickblox.module.chat.QBPrivateChatManager;
import com.quickblox.module.chat.listeners.QBPrivateChatManagerListener;
import com.quickblox.module.chat.utils.QBChatUtils;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.sample.test.BaseTestCase;
import com.quickblox.sample.test.TestConfig;
import com.quickblox.sample.test.faker.ChatServiceFaker;

import org.jivesoftware.smack.packet.Message;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestCreatePrivateChat extends BaseTestCase {

    private static final long CHAT_CREATION_TIMEOUT = 10;

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
        QBPrivateChatManager manager = service.getPrivateChatManager();

        manager.addPrivateChatManagerListener(new QBPrivateChatManagerListener() {
            @Override
            public void chatCreated(QBPrivateChat chat, boolean createdLocally) {
                assertEquals(createdLocally, true);
                final int participantId = participant.getId();
                assertEquals(chat.getParticipant(), participantId);
                signal.countDown();
            }
        });

        QBPrivateChat chat = manager.createChat(participant.getId(), null);

        signal.await(CHAT_CREATION_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(chat, manager.getChat(participant.getId()));
    }

    public void testCreateOnIncomingChatMessage() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        QBPrivateChatManager manager = service.getPrivateChatManager();

        manager.addPrivateChatManagerListener(new QBPrivateChatManagerListener() {
            @Override
            public void chatCreated(QBPrivateChat chat, boolean createdLocally) {
                assertEquals(createdLocally, false);
                final int participantId = participant.getId();
                assertEquals(chat.getParticipant(), participantId);

                testPassed = true;

                signal.countDown();
            }
        });

        QBPrivateChat chat = serviceFaker.getPrivateChatManager().createChat(user.getId(), null);
        final String message = "chat test";
        chat.sendMessage(message);

        signal.await(CHAT_CREATION_TIMEOUT, TimeUnit.SECONDS);
        assertNotNull(manager.getChat(participant.getId()));
        assertEquals(testPassed, true);
    }

    public void testCreateOnIncomingNormalMessage() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        QBPrivateChatManager manager = service.getPrivateChatManager();

        assertNull(manager.getChat(participant.getId()));

        manager.addPrivateChatManagerListener(new QBPrivateChatManagerListener() {
            @Override
            public void chatCreated(QBPrivateChat chat, boolean createdLocally) {
                assertEquals(createdLocally, false);
                final int participantId = participant.getId();
                assertEquals(chat.getParticipant(), participantId);

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
        assertEquals(testPassed, true);
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
