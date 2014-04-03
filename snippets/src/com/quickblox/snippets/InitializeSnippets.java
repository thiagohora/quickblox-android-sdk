package com.quickblox.snippets;

import com.quickblox.core.QBCallback;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBSettings;
import com.quickblox.core.TransferProtocol;
import com.quickblox.module.auth.QBAuth;

/**
 * User: Oleg Soroka
 * Date: 01.10.12
 * Time: 19:30
 */
public class InitializeSnippets {


    public InitializeSnippets() {

        // App credentials from QB Admin Panel
        QBSettings.getInstance().setServerApiDomain("api.stage.quickblox.com");
        QBSettings.getInstance().setChatServerDomain("chatstage.quickblox.com");
        QBSettings.getInstance().setContentBucketName("blobs-test-oz");
        QBSettings.getInstance().setTransferProtocol(TransferProtocol.HTTP);
        QBSettings.getInstance().fastConfigInit("438", "EYvyxCwkBHfa8EB", "NEsZAtydRU8syMS");

        //specify custom domains
//        QBSettings.getInstance().setServerApiDomain(Config.SERVER_DOMAIN);
//        QBSettings.getInstance().setContentBucketName(Config.CONTENT_DOMAIN);
//        QBSettings.getInstance().setChatServerDomain(Config.CHAT_DOMAIN);
//        QBSettings.getInstance().setTurnServerDomain(Config.TURN_SERVER_DOMAIN);
        QBAuth.createSession((QBEntityCallback)null);


    }
}