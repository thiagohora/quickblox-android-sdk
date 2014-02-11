package com.quickblox.customobject.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.customobject.R;
import com.quickblox.customobject.helper.DataHolder;
import com.quickblox.module.custom.QBCustomObjects;
import com.quickblox.module.custom.model.QBCustomObject;

import java.util.HashMap;
import java.util.List;

import static com.quickblox.customobject.definition.Consts.*;

public class AddNewNoteActivity extends Activity implements QBEntityCallback<QBCustomObject> {

    private EditText note;
    private EditText comments;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_note);
        initialize();
    }

    private void initialize() {
        note = (EditText) findViewById(R.id.note);
        comments = (EditText) findViewById(R.id.comments);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save:
                createNewNote();
                break;
        }
    }

    private void createNewNote() {
        // create new score in note class
        showProgressDialog();
        HashMap<String, Object> fields = new HashMap<String, Object>();
        fields.put(TITLE, note.getText().toString());
        fields.put(COMMENTS, comments.getText().toString());
        fields.put(STATUS, STATUS_NEW);
        QBCustomObject qbCustomObject = new QBCustomObject();
        qbCustomObject.setClassName(CLASS_NAME);
        qbCustomObject.setFields(fields);
        QBCustomObjects.createObject(qbCustomObject, this);
    }

    private void showProgressDialog() {
        progressDialog = ProgressDialog.show(this, null, getResources().getString(R.string.please_wait), false, false);
    }

    @Override
    public void onSuccess(QBCustomObject qbCustomObject, Bundle bundle) {
        progressDialog.dismiss();
        DataHolder.getDataHolder().addNoteToList(qbCustomObject);
        finish();
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError(List<String> errors) {
        // print errors that came from server
        Toast.makeText(getBaseContext(), errors.toString(), Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
    }
}
