package com.quickblox.sample.user.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.auth.model.QBSession;
import com.quickblox.module.users.QBUsers;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.sample.user.R;
import com.quickblox.sample.user.helper.DataHolder;

import java.util.ArrayList;
import java.util.List;

import static com.quickblox.sample.user.definitions.Consts.*;

/**
 * Created with IntelliJ IDEA.
 * User: android
 * Date: 03.12.12
 * Time: 12:38
 * To change this template use File | Settings | File Templates.
 */
public class SplashActivity extends Activity implements QBEntityCallback<QBSession> {


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
        QBSettings.getInstance().fastConfigInit(APP_ID, AUTH_KEY, AUTH_SECRET);
        // Authorize application
        QBAuth.createSession(this);
    }


    private void getAllUser() {
        // Get all users for the current app
        QBUsers.getUsers(null, new QBEntityCallbackImpl<ArrayList<QBUser>>() {

            @Override
            public void onSuccess(ArrayList<QBUser> users, Bundle args) {
                DataHolder.getDataHolder().setQbUserList(users);
                startGetAllUsersActivity();
            }

            @Override
            public void onError(List<String> errors) {
                Toast.makeText(getBaseContext(), errors.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startGetAllUsersActivity() {
        Intent intent = new Intent(this, GetAllUsersActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onSuccess(QBSession session, Bundle bundle) {
        getAllUser();
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
