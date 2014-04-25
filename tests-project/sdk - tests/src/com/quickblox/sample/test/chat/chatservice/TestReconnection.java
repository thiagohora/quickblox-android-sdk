package com.quickblox.sample.test.chat.chatservice;

import android.util.Log;

import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.sample.test.BaseTestCase;
import com.quickblox.sample.test.TestConfig;
import com.quickblox.sample.test.faker.ChatServiceFaker;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestReconnection extends BaseTestCase {

    private static final long RECONNECTION_TIMEOUT = 17;

    private QBUser user;
    private QBChatService service;

    private boolean testPassed;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        QBChatService.setDebugEnabled(true);

        user = new QBUser(TestConfig.USER_LOGIN, TestConfig.USER_PASSWORD);
        user.setId(TestConfig.USER_ID);
        QBChatService.init(context);
        service = QBChatService.getInstance();

        testPassed = false;
    }

    @Override
    protected void tearDown() throws Exception {
        service.logout();
        service.destroy();
        super.tearDown();
    }

    public void testAutoReconnection() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        service.login(user);
        XMPPConnectionTestListener listener = new XMPPConnectionTestListener();
        service.addConnectionListener(listener);

        // Simulates an error in the connection
        ChatServiceFaker.notifyConnectionError(service, new Exception("Simulated Error"));
        signal.await(RECONNECTION_TIMEOUT, TimeUnit.SECONDS);

        // After 10 seconds, the reconnection manager must reestablishes the connection
        assertEquals("The ConnectionListener.connectionEstablished() notification was not fired", true,
                listener.reconnected);
        assertTrue("The ReconnectionManager algorithm has reconnected without waiting at least 5 seconds",
                listener.attemptsNotifications > 0);

        signal.await(RECONNECTION_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(true, service.isLoggedIn());
    }

    public void testManualReconnection() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        service.login(user);
        XMPPConnectionTestListener listener = new XMPPConnectionTestListener();
        service.addConnectionListener(listener);

        // Simulates an error in the connection
        ChatServiceFaker.notifyConnectionError(service, new Exception("Simulated Error"));

        // After 10 seconds, the reconnection manager must reestablishes the connection
        assertEquals("The ConnectionListener.connectionClosedOnError() notification was not fired", true,
                listener.connectionClosedOnError);

        // Makes a manual reconnection
        service.login(user, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                testPassed = true;
                signal.countDown();
            }

            @Override
            public void onError(List errors) {
                fail(Arrays.toString(errors.toArray()));
            }
        });
        signal.await(RECONNECTION_TIMEOUT, TimeUnit.SECONDS);

        assertEquals("User didn't login", true, service.isLoggedIn());
        assertEquals("User logged in automatically", true, testPassed);
    }

    public void testCloseAndManualReconnection() throws Exception {
        service.login(user);
        XMPPConnectionTestListener listener = new XMPPConnectionTestListener();
        service.addConnectionListener(listener);

        // Produces a normal disconnection
        service.logout();
        assertEquals("ConnectionListener.connectionClosed() was not notified", true,
                listener.connectionClosed);
        // Waits 10 seconds waiting for a reconnection that must not happened.
        Thread.sleep(RECONNECTION_TIMEOUT * 1000);
        assertEquals("The connection was stablished but it was not allowed to", false, listener.reconnected);

        // Makes a manual reconnection
        service.login(user);
        assertEquals(true, service.isLoggedIn());
    }

    private class XMPPConnectionTestListener implements ConnectionListener {

        // Variables to support listener notifications verification
        private volatile boolean connectionClosed = false;
        private volatile boolean connectionClosedOnError = false;
        private volatile boolean reconnected = false;
        private volatile boolean reconnectionFailed = false;
        private volatile int remainingSeconds = 0;
        private volatile int attemptsNotifications = 0;
        private CountDownLatch countDownLatch;

        private XMPPConnectionTestListener(CountDownLatch latch) {
            countDownLatch = latch;
        }

        private XMPPConnectionTestListener() {
        }

        @Override
        public void connected(XMPPConnection connection) {

        }

        @Override
        public void authenticated(XMPPConnection connection) {

        }

        /**
         * Methods to test the listener.
         */
        @Override
        public void connectionClosed() {
            connectionClosed = true;

            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
            Log.d("SMACK", "connectionClosed()");
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            connectionClosedOnError = true;
            Log.d("SMACK", "connectionClosedOnError() " + e.getMessage());
        }

        @Override
        public void reconnectingIn(int seconds) {
            attemptsNotifications = attemptsNotifications + 1;
            remainingSeconds = seconds;
            Log.d("SMACK", "reconnectingIn(" + seconds + ")");
        }

        @Override
        public void reconnectionSuccessful() {
            reconnected = true;

            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
            Log.d("SMACK", "reconnectionSuccessful()");
        }

        @Override
        public void reconnectionFailed(Exception error) {
            reconnectionFailed = true;

            if (countDownLatch != null) {
                countDownLatch.countDown();
            }
            Log.d("SMACK", "reconnectionFailed() " + error.getMessage());
        }
    }
}
