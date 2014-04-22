package com.quickblox.sample.test.chat.chatservice;

import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.utils.Consts;
import com.quickblox.sample.test.BaseTestCase;

public class TestInit extends BaseTestCase {

    @Override
    protected void tearDown() throws Exception {
        if (QBChatService.isInitialized()) {
            QBChatService.getInstance().destroy();
        }
        super.tearDown();
    }

    public void testGetInstanceBeforeInit() throws Exception {
        try {
            QBChatService.getInstance();
            fail("Missing exception");
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), Consts.CHAT_NOT_INITIALIZED);
        }
    }

    public void testInit() throws Exception {
        QBChatService.init(context);
        assertEquals(true, QBChatService.isInitialized());
    }

    public void testInitTwice() throws Exception {
        QBChatService.init(context);
        QBChatService.init(context);
    }

    public void testDestroy() throws Exception {
        QBChatService.init(context);
        QBChatService.getInstance().destroy();
        assertEquals(false, QBChatService.isInitialized());
    }

    public void testDestroyTwice() throws Exception {
        QBChatService.init(context);
        QBChatService.getInstance().destroy();
        try {
            QBChatService.getInstance().destroy();
            fail("Missing exception");
        } catch (IllegalStateException e) {
            assertEquals(e.getMessage(), Consts.CHAT_NOT_INITIALIZED);
        }
    }
}
