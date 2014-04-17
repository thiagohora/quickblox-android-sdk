package com.quickblox.sample.test.chat.chatservice;

import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.utils.Consts;
import com.quickblox.module.chat.utils.QBChatUtils;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.sample.test.BaseTestCase;
import com.quickblox.sample.test.TestConfig;
import com.quickblox.sample.test.faker.ChatServiceFaker;
import com.quickblox.sample.test.faker.PresenceFaker;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.AndFilter;
import org.jivesoftware.smack.filter.FromMatchesFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestSendPacket extends BaseTestCase {

    private static final long PACKET_DELIVERY_TIMEOUT = 10;

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

    public void testSendPacket() throws Exception {
        final String from = QBChatUtils.getChatLoginFull(user.getId());
        final String to = QBChatUtils.getChatLoginFull(participant.getId());
        final String messageBody = "test";

        Message message = new Message(to);
        message.setFrom(from);
        message.setBody(messageBody);

        final CountDownLatch signal = new CountDownLatch(1);
        serviceFaker.addPacketListener(new PacketListener() {
            @Override
            public void processPacket(Packet packet) {
                Message message = (Message) packet;
                if (message.getFrom().equals(from) && message.getBody().equals(messageBody)) {
                    testPassed = true;
                    signal.countDown();
                }
            }
        }, new AndFilter(new FromMatchesFilter(from), new PacketTypeFilter(Message.class)));
        service.sendPacket(message);
        signal.await(PACKET_DELIVERY_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(testPassed, true);
    }

    public void testSendPacketWithNoConnection() throws Exception {
        service.logout();
        try {
            Packet packet = PresenceFaker.getPresence(user.getId(), participant.getId());
            service.sendPacket(packet);
            fail("Missed exception");
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), Consts.NOT_LOGGED_IN);
        }
    }

    public void testSendNullPacket() throws Exception {
        try {
            service.sendPacket(null);
            fail("Missed exception");
        } catch (NullPointerException e) {
            // Ok
        }
    }
}
