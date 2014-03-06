package com.quickblox.sample.video_webrtc;

import com.quickblox.module.users.model.QBUser;

/**
 * Created by vadim on 26.02.14.
 */
public class DataHolder {
    private static QBUser qbUser;

    public static void setUser(QBUser qbUser) {

        DataHolder.qbUser = qbUser;
    }

    public static QBUser getQbUser() {
        return qbUser;
    }
}
