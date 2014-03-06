package com.quickblox.sample.video_webrtc;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.quickblox.module.chat.listeners.SessionListener;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.module.videochat_webrtc.ISignalingChannel;
import com.quickblox.module.videochat_webrtc.QBVideoChat;
import com.quickblox.module.videochat_webrtc.SignalingChannel;
import com.quickblox.module.videochat_webrtc.VideoStreamsView;
import java.util.LinkedList;

/**
 * Created by vadim on 26.02.14.
 */
public class QBRTCDemoActivity extends Activity implements SessionListener, View.OnClickListener, ISignalingChannel.MessageObserver {

    private static final String TAG = QBRTCDemoActivity.class.getSimpleName();
    private VideoStreamsView vsv;
    private Toast logToast;
    private LinkedList<IceCandidate> queuedRemoteCandidates =
            new LinkedList<IceCandidate>();
    // Synchronize on quit[0] to avoid teardown-related crashes.
    private final Boolean[] quit = new Boolean[] { false };
    private MediaConstraints sdpMediaConstraints;
    QBVideoChat qbVideoChat;
    private MediaRecorder.VideoSource videoSource;
    private SignalingChannel qbVideoChatSignlaing;
    private ProgressBar progressBar;
    private int userId;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(
                new UnhandledExceptionHandler(this));
        setContentView(R.layout.videolayout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        abortUnless(PeerConnectionFactory.initializeAndroidGlobals(this),
                "Failed to initializeAndroidGlobals");
        AudioManager audioManager =
                ((AudioManager) getSystemService(AUDIO_SERVICE));
        // TODO(fischman): figure out how to do this Right(tm) and remove the
        // suppression.
        @SuppressWarnings("deprecation")
        boolean isWiredHeadsetOn = audioManager.isWiredHeadsetOn();
        audioManager.setMode(isWiredHeadsetOn ?
                AudioManager.MODE_IN_CALL : AudioManager.MODE_IN_COMMUNICATION);
        audioManager.setSpeakerphoneOn(!isWiredHeadsetOn);

        sdpMediaConstraints = new MediaConstraints();
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveAudio", "true"));
        sdpMediaConstraints.mandatory.add(new MediaConstraints.KeyValuePair(
                "OfferToReceiveVideo", "true"));
        qbVideoChatSignlaing = new SignalingChannel(this, this);
        qbVideoChatSignlaing.addMessageObserver(this);
        qbVideoChatSignlaing.login(DataHolder.getQbUser());
        initViews();
    }

    private void initViews(){
        findViewById(R.id.call).setOnClickListener(this);
        findViewById(R.id.accept).setOnClickListener(this);
        findViewById(R.id.reject).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
        vsv = (VideoStreamsView) findViewById(R.id.videoView);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        QBUser qbUser = DataHolder.getQbUser();
        String userName = qbUser.getId() == Splash.BOB_USER_ID ? Splash.SAM_NAME :Splash.BOB_NAME;
        int opponentId = qbUser.getId() == Splash.BOB_USER_ID ? Splash.SAM_USER_ID :Splash.BOB_USER_ID;
        ((Button)findViewById(R.id.call)).setText("call to "+ userName);
        (findViewById(R.id.call)).setTag(opponentId);
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "Onclick");
        switch (v.getId()){
            case R.id.call:{
                int opponentId = (Integer)findViewById(R.id.call).getTag();
                qbVideoChat.call(opponentId);
                break;
            }
            case R.id.accept:{
                qbVideoChat.accept(userId);
                enableAcceptView(false);
                break;
            }
            case R.id.reject:{
                qbVideoChat.reject(userId);
                enableAcceptView(false);
                break;
            }
            case R.id.stop:{
                qbVideoChat.stopCall();
                break;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        vsv.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        vsv.onResume();
    }


    @Override
    protected void onDestroy() {
        disconnectAndExit();
        if(qbVideoChat != null){
            qbVideoChat.dispose();
        }
        if(qbVideoChatSignlaing != null){
            qbVideoChatSignlaing.disconnect();
        }
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
        }
    }

    @Override
    public void onLoginSuccess() {
        Log.i(TAG, "onLoginSuccess");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                enableView(R.id.progressLayout, false);
                enableCallView(true);
                Toast.makeText(QBRTCDemoActivity.this, "onLoginsucces", Toast.LENGTH_SHORT).show();
                qbVideoChat = new QBVideoChat(QBRTCDemoActivity.this, sdpMediaConstraints, qbVideoChatSignlaing,  vsv );
            }
        });
    }

    private void enableView(int viewId, boolean enable){
        findViewById(viewId).setVisibility(enable ? View.VISIBLE:View.INVISIBLE);
    }

    private void enableCallView(boolean enable){
        enableView(R.id.call, enable);
        enableView(R.id.stop, enable);
    }

    private void enableAcceptView(boolean enable){
        enableView(R.id.accept, enable);
        enableView(R.id.reject, enable);
    }

    @Override
    public void onLoginError() {
        Log.i(TAG, "onLoginError");
        enableView(R.id.progressLayout, false);
        enableCallView(false);
        //Toast.makeText(this, "onLoginsucces", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onDisconnectOnError(Exception e) {

    }


    @Override
    public void onCall(final String fromUserid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("call to you from "+fromUserid);
                userId = Integer.parseInt(fromUserid);
                Vibrator vibrator = (Vibrator) getSystemService(QBRTCDemoActivity.this. VIBRATOR_SERVICE);
                vibrator.vibrate(4000);
                enableAcceptView(true);
            }
        });
    }

    @Override
    public void onAccepted(String fromUserid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("onAccept");
            }
        });
    }

    @Override
    public void onStop(String fromUserid) {

    }

    @Override
    public void onRejected(String fromUserid) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("Reject call");
            }
        });
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

}