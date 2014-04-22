package com.quickblox.sample.test.chat.chatservice;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.internal.core.helper.Lo;
import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.sample.test.BaseTestCase;
import com.quickblox.sample.test.TestConfig;

import org.jivesoftware.smack.AbstractConnectionListener;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPException;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestReconnection extends BaseTestCase {

    private static final long RECONNECTION_TIMEOUT = 25;

    private QBUser user;
    private QBChatService service;
    private WifiManager wifiManager;

    private boolean testPassed;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
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
        wifiManager.setWifiEnabled(true);
        super.tearDown();
    }

    /*
    public void testManualReconnection() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        service.login(user);
        ConnectionListener connectionListener = new FakeConnectionListenerImpl() {

            @Override
            public void reconnectionFailed(Exception e) {
                try {
                    wifiManager.setWifiEnabled(true);
                    Thread.sleep(5000);
                    service.login(user);
                    testPassed = true;
                    signal.countDown();
                } catch (XMPPException e1) {
                    Log.d(TestConfig.TAG, "failed to login", e1);
                    fail(e1.getMessage());
                } catch (IllegalStateException e1) {
                    Log.d(TestConfig.TAG, "failed to login", e1);
                    fail(e1.getMessage());
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        };
        service.addConnectionListener(connectionListener);
        wifiManager.setWifiEnabled(false);
        signal.await(RECONNECTION_TIMEOUT, TimeUnit.SECONDS);
        // service.removeConnectionListener(connectionListener);
        assertEquals(true, service.isLoggedIn());
        assertEquals(true, testPassed);
    }
    */

    public void testAutoReconnection() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        service.login(user);
        ConnectionListener connectionListener = new FakeConnectionListenerImpl() {

            @Override
            public void reconnectingIn(int seconds) {
                if (!wifiManager.isWifiEnabled()) {
                    wifiManager.setWifiEnabled(true);
                }
            }

            @Override
            public void reconnectionSuccessful() {
                signal.countDown();
            }
        };
        service.addConnectionListener(connectionListener);
        wifiManager.setWifiEnabled(false);
        signal.await(RECONNECTION_TIMEOUT, TimeUnit.SECONDS);
        service.removeConnectionListener(connectionListener);
        assertEquals(true, service.isLoggedIn());
    }

    private class FakeConnectionListenerImpl extends AbstractConnectionListener {

        @Override
        public void reconnectionFailed(Exception e) {
            fail("Reconnection Failed");
        }
    }
}
