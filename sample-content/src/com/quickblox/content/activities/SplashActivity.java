package com.quickblox.content.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import com.quickblox.content.R;
import com.quickblox.content.helper.DataHolder;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.internal.core.request.QBPagedRequestBuilder;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.auth.model.QBSession;
import com.quickblox.module.content.QBContent;
import com.quickblox.module.content.model.QBFile;
import com.quickblox.module.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends Activity {

    private final int APP_ID = 99;
    private final String AUTH_KEY = "63ebrp5VZt7qTOv";
    private final String AUTH_SECRET = "YavMAxm5T59-BRw";
    private final String USER_LOGIN = "bobbobbob";
    private final String USER_PASSWORD = "bobbobbob";
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        // ================= QuickBlox ===== Step 1 =================
        // Initialize QuickBlox application with credentials.
        // Getting app credentials -- http://quickblox.com/developers/Getting_application_credentials
        QBSettings.getInstance().fastConfigInit(String.valueOf(APP_ID), AUTH_KEY, AUTH_SECRET);
        authorizeApp();
    }

    private void authorizeApp() {
        QBUser qbUser = new QBUser(USER_LOGIN, USER_PASSWORD);
        // authorize app with default user
        QBAuth.createSession(qbUser, new QBEntityCallbackImpl<QBSession>(){

            @Override
            public void onSuccess(QBSession session, Bundle args) {
                DataHolder.getDataHolder().setSignInUserId(session.getUserId());
                getFileList();
            }

            @Override
            public void onError(List<String> errors) {
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void getFileList() {

        // ================= QuickBlox ===== Step 2 =================
        // Gey all user's files

        QBPagedRequestBuilder builder = new  QBPagedRequestBuilder();
        builder.setPerPage(100);
        builder.setPage(1);
        QBContent.getFiles(builder, new QBEntityCallbackImpl<ArrayList<QBFile>>() {

            @Override
            public void onSuccess(ArrayList<QBFile> files, Bundle bundle) {
                DataHolder.getDataHolder().setQbFileList(files);
                startGalleryActivity();
            }

        });
    }

    private void startGalleryActivity() {
        Intent intent = new Intent(this, GalleryActivity.class);
        startActivity(intent);
        finish();
    }
}
