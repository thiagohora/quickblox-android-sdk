package com.quickblox.sample.chat.ui.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ProgressBar;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.auth.model.QBSession;
import com.quickblox.sample.chat.R;

import java.util.List;

public class SplashActivity extends Activity {

    private static final String APP_ID = "99";
    private static final String AUTH_KEY = "63ebrp5VZt7qTOv";
    private static final String AUTH_SECRET = "YavMAxm5T59-BRw";

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        QBSettings.getInstance().fastConfigInit(APP_ID, AUTH_KEY, AUTH_SECRET);
        QBAuth.createSession(new QBEntityCallbackImpl<QBSession>(){
            @Override
            public void onSuccess(QBSession result, Bundle args) {
                Intent intent = new Intent( SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(List<String> errors) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(SplashActivity.this);
                dialog.setMessage("Error(s) occurred. Look into DDMS log for details, " +
                        "please. Errors: " + errors).create().show();
            }
        });
    }
}