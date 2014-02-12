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

import static com.quickblox.sample.user.definitions.Consts.EMPTY_STRING;

/**
 * Created with IntelliJ IDEA.
 * User: android
 * Date: 29.11.12
 * Time: 17:09
 */
public class UpdateUserActivity extends Activity implements QBEntityCallback<QBUser> {

    ProgressDialog progressDialog;
    EditText login;
    EditText password;
    EditText email;
    EditText fullName;
    EditText phone;
    EditText webSite;
    EditText tags;

    @Override
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        setContentView(R.layout.update_user);
        initialize();
    }

    private void initialize() {

        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);
        fullName = (EditText) findViewById(R.id.full_name);
        phone = (EditText) findViewById(R.id.phone);
        webSite = (EditText) findViewById(R.id.web_site);
        tags = (EditText) findViewById(R.id.tags);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        fillAllFields();
    }

    private void fillAllFields() {
        login.setText(DataHolder.getDataHolder().getSignInUserLogin());
        email.setText(DataHolder.getDataHolder().getSignInUserEmail());
        fullName.setText(DataHolder.getDataHolder().getSignInUserFullName());
        phone.setText(DataHolder.getDataHolder().getSignInUserPhone());
        webSite.setText(DataHolder.getDataHolder().getSignInUserWebSite());
        tags.setText(DataHolder.getDataHolder().getSignInUserTags());
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_btn:
                progressDialog.show();
                // call query to update user
                QBManager.updateUser(DataHolder.getDataHolder().getSignInUserId(), login.getText().toString(),
                        DataHolder.getDataHolder().getSignInUserOldPassword(), password.getText().toString(), fullName.getText().toString(),
                        email.getText().toString(), phone.getText().toString(), webSite.getText().toString(), tags.getText().toString(), this);
                break;
        }
    }

    @Override
    public void onSuccess(QBUser qbUser, Bundle bundle) {
        progressDialog.hide();
        DataHolder.getDataHolder().setSignInQbUser(qbUser);
        if (!EMPTY_STRING.equals(password)) {
            DataHolder.getDataHolder().setSignInUserPassword(password.getText().toString());
        }
        Toast.makeText(getBaseContext(), getResources().getString(R.string.user_successfully_updated), Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(List<String> errors) {
        // print errors that came from server
        progressDialog.hide();
        Toast.makeText(getBaseContext(), errors.toString(), Toast.LENGTH_SHORT).show();
    }
}
