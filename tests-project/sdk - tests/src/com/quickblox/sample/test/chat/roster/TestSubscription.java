package com.quickblox.sample.test.chat.roster;

import android.util.Log;

import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.QBRoster;
import com.quickblox.module.chat.QBRosterEntry;
import com.quickblox.module.chat.listeners.QBRosterListener;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.sample.test.BaseTestCase;
import com.quickblox.sample.test.TestConfig;
import com.quickblox.sample.test.faker.ChatServiceFaker;

import org.jivesoftware.smack.SmackException;
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

    private boolean serviceTestPassed;
    private boolean fakerTestPassed;

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

        serviceTestPassed = false;
        fakerTestPassed = false;
    }

    private void cleanRoster(QBRoster roster) throws XMPPException, SmackException {
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
/*
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
                serviceTestPassed = true;
                signal.countDown();
            }

            @Override
            public void entriesUpdated(Collection<Integer> userIds) {

            }

            @Override
            public void presenceChanged(Presence presence) {

            }
        });

        service.getRoster().createEntry(participant.getId(), null);

        signal.await(SUBSCRIPTION_TIMEOUT, TimeUnit.SECONDS);
        QBRosterEntry entry = service.getRoster().getEntry(participant.getId());
        assertEquals(entry.getStatus(), RosterPacket.ItemStatus.SUBSCRIPTION_PENDING);
        assertEquals(entry.getType(), RosterPacket.ItemType.none);
        assertEquals(true, serviceTestPassed);
    }
*/
/*
    public void testManualModeConfirmation() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);

        serviceFaker.getRoster().addRosterListener(new QBRosterListener() {
            @Override
            public void entriesDeleted(Collection<Integer> userIds) {

            }

            @Override
            public void entriesAdded(Collection<Integer> userIds) {
                int participantId = userIds.iterator().next();
                try {
                    serviceFaker.getRoster().confirmSubscription(participantId);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
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

                if (entry.getType() == RosterPacket.ItemType.to) {
                    serviceTestPassed = true;
                    signal.countDown();
                }
            }

            @Override
            public void presenceChanged(Presence presence) {
            }
        });

        service.getRoster().createEntry(participant.getId(), null);

        signal.await(SUBSCRIPTION_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(true, serviceTestPassed);
    }
    */

    public void testMutualModeConfirmation() throws Exception {
        final CountDownLatch signal = new CountDownLatch(2);

        serviceFaker.getRoster().setSubscriptionMode(QBRoster.SubscriptionMode.mutual);
        serviceFaker.getRoster().addRosterListener(new QBRosterListener() {
            @Override
            public void entriesDeleted(Collection<Integer> userIds) {

            }

            @Override
            public void entriesAdded(Collection<Integer> userIds) {
                int participantId = userIds.iterator().next();
                try {
                    serviceFaker.getRoster().confirmSubscription(participantId);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void entriesUpdated(Collection<Integer> userIds) {
                int participantId = userIds.iterator().next();
                assertEquals(TestConfig.USER_ID, participantId);
                QBRosterEntry entry = service.getRoster().getEntry(participantId);

                if (entry.getType() == RosterPacket.ItemType.both) {
                    fakerTestPassed = true;
                    signal.countDown();
                }
            }

            @Override
            public void presenceChanged(Presence presence) {

            }
        });

        service.getRoster().setSubscriptionMode(QBRoster.SubscriptionMode.mutual);
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
                assertEquals(TestConfig.PARTICIPANT_ID, participantId);
                QBRosterEntry entry = service.getRoster().getEntry(participantId);

                if (entry.getType() == RosterPacket.ItemType.both) {
                    serviceTestPassed = true;
                    signal.countDown();
                }
            }

            @Override
            public void presenceChanged(Presence presence) {
            }
        });

        service.getRoster().createEntry(participant.getId(), null);

        signal.await(SUBSCRIPTION_TIMEOUT, TimeUnit.SECONDS);

        Log.d("Roster", "service entries:");
        printEntities(service);
        Log.d("Roster", "serviceFaker entries:");
        printEntities(serviceFaker);

        assertEquals(RosterPacket.ItemType.both, service.getRoster().getEntry(participant.getId()).getType());
        assertEquals(RosterPacket.ItemType.both, serviceFaker.getRoster().getEntry(user.getId()).getType());
//        assertEquals(true, serviceTestPassed);
    }

    private void printEntities(QBChatService chatService) {
        Collection<QBRosterEntry> entries = chatService.getRoster().getEntries();
        for (QBRosterEntry entry : entries) {
            Log.d("Roster", entry.getUserId() + " " + entry.getType() + " " + entry.getStatus());
        }
    }
}
