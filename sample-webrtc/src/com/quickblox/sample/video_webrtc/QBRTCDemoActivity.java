package com.quickblox.sample.video_webrtc;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.listeners.SessionListener;
import com.quickblox.module.chat.smack.SmackAndroid;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.module.videochat_webrtc.*;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;

import java.util.List;

/**
 * Created by vadim on 26.02.14.
 */
public class QBRTCDemoActivity extends Activity implements QBEntityCallback<Void>, View.OnClickListener, ISignalingChannel.MessageObserver {

    private static final String TAG = QBRTCDemoActivity.class.getSimpleName();
    private VideoStreamsView vsv;
    private Toast logToast;
    // Synchronize on quit[0] to avoid teardown-related crashes.
    private final Boolean[] quit = new Boolean[]{false};
    private MediaConstraints sdpMediaConstraints;
    QBVideoChat qbVideoChat;
    private SignalingChannel qbVideoChatSignlaing;
    private int userId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videolayout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initViews();
        initConstraints();
        QBChatService.init(this);
        QBChatService.getInstance().loginWithUser(DataHolder.getQbUser(), this);
    }

    private void initSignaling() {
        qbVideoChatSignlaing = new SignalingChannel(QBChatService.getInstance().getPrivateChatInstance());
        qbVideoChatSignlaing.addMessageObserver(this);
    }

    private void initConstraints() {
        sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                WebRTC.RECEIVE_AUDIO, WebRTC.TRUE_FLAG));
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                WebRTC.RECEIVE_VIDEO, WebRTC.TRUE_FLAG));
    }

    private void initViews() {
        vsv= (VideoStreamsView) findViewById(R.id.videoView);
        findViewById(R.id.call).setOnClickListener(this);
        findViewById(R.id.accept).setOnClickListener(this);
        findViewById(R.id.reject).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
        QBUser qbUser = DataHolder.getQbUser();
        String userName = qbUser.getId() == Splash.BOB_USER_ID ? Splash.SAM_NAME : Splash.BOB_NAME;
        int opponentId = qbUser.getId() == Splash.BOB_USER_ID ? Splash.SAM_USER_ID : Splash.BOB_USER_ID;
        ((Button) findViewById(R.id.call)).setText("call to " + userName);
        (findViewById(R.id.call)).setTag(opponentId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call: {
                int opponentId = (Integer) findViewById(R.id.call).getTag();
                qbVideoChat.call(opponentId);
                break;
            }
            case R.id.accept: {
                qbVideoChat.accept(userId);
                enableAcceptView(false);
                break;
            }
            case R.id.reject: {
                qbVideoChat.reject(userId);
                enableAcceptView(false);
                break;
            }
            case R.id.stop: {
                qbVideoChat.stopCall();
                break;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (qbVideoChat != null) {
            qbVideoChat.onActivityPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (qbVideoChat != null) {
            qbVideoChat.onActivityResume();
        }
    }


    @Override
    protected void onDestroy() {
        disconnectAndExit();
        super.onDestroy();
    }

    // Poor-man's assert(): die with |msg| unless |condition| is true.
    private static void abortUnless(boolean condition, String msg) {
        if (!condition) {
            throw new RuntimeException(msg);
        }
    }

    // Disconnect from remote resources, dispose of local resources, and exit.
    private void disconnectAndExit() {
        synchronized (quit[0]) {
            if (quit[0]) {
                return;
            }
            quit[0] = true;
            if (qbVideoChat != null) {
                qbVideoChat.dispose();
            }
            QBChatService.getInstance().logout();
            QBChatService.getInstance().destroy();
        }
    }

    private void enableView(int viewId, boolean enable) {
        findViewById(viewId).setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
    }

    private void enableCallView(boolean enable) {
        enableView(R.id.call, enable);
        enableView(R.id.stop, enable);
    }

    private void enableAcceptView(boolean enable) {
        enableView(R.id.accept, enable);
        enableView(R.id.reject, enable);
    }


    // Log |msg| and Toast about it.
    private void logAndToast(String msg) {
        Log.d(TAG, msg);
        if (logToast != null) {
            logToast.cancel();
        }
        logToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        logToast.show();
    }

    @Override
    public void onCall(final int otherUserId, SessionDescription sessionDescription, long sessionId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                userId = otherUserId;
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(4000);
                enableAcceptView(true);
            }
        });
    }

    @Override
    public void onAccepted(int fromUserid, SessionDescription sessionDescription, long sessionId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("onAccept");
            }
        });
    }

    @Override
    public void onStop(int fromUserid, String reason, long sesison) {

    }

    @Override
    public void onRejected(int fromUserid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("Reject call");
            }
        });
    }

    @Override
    public void onSuccess(Void result, Bundle params) {

    }

    @Override
    public void onSuccess() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enableView(R.id.progressLayout, false);
                enableCallView(true);
                Toast.makeText(QBRTCDemoActivity.this, "onLoginsucces", Toast.LENGTH_SHORT).show();
                initSignaling();
                qbVideoChat = new QBVideoChat(QBRTCDemoActivity.this, sdpMediaConstraints, qbVideoChatSignlaing, vsv);
            }
        });
    }

    @Override
    public void onError(final List<String> errors) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(QBRTCDemoActivity.this, "errors in connection " + errors.toString(), Toast.LENGTH_SHORT).show();
                enableView(R.id.progressLayout, false);
                enableCallView(false);
            }
        });
    }
}