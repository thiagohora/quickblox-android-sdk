package com.quickblox.sample.test.chat.privatechat;

import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.QBPrivateChat;
import com.quickblox.module.chat.QBPrivateChatManager;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.sample.test.BaseTestCase;
import com.quickblox.sample.test.TestConfig;

public class TestCloseChat extends BaseTestCase {

    private QBChatService service;
    private QBUser participant;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        QBUser user = new QBUser(TestConfig.USER_LOGIN, TestConfig.USER_PASSWORD);
        user.setId(TestConfig.USER_ID);
        participant = new QBUser(TestConfig.PARTICIPANT_NAME, TestConfig.PARTICIPANT_PASSWORD);
        participant.setId(TestConfig.PARTICIPANT_ID);
        QBChatService.init(context);

        service = QBChatService.getInstance();
        service.login(user);
    }

    @Override
    protected void tearDown() throws Exception {
        service.logout();
        service.destroy();
        super.tearDown();
    }

    public void testClose() throws Exception {
        QBPrivateChatManager manager = service.getPrivateChatManager();
        QBPrivateChat chat = manager.createChat(participant.getId(), null);
        assertNotNull(manager.getChat(participant.getId()));
        chat.close();
        assertNull(manager.getChat(participant.getId()));
    }
}
