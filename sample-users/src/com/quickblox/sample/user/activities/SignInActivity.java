package com.quickblox.sample.user.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.module.users.model.QBUser;
import com.quickblox.sample.user.R;
import com.quickblox.sample.user.helper.DataHolder;
import com.quickblox.sample.user.managers.QBManager;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: android
 * Date: 20.11.12
 * Time: 16:53
 */
public class SignInActivity extends Activity implements QBEntityCallback<QBUser> {

    EditText login;
    EditText password;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.sign_in);
        initialize();
    }

    private void initialize() {
        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in:
                progressDialog.show();
                // Sign in application with user.
                // You can create user on admin.quickblox.com, Users module or through QBUsers.signUp method
                QBManager.singIn(login.getText().toString(), password.getText().toString(), this);
                break;
        }
    }

    @Override
    public void onSuccess(QBUser qbUser, Bundle bundle) {
        progressDialog.hide();
        setResult(RESULT_OK);
        DataHolder.getDataHolder().setSignInQbUser(qbUser);
        // password does not come, so if you want use it somewhere else, try something like this:
        DataHolder.getDataHolder().setSignInUserPassword(password.getText().toString());
        Toast.makeText(getBaseContext(), getResources().getString(R.string.user_successfully_sign_in), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(List<String> errors) {
        Toast.makeText(getBaseContext(), errors.toString(), Toast.LENGTH_SHORT).show();
        progressDialog.hide();
    }
}
