package com.quickblox.sample.video_webrtc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.module.videochat_webrtc.*;
import com.quickblox.module.videochat_webrtc.model.CallConfig;
import com.quickblox.module.videochat_webrtc.model.ConnectionConfig;
import com.quickblox.module.videochat_webrtc.render.VideoStreamsView;

import org.jivesoftware.smack.SmackException;
import org.webrtc.SessionDescription;

import java.util.List;

public class QBRTCDemoActivity extends Activity implements QBEntityCallback<Void>, View.OnClickListener, QBSignalingChannel.SignalingListener {

    private static final String TAG = QBRTCDemoActivity.class.getSimpleName();
    private VideoStreamsView vsv;
    private Toast logToast;
    private QBVideoChat qbVideoChat;
    private ExtensionSignalingChannel qbVideoChatSignlaing;
    private QBUser opponent;
    private SessionDescription sdp;
    private ProgressDialog progressDialog;
    private String sessionId;

    private int[] orientations = {ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT, ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE};
    private int orientationIndex = 0;
    private int orientation = orientations[orientationIndex];
    private WebRTC.MEDIA_STREAM callType;
    private CallConfig callConfig;
    private boolean cameraEnabled = true;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videolayout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initViews();
        QBChatService.setDebugEnabled(true);
        QBChatService.init(this);
        QBChatService.getInstance().login(DataHolder.getQbUser(), this);
    }

    private void initSignaling() {
        qbVideoChatSignlaing = new ExtensionSignalingChannel(
                QBChatService.getInstance().getSignalingManager(), DataHolder.getQbUser());
        qbVideoChatSignlaing.addSignalingListener(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.i(TAG, "onConfigurationChanged");
        if (qbVideoChat != null){
            qbVideoChat.onConfigurationChanged(newConfig);
        }
    }

    private void initViews() {
        vsv = (VideoStreamsView) findViewById(R.id.videoView);
        findViewById(R.id.call).setOnClickListener(this);
        findViewById(R.id.accept).setOnClickListener(this);
        findViewById(R.id.reject).setOnClickListener(this);
        findViewById(R.id.stop).setOnClickListener(this);
        findViewById(R.id.muteMicrophone).setOnClickListener(this);
        findViewById(R.id.turnCamera).setOnClickListener(this);
        findViewById(R.id.orientation).setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait. Connecting to chat...");
        progressDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call: {
                showCallDialog();
                break;
            }
            case R.id.accept: {
                accept();
                break;
            }
            case R.id.reject: {
                reject();
            }
            case R.id.stop: {
                if (qbVideoChat != null){
                    qbVideoChat.stopCall();
                }
                break;
            }
            case R.id.muteMicrophone: {
                muteMicrophone();
                break;
            }
            case R.id.turnCamera: {
                enableCamera();
                break;
            }
            case R.id.orientation: {
                changeOrientation();
            }
        }
    }

    private void muteMicrophone() {
        if (qbVideoChat != null){
            qbVideoChat.muteMicrophone(!qbVideoChat.isMicrophoneMute());
            String status = qbVideoChat.isMicrophoneMute() ? "off" : "on";
            ((Button)findViewById(R.id.muteMicrophone)).setText("Mute " + status);
        }
    }

    private void reject() {
        if (qbVideoChat != null){
            qbVideoChat.reject(opponent, sessionId);
        }
        else if (callConfig != null){
            ConnectionConfig connectionConfig = new ConnectionConfig(callConfig.getParticipant(),
                    callConfig.getConnectionSession());
            qbVideoChatSignlaing.sendReject(connectionConfig);
        }
        enableAcceptView(false);
    }

    private void enableCamera(){
        if (qbVideoChat != null){
            if (cameraEnabled) {
                qbVideoChat.disableCamera();
                cameraEnabled = false;
            }
            else{
                qbVideoChat.enableCamera();
                cameraEnabled = true;
            }
            int resource = cameraEnabled ? R.string.camera_off : R.string.camera_on;
            ((Button)findViewById(R.id.turnCamera)).setText( getString(resource) );
        }
    }

    private void accept() {
        if (qbVideoChat == null){
            initVideoChat();
        }
        logAndToast("callType="+callConfig.getCallStreamType());
        qbVideoChat.accept(callConfig);
        enableAcceptView(false);
    }

    private void startCall() {
        if (qbVideoChat == null){
            initVideoChat();
        }
        if (QBVideoChat.VIDEO_CHAT_STATE.INACTIVE.equals(qbVideoChat.getState())) {
            qbVideoChat.call(opponent, getCallType());
        } else {
            logAndToast("Stop current chat before call");
        }
    }

    private void showCallDialog() {
        final EditText userIdEditText = new EditText(this);
        userIdEditText.setHint("insert user id");
        userIdEditText.setSelection(userIdEditText.getText().length());
        DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        abortUnless(which == DialogInterface.BUTTON_POSITIVE, "lolwat?");
                        dialog.dismiss();
                        opponent = new QBUser();
                        int id =0;
                        try {
                            id = Integer.parseInt(userIdEditText.getText().toString());
                        }
                        catch (NumberFormatException exc){
                            exc.printStackTrace();
                        }
                        if (id !=0) {
                            opponent.setId(id);
                            startCall();
                        }
                    }
                };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage("Enter user id").setView(userIdEditText)
                .setPositiveButton("Call!", listener).show();
    }

    private void initVideoChat(){
        qbVideoChat = new QBVideoChat(QBRTCDemoActivity.this, qbVideoChatSignlaing, vsv);
        qbVideoChat.setMediaCaptureCallback( new QBVideoChat.MediaCaptureCallback() {
            @Override
            public void onCaptureFail(WebRTC.MEDIA_STREAM mediaStream, String problem) {
                logAndToast(problem);
            }

            @Override
            public void onCaptureSuccess(WebRTC.MEDIA_STREAM mediaStream) {

            }
        });
    }

    private WebRTC.MEDIA_STREAM getCallType(){
        int selectedId = ((RadioGroup)findViewById(R.id.callMode)).getCheckedRadioButtonId();
        return (R.id.audio_call ==selectedId) ? WebRTC.MEDIA_STREAM.AUDIO :WebRTC.MEDIA_STREAM.VIDEO;

    }

    public void changeOrientation() {
        orientationIndex++;
        if (orientationIndex >= orientations.length) {
            orientationIndex = 0;
        }
        orientation = orientations[orientationIndex];
        setRequestedOrientation(orientation);
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

    // Disconnect from remote resources, disposeConnection of local resources, and exit.
    private void disconnectAndExit() {
        if (qbVideoChat != null) {
            if (!QBVideoChat.VIDEO_CHAT_STATE.INACTIVE.equals(qbVideoChat.getState()) ){
                qbVideoChat.disposeConnection();
            }
            qbVideoChat.clean();
            qbVideoChatSignlaing.close();
        }
        try {
            QBChatService.getInstance().logout();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        QBChatService.getInstance().destroy();
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
    public void onCall(final ConnectionConfig connectionConfig) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                opponent = connectionConfig.getParticipant();
                logAndToast("call from user " + opponent.getFullName());
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(4000);
                enableAcceptView(true);
                QBRTCDemoActivity.this.sessionId = connectionConfig.getConnectionSession();
                sdp = ((CallConfig)connectionConfig).getSessionDescription();
                callType = ((CallConfig)connectionConfig).getCallStreamType();
                callConfig =  (CallConfig)connectionConfig;
            }
        });
    }

    @Override
    public void onIceCandidate(ConnectionConfig connectionConfig) {

    }

    @Override
    public void onAccepted(final ConnectionConfig connectionConfig) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("Call accepted");
            }
        });
    }

    @Override
    public void onParametersChanged(final ConnectionConfig connectionConfig) {

    }

    @Override
    public void onStop(final ConnectionConfig connectionConfig) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("Participant closed connection");
                qbVideoChat.disposeConnection();
            }
        });
    }

    @Override
    public void onRejected(final ConnectionConfig connectionConfig) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("Reject call");
                qbVideoChat.disposeConnection();
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
            }
        });
    }

    private void cancelDlg() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onClosed(String msg) {

    }

    @Override
    public void onError(final List<String> errors) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cancelDlg();
                Toast.makeText(QBRTCDemoActivity.this, "errors in connection " + errors.toString(),
                        Toast.LENGTH_SHORT).show();
                enableCallView(false);
            }
        });
    }
}