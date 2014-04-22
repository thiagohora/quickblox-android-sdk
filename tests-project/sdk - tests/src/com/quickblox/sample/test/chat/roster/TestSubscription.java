package com.quickblox.sample.test.chat.roster;

import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.QBRoster;
import com.quickblox.module.chat.QBRosterEntry;
import com.quickblox.module.chat.listeners.QBRosterListener;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.sample.test.BaseTestCase;
import com.quickblox.sample.test.TestConfig;
import com.quickblox.sample.test.faker.ChatServiceFaker;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.RosterPacket;

import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestSubscription extends BaseTestCase {

    private static final long SUBSCRIPTION_TIMEOUT = 20;

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

        cleanRoster(service.getRoster());
        cleanRoster(serviceFaker.getRoster());

        testPassed = false;
    }

    private void cleanRoster(QBRoster roster) throws XMPPException {
        for (QBRosterEntry entry : roster.getEntries()) {
            roster.removeEntry(entry);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        service.logout();
        serviceFaker.logout();
        service.destroy();
        super.tearDown();
    }

    public void testAskForSubscription() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);

        serviceFaker.getRoster().addRosterListener(new QBRosterListener() {
            @Override
            public void entriesDeleted(Collection<Integer> userIds) {

            }

            @Override
            public void entriesAdded(Collection<Integer> userIds) {
                int participantId = userIds.iterator().next();
                QBRosterEntry entry = serviceFaker.getRoster().getEntry(participantId);
                assertEquals(entry.getType(), RosterPacket.ItemType.none);
                assertNull(entry.getStatus());
                testPassed = true;
                signal.countDown();
            }

            @Override
            public void entriesUpdated(Collection<Integer> userIds) {

            }

            @Override
            public void presenceChanged(Presence presence) {

            }
        });

        service.getRoster().createEntry(participant.getId(), null, new String[]{"group"});

        signal.await(SUBSCRIPTION_TIMEOUT, TimeUnit.SECONDS);
        QBRosterEntry entry = service.getRoster().getEntry(participant.getId());
        assertEquals(entry.getStatus(), RosterPacket.ItemStatus.SUBSCRIPTION_PENDING);
        assertEquals(entry.getType(), RosterPacket.ItemType.none);
        assertEquals(testPassed, true);
    }

    public void testManualModeConfirmation() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);

        serviceFaker.getRoster().addRosterListener(new QBRosterListener() {
            @Override
            public void entriesDeleted(Collection<Integer> userIds) {

            }

            @Override
            public void entriesAdded(Collection<Integer> userIds) {
                int participantId = userIds.iterator().next();
                QBRosterEntry entry = serviceFaker.getRoster().getEntry(participantId);
                serviceFaker.getRoster().confirmSubscription(entry);
            }

            @Override
            public void entriesUpdated(Collection<Integer> userIds) {

            }

            @Override
            public void presenceChanged(Presence presence) {

            }
        });

        service.getRoster().addRosterListener(new QBRosterListener() {
            @Override
            public void entriesDeleted(Collection<Integer> userIds) {

            }

            @Override
            public void entriesAdded(Collection<Integer> userIds) {

            }

            @Override
            public void entriesUpdated(Collection<Integer> userIds) {
                int participantId = userIds.iterator().next();
                assertEquals(participantId, TestConfig.PARTICIPANT_ID);
                QBRosterEntry entry = service.getRoster().getEntry(participantId);

                if (entry.getType() == RosterPacket.ItemType.from) {
                    testPassed = true;
                    signal.countDown();
                }
            }

            @Override
            public void presenceChanged(Presence presence) {
            }
        });

        service.getRoster().createEntry(participant.getId(), null, new String[]{"group"});

        signal.await(SUBSCRIPTION_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(testPassed, true);
    }
}
