package com.quickblox.sample.test.faker;

import com.quickblox.module.chat.utils.QBChatUtils;

import org.jivesoftware.smack.packet.Presence;

public class PresenceFaker {

    public static Presence getPresence(int userId, int participantId) {
        Presence presence = new Presence(Presence.Type.available);
        presence.setFrom(QBChatUtils.getChatLoginFull(userId));
        presence.setTo(QBChatUtils.getChatLoginFull(participantId));
        presence.setProperty("type", "faker");
        return presence;
    }
}
