package com.quickblox.sample.test.chat.chatservice;

import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.sample.test.BaseTestCase;
import com.quickblox.sample.test.TestConfig;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class TestLogout extends BaseTestCase {

    private static final int LOGOUT_TIMEOUT = 60;
    private QBChatService service;
    private QBUser user;

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
        service.destroy();
        super.tearDown();
    }

    public void testLogoutSync() throws Exception {
        service.login(user);
        service.logout();
        assertEquals(service.isLoggedIn(), false);
    }

    public void testLogoutAsync() throws Exception {
        service.login(user);
        final CountDownLatch signal = new CountDownLatch(1);
        service.logout(new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                signal.countDown();
            }
        });
        signal.await(LOGOUT_TIMEOUT, TimeUnit.SECONDS);
        assertEquals(service.isLoggedIn(), false);
    }

    public void testLogoutWithoutLogin() throws Exception {
        service.logout();
    }


}
