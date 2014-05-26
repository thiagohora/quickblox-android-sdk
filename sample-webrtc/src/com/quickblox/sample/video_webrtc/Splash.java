package com.quickblox.sample.video_webrtc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.TransferProtocol;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.auth.model.QBSession;
import com.quickblox.module.users.QBUsers;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.sample.video_webrtc.R;

import java.util.List;

/**
 * Created by vadim on 24.02.14.
 */
public class Splash extends Activity {

    private static final String APP_ID = "22";
    private static final String AUTH_KEY = "xGRzM9GDkuC5RB2";
    private static final String AUTH_SECRET = "bM3WqUc5Kyu3mWE";

    ProgressDialog progressDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);


        QBSettings.getInstance().fastConfigInit(APP_ID, AUTH_KEY, AUTH_SECRET);

         /* Stage */
        QBSettings.getInstance().setServerApiDomain("api.stage.quickblox.com");
        QBSettings.getInstance().setChatServerDomain("chatstage.quickblox.com");
        QBSettings.getInstance().setContentBucketName("blobs-test-oz");
        QBSettings.getInstance().setTransferProtocol(TransferProtocol.HTTP);

        findViewById(R.id.loginButtonChat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login = ((EditText)findViewById(R.id.login)).getText().toString();
                String pswd = ((EditText)findViewById(R.id.pswd)).getText().toString();
                signinwithUser(login, pswd);
            }
        });
        QBAuth.createSession(sessionCallback);
    }

    private void signinwithUser(String login, String pswd) {
        qbUser = new QBUser();
        qbUser.setLogin(login);
        qbUser.setPassword(pswd);
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        //QBAuth.createSession(qbUser, sessionCallback);
        QBUsers.signIn(qbUser, userCallback);
    }


    private QBEntityCallback<QBUser> userCallback = new QBEntityCallbackImpl<QBUser>() {
        @Override
        public void onSuccess(QBUser user, Bundle args) {
            progressDialog.cancel();
            user.setPassword(qbUser.getPassword());
            DataHolder.setUser(user);
            Intent intent = new Intent(Splash.this, QBRTCDemoActivity.class);
            startActivity(intent);
            finish();
        }

        @Override
        public void onError(List<String> errors) {
            progressDialog.cancel();
            AlertDialog.Builder dialog = new AlertDialog.Builder(Splash.this);
            dialog.setMessage("Error(s) occurred. Look into DDMS log for details, " +
                    "please. Errors: " + errors).create().show();
        }
    };

    private QBUser qbUser;
    private QBEntityCallback<QBSession> sessionCallback = new QBEntityCallbackImpl<QBSession>() {
        @Override
        public void onSuccess(QBSession result, Bundle args) {
           findViewById(R.id.loginButtonChat).setEnabled(true);
        }

        @Override
        public void onError(List<String> errors) {
            //progressDialog.cancel();
            AlertDialog.Builder dialog = new AlertDialog.Builder(Splash.this);
            dialog.setMessage("Error(s) occurred. Look into DDMS log for details, " +
                    "please. Errors: " + errors).create().show();
        }
    };
}