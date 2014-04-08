package com.quickblox.sample.video_webrtc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.module.videochat.model.objects.CallType;
import com.quickblox.module.videochat_webrtc.*;
import com.quickblox.module.videochat_webrtc.render.VideoStreamsView;
import com.quickblox.module.videochat_webrtc.ISignalingChannel;
import com.quickblox.module.videochat_webrtc.SignalingChannel;
import org.webrtc.MediaConstraints;
import org.webrtc.SessionDescription;

import java.util.List;

public class QBRTCDemoActivity extends Activity implements QBEntityCallback<Void>, View.OnClickListener, ISignalingChannel.MessageObserver {

    private static final String TAG = QBRTCDemoActivity.class.getSimpleName();
    private VideoStreamsView vsv;
    private Toast logToast;
    // Synchronize on quit[0] to avoid teardown-related crashes.
    private final Boolean[] quit = new Boolean[]{false};
    private MediaConstraints sdpMediaConstraints;
    QBVideoChat qbVideoChat;
    private SignalingChannel qbVideoChatSignlaing;
    private QBUser opponent;
    private SessionDescription sdp;
    ProgressDialog progressDialog;

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
        vsv = (VideoStreamsView) findViewById(R.id.videoView);
        findViewById(R.id.call).setOnClickListener(this);
        findViewById(R.id.accept).setOnClickListener(this);
        findViewById(R.id.reject).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
        findViewById(R.id.muteMicrophone).setOnClickListener(this);
        findViewById(R.id.turnSound).setOnClickListener(this);
        QBUser qbUser = DataHolder.getQbUser();
        String userName = qbUser.getId() == Splash.BOB_USER_ID ? Splash.SAM_NAME : Splash.BOB_NAME;
        int opponentId = qbUser.getId() == Splash.BOB_USER_ID ? Splash.SAM_USER_ID : Splash.BOB_USER_ID;
        opponent = new QBUser(opponentId);
        opponent.setFullName(userName);
        ((Button) findViewById(R.id.call)).setText("call to " + userName);
        (findViewById(R.id.call)).setTag(opponent);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait. Connecting to chat...");
        progressDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call: {
                if (QBVideoChat.VIDEO_CHAT_STATE.INACTIVE.equals(qbVideoChat.getState())) {
                    opponent = (QBUser) findViewById(R.id.call).getTag();
                    qbVideoChat.call(opponent, CallType.VIDEO);
                } else {
                    logAndToast("Stop current chat before call");
                }
                break;
            }
            case R.id.accept: {
                qbVideoChat.setRemoteSessionDescription(sdp);
                qbVideoChat.accept(opponent);
                enableAcceptView(false);
                break;
            }
            case R.id.reject: {
                qbVideoChat.reject(opponent);
                enableAcceptView(false);
                break;
            }
            case R.id.stop: {
                qbVideoChat.stopCall();
                break;
            }
            case R.id.muteMicrophone: {
                qbVideoChat.muteMicrophone(!qbVideoChat.isMicrophoneMute());
                break;
            }
            case R.id.turnSound: {
                qbVideoChat.muteSound(!qbVideoChat.isSoundMute());
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
    public void onCall(final QBUser otherUser, CallType callType, final SessionDescription sessionDescription, long sessionId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                opponent = otherUser;
                logAndToast("call from user " + opponent.getFullName());
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(4000);
                enableAcceptView(true);
                sdp = sessionDescription;
            }
        });
    }

    @Override
    public void onAccepted(QBUser otherUser, SessionDescription sessionDescription, long sessionId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("Call accepted");
            }
        });
    }

    @Override
    public void onStop(QBUser otherUser, String reason, long sesison) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("Participant closed connection");
            }
        });
    }

    @Override
    public void onRejected(QBUser otherUser, long sesison) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("Reject call");
                qbVideoChat.dispose();
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
                cancelDlg();
                enableCallView(true);
                initSignaling();
                qbVideoChat = new QBVideoChat(QBRTCDemoActivity.this, sdpMediaConstraints, qbVideoChatSignlaing, vsv);
            }
        });
    }

    private void cancelDlg() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onError(final List<String> errors) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cancelDlg();
                Toast.makeText(QBRTCDemoActivity.this, "errors in connection " + errors.toString(), Toast.LENGTH_SHORT).show();
                enableCallView(false);
            }
        });
    }
}