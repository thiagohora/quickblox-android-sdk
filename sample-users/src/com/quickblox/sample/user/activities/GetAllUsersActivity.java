package com.quickblox.sample.user.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.sample.user.R;
import com.quickblox.sample.user.adapter.UserListAdapter;
import com.quickblox.sample.user.definitions.QBQueries;
import com.quickblox.sample.user.helper.DataHolder;
import com.quickblox.sample.user.managers.QBManager;

import java.util.List;

import static com.quickblox.sample.user.definitions.Consts.POSITION;

public class GetAllUsersActivity extends Activity implements QBEntityCallback<Void>, AdapterView.OnItemClickListener {

    private static final String TAG = GetAllUsersActivity.class.getSimpleName();
    UserListAdapter userListAdapter;
    ListView userList;
    ProgressDialog progressDialog;
    Button logOut;
    Button signIn;
    Button selfEdit;
    Button singUp;
    QBQueries qbQueryType = QBQueries.QB_QUERY_LOG_OUT_QB_USER;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);
        initialize();
        userList.setOnItemClickListener(this);
    }

    private void initialize() {
        logOut = (Button) findViewById(R.id.logout);
        signIn = (Button) findViewById(R.id.sign_in);
        selfEdit = (Button) findViewById(R.id.self_edit);
        singUp = (Button) findViewById(R.id.sign_up);
        userList = (ListView) findViewById(R.id.user_list);
        userListAdapter = new UserListAdapter(this);
        userList.setAdapter(userListAdapter);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DataHolder.getDataHolder().getSignInQbUser() != null) {
            signIn.setVisibility(View.INVISIBLE);
            singUp.setVisibility(View.INVISIBLE);
            logOut.setVisibility(View.VISIBLE);
            selfEdit.setVisibility(View.VISIBLE);
        }
        userListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        qbQueryType = QBQueries.QB_QUERY_DELETE_QB_USER;
        // destroy session after app close
        QBAuth.deleteSession(this);
    }


    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.sign_in:
                intent = new Intent(this, SignInActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.sign_up:
                intent = new Intent(this, SignUpUserActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                progressDialog.show();
                // call query to sign out by current user
                QBManager.signOut(this);
                break;
            case R.id.self_edit:
                intent = new Intent(this, UpdateUserActivity.class);
                startActivity(intent);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            signIn.setVisibility(View.INVISIBLE);
            logOut.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Intent intent = new Intent(this, ShowUserActivity.class);
        intent.putExtra(POSITION, position);
        startActivity(intent);
    }

    //be careful Void is just generic stub and value is null!
    @Override
    public void onSuccess(Void aVoid, Bundle bundle) {
        closeDlg();
        switch (qbQueryType) {
            case QB_QUERY_LOG_OUT_QB_USER:
                Toast.makeText(getBaseContext(), getResources().getString(R.string.user_log_out_msg), Toast.LENGTH_SHORT).show();
                // set SignInQbUser null after logOut
                DataHolder.getDataHolder().setSignInQbUser(null);
                signIn.setVisibility(View.VISIBLE);
                logOut.setVisibility(View.INVISIBLE);
                selfEdit.setVisibility(View.INVISIBLE);
                singUp.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void closeDlg(){
        if(progressDialog!= null && progressDialog.isShowing()){
            progressDialog.hide();
        }
    }

    @Override
    public void onSuccess() {
    }

    @Override
    public void onError(List<String> errors) {
        closeDlg();
        Toast.makeText(getBaseContext(), errors.toString(), Toast.LENGTH_SHORT).show();
    }
}
