package com.quickblox.sample.video_webrtc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.auth.model.QBSession;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.sample.video_webrtc.R;

import java.util.List;

/**
 * Created by vadim on 24.02.14.
 */
public class Splash extends Activity {

    private static final String APP_ID = "92";
    private static final String AUTH_KEY = "wJHdOcQSxXQGWx5";
    private static final String AUTH_SECRET = "BTFsj7Rtt27DAmT";

    public static final String BOB_USER = "bobbobbob";
    public static final String BOB_NAME = "bob";
    public static final int BOB_USER_ID = 298;
    public static final String SAM_USER = "samsamsam";
    public static final String SAM_NAME = "sam";
    public static final int SAM_USER_ID = 299;
    public static final int USER_ID = 999;
    ProgressDialog progressDialog;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);


        QBSettings.getInstance().fastConfigInit(APP_ID, AUTH_KEY, AUTH_SECRET);

        findViewById(R.id.loginButtonChat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qbUser = new QBUser();
                //int userid = Integer.parseInt( ((EditText) findViewById(R.id.userId)).getText().toString());
                qbUser.setLogin(BOB_USER);
                qbUser.setPassword(BOB_USER);
                qbUser.setId(BOB_USER_ID);
                signinwithUser(qbUser);
            }
        });
        findViewById(R.id.loginButtonChat1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qbUser = new QBUser();
                qbUser.setLogin(SAM_USER);
                qbUser.setPassword(SAM_USER);
                qbUser.setId(SAM_USER_ID);
                signinwithUser(qbUser);
            }
        });
    }

    private void signinwithUser(QBUser qbUser) {
        progressDialog = new ProgressDialog(this);
        progressDialog.show();
        QBAuth.createSession(qbUser, sessionCallback);
    }

    private QBUser qbUser;
    private QBEntityCallback<QBSession> sessionCallback = new QBEntityCallbackImpl<QBSession>() {
        @Override
        public void onSuccess(QBSession result, Bundle args) {
            progressDialog.cancel();
            DataHolder.setUser(qbUser);
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
}