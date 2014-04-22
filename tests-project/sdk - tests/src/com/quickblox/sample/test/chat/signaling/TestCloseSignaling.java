package com.quickblox.sample.test.chat.signaling;

import com.quickblox.module.chat.QBSignaling;
import com.quickblox.module.chat.QBSignalingManager;

public class TestCloseSignaling extends SignalingTestCase {

    public void testClose() throws Exception {
        QBSignalingManager manager = service.getSignalingManager();
        QBSignaling signaling = manager.createSignaling(participant.getId(), null);
        assertNotNull(manager.getSignaling(participant.getId()));
        signaling.close();
        assertNull(manager.getSignaling(participant.getId()));
    }
}
