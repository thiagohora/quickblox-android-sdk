package com.quickblox.sample.test.chat.chatservice;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.utils.Consts;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.sample.test.BaseTestCase;
import com.quickblox.sample.test.TestConfig;

import org.jivesoftware.smack.XMPPException;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestLogin extends BaseTestCase {

    private static final long LOGIN_TIMEOUT = 15;

    private QBUser user;
    private QBChatService service;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        user = new QBUser(TestConfig.USER_LOGIN, TestConfig.USER_PASSWORD);
        user.setId(TestConfig.USER_ID);
        QBChatService.init(context);
        service = QBChatService.getInstance();
    }

    @Override
    protected void tearDown() throws Exception {
        service.logout();
        service.destroy();
        super.tearDown();
    }

    public void testLoginSync() throws Exception {
        service.login(user);
        assertEquals(true, service.isLoggedIn());
    }

    public void testLoginAsync() throws Exception {
        final CountDownLatch signal = new CountDownLatch(1);
        service.login(user, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                signal.countDown();
            }

            @Override
            public void onError(List errors) {
                fail();
            }
        });
        signal.await(LOGIN_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(true, service.isLoggedIn());
    }

    public void testLoginTwiceSync() throws Exception {
        service.login(user);
        try {
            service.login(user);
            fail("Missing exception");
        } catch (XMPPException e) {
            assertEquals(Consts.ALREADY_LOGGED_IN, e.getMessage());
        }
    }

    public void testLoginTwiceAsync() throws Exception {
        final CountDownLatch signal = new CountDownLatch(2);
        QBEntityCallback callback = new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                if (signal.getCount() == 2) {
                    signal.countDown();
                } else {
                    fail();
                }
            }

            @Override
            public void onError(List errors) {
                if (signal.getCount() == 1 && errors.get(0).equals(Consts.ALREADY_LOGGED_IN)) {
                    signal.countDown();
                } else {
                    fail();
                }
            }
        };
        service.login(user, callback);
        service.login(user, callback);
        signal.await(LOGIN_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(true, service.isLoggedIn());
    }

    public void testNullUserSync() throws Exception {
        try {
            service.login(null);
            fail("Missing exception");
        } catch (NullPointerException e) {
            // Ok
        }
    }

    public void testNullUserIdSync() throws Exception {
        try {
            user = new QBUser(TestConfig.USER_LOGIN, TestConfig.USER_PASSWORD);
            service.login(user);
            fail("Missing exception");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), Consts.ILLEGAL_LOGIN_ARGUMENT);
        }
    }

    public void testNullUserPasswordSync() throws Exception {
        try {
            user = new QBUser(TestConfig.USER_LOGIN);
            user.setId(TestConfig.USER_ID);
            service.login(user);
            fail("Missing exception");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), Consts.ILLEGAL_LOGIN_ARGUMENT);
        }
    }

    public void testNullUserIdAndPasswordSync() throws Exception {
        try {
            user = new QBUser(TestConfig.USER_LOGIN);
            service.login(user);
            fail("Missing exception");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), Consts.ILLEGAL_LOGIN_ARGUMENT);
        }
    }

    public void testNullUserAsync() throws Exception {
        try {
            final CountDownLatch signal = new CountDownLatch(1);
            service.login(null, new TestLoginCallback(signal));
            signal.await(LOGIN_TIMEOUT, TimeUnit.SECONDS);
            fail("Missing exception");
        } catch (NullPointerException e) {
            // Ok
        }
    }

    public void testNullUserIdAsync() throws Exception {
        try {
            final CountDownLatch signal = new CountDownLatch(1);
            user = new QBUser(TestConfig.USER_LOGIN, TestConfig.USER_PASSWORD);
            service.login(user, new TestLoginCallback(signal));
            signal.await(LOGIN_TIMEOUT, TimeUnit.SECONDS);
            fail("Missing exception");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), Consts.ILLEGAL_LOGIN_ARGUMENT);
        }
    }

    public void testNullUserPasswordAsync() throws Exception {
        try {
            final CountDownLatch signal = new CountDownLatch(1);
            user = new QBUser(TestConfig.USER_LOGIN);
            user.setId(TestConfig.USER_ID);
            service.login(user, new TestLoginCallback(signal));
            signal.await(LOGIN_TIMEOUT, TimeUnit.SECONDS);
            fail("Missing exception");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), Consts.ILLEGAL_LOGIN_ARGUMENT);
        }
    }

    public void testNullUserIdAndPasswordAsync() throws Exception {
        try {
            final CountDownLatch signal = new CountDownLatch(1);
            user = new QBUser(TestConfig.USER_LOGIN);
            service.login(user, new TestLoginCallback(signal));
            signal.await(LOGIN_TIMEOUT, TimeUnit.SECONDS);
            fail("Missing exception");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), Consts.ILLEGAL_LOGIN_ARGUMENT);
        }
    }

    public void testLoginAfterLogout() throws Exception {
        service.login(user);
        assertEquals(true, service.isLoggedIn());
        service.logout();
        assertEquals(false, service.isLoggedIn());
        service.login(user);
        assertEquals(true, service.isLoggedIn());
    }

    // TODO : add test for adding null listener

    private class TestLoginCallback extends QBEntityCallbackImpl {

        private CountDownLatch signal;

        public TestLoginCallback(CountDownLatch signal) {
            this.signal = signal;
        }

        @Override
        public void onSuccess() {
            fail();
        }

        @Override
        public void onError(List errors) {
            signal.countDown();
        }
    }
}
