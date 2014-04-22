package com.quickblox.sample.test.chat.privatechat;

import com.quickblox.module.chat.QBPrivateChat;
import com.quickblox.module.chat.QBPrivateChatManager;

public class TestCloseChat extends PrivateChatTestCase {

    public void testClose() throws Exception {
        QBPrivateChatManager manager = service.getPrivateChatManager();
        QBPrivateChat chat = manager.createChat(participant.getId(), null);
        assertNotNull(manager.getChat(participant.getId()));
        chat.close();
        assertNull(manager.getChat(participant.getId()));
    }
}
