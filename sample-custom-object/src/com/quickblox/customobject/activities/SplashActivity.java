package com.quickblox.customobject.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.customobject.R;
import com.quickblox.customobject.definition.Consts;
import com.quickblox.customobject.helper.DataHolder;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.auth.model.QBSession;
import com.quickblox.module.custom.QBCustomObjects;
import com.quickblox.module.custom.model.QBCustomObject;
import com.quickblox.module.users.model.QBUser;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends Activity implements QBEntityCallback<QBSession> {

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
        QBUser qbUser = new QBUser(USER_LOGIN, USER_PASSWORD);
        // authorize app with default user
        QBAuth.createSession(qbUser, this);

    }

    private void getNoteList() {
        // ================= QuickBlox ===== Step 2 =================
        // Get all notes
        QBCustomObjects.getObjects(Consts.CLASS_NAME, new QBEntityCallbackImpl<ArrayList<QBCustomObject>>(){
            @Override
            public void onSuccess(ArrayList<QBCustomObject> customObjects, Bundle args) {
                if (DataHolder.getDataHolder().size() > 0) {
                    DataHolder.getDataHolder().clear();
                }
                if (customObjects != null && customObjects.size() != 0) {
                    for (QBCustomObject customObject : customObjects) {
                        DataHolder.getDataHolder().addNoteToList(customObject);
                    }
                }
                startDisplayNoteListActivity();
            }
        });
    }

    private void startDisplayNoteListActivity() {
        Intent intent = new Intent(this, DisplayNoteListActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSuccess(QBSession session, Bundle bundle) {
        DataHolder.getDataHolder().setSignInUserId(session.getUserId());
        getNoteList();
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(List<String> errors) {
        Toast.makeText(getBaseContext(), errors.toString(), Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.INVISIBLE);
    }
}
