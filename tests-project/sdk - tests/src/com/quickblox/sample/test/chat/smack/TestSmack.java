package com.quickblox.sample.test.chat.smack;

import com.quickblox.core.QBCallback;
import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.result.Result;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.users.QBUsers;
import com.quickblox.sample.test.BaseTestCase;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPTCPConnection;
import org.jivesoftware.smack.packet.Message;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.CountDownLatch;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class TestSmack extends BaseTestCase {

    private boolean success;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        success = false;
    }

    public void testName() throws Exception {
        SmackConfiguration.DEBUG_ENABLED = true;

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, null, new java.security.SecureRandom());

        ConnectionConfiguration configuration = new ConnectionConfiguration("chatstage.quickblox.com");
        configuration.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
        configuration.setCustomSSLContext(context);
        XMPPConnection connection = new XMPPTCPConnection(configuration);
        connection.connect();
        connection.login("18551-438", "videoChatUser1");

        final CountDownLatch latch = new CountDownLatch(1);
        ChatManager manager = ChatManager.getInstanceFor(connection);
        Chat chat = manager.createChat(connection.getUser(), new MessageListener() {
            @Override
            public void processMessage(Chat chat, Message message) {
                success = true;
                latch.countDown();
            }
        });
        chat.sendMessage("hello");
        latch.await();
        assertEquals(true, success);
        connection.disconnect();
    }
}
