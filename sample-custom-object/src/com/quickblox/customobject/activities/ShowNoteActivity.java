package com.quickblox.customobject.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.customobject.R;
import com.quickblox.customobject.definition.QBQueries;
import com.quickblox.customobject.helper.DataHolder;
import com.quickblox.customobject.object.Note;
import com.quickblox.module.custom.QBCustomObjects;
import com.quickblox.module.custom.model.QBCustomObject;

import java.util.HashMap;
import java.util.List;

import static com.quickblox.customobject.definition.Consts.*;

public class ShowNoteActivity extends Activity implements QBEntityCallback<QBCustomObject> {

    private final String POSITION = "position";
    private TextView title;
    private TextView status;
    private EditText comments;
    private int position;
    private ProgressDialog progressDialog;
    QBQueries qbQueryType =  QBQueries.UPDATE_STATUS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note);
        initialize();
    }

    private void initialize() {
        position = getIntent().getIntExtra(POSITION, 0);
        title = (TextView) findViewById(R.id.note);
        status = (TextView) findViewById(R.id.status);
        comments = (EditText) findViewById(R.id.comments);
        fillFields();
    }

    private void fillFields() {
        title.setText(DataHolder.getDataHolder().getNoteTitle(position));
        status.setText(DataHolder.getDataHolder().getNoteStatus(position));
        applyComment();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_comment:
                showAddNewCommentDialog();
                break;
            case R.id.change_status:
                showSetNewStatusDialog();
                break;
            case R.id.delete:
                showProgressDialog();
                // create query for delete score
                // set className and scoreId
                QBCustomObjects.deleteObject(CLASS_NAME, DataHolder.getDataHolder().getNoteId(position), new QBEntityCallbackImpl<Void>(){
                    @Override
                    public void onSuccess() {
                        DataHolder.getDataHolder().removeNoteFromList(position);
                        Toast.makeText(getBaseContext(), getBaseContext().getResources().getString(R.string.note_successfully_deleted), Toast.LENGTH_SHORT).show();
                        finish();
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onError(List<String> errors) {
                        Toast.makeText(getBaseContext(), errors.toString(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
                break;
        }

    }

    private void showAddNewCommentDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getResources().getString(R.string.new_comment));
        alert.setMessage(getResources().getString(R.string.write_new_comment));
        final EditText input = new EditText(this);
        alert.setView(input);
        input.setSingleLine();
        alert.setPositiveButton(getBaseContext().getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int buttonNumber) {
                showProgressDialog();
                addNewComment(input.getText().toString());
                dialog.cancel();
            }
        });

        alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    private void showSetNewStatusDialog() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final String[] statusList = {STATUS_NEW, STATUS_IN_PROCESS, STATUS_DONE};
        alert.setTitle(getBaseContext().getResources().getString(R.string.choose_new_status));

        alert.setItems(statusList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                showProgressDialog();
                String status;
                if (item == 0) {
                    status = STATUS_NEW;
                } else if (item == 1) {
                    status = STATUS_IN_PROCESS;
                } else {
                    status = STATUS_DONE;
                }
                updateNoteStatus(status);
            }
        });
        alert.show();
    }

    private void updateNoteStatus(String status) {
        // create query for update note status
        // set class name , status
        HashMap<String, Object> fields = new HashMap<String, Object>();
        fields.put(STATUS, status);
        QBCustomObject qbCustomObject = new QBCustomObject();
        qbCustomObject.setCustomObjectId(DataHolder.getDataHolder().getNoteId(position));
        qbCustomObject.setClassName(CLASS_NAME);
        qbCustomObject.setFields(fields);
        qbQueryType =  QBQueries.UPDATE_STATUS;
        QBCustomObjects.updateObject(qbCustomObject, this);
    }

    private void addNewComment(String comment) {
        DataHolder.getDataHolder().addNewComment(position, comment);
        // create query for update note status
        // set class name
        // add new comments
        HashMap<String, Object> fields = new HashMap<String, Object>();
        fields.put(COMMENTS, DataHolder.getDataHolder().getComments(position));
        QBCustomObject qbCustomObject = new QBCustomObject();
        qbCustomObject.setCustomObjectId(DataHolder.getDataHolder().getNoteId(position));
        qbCustomObject.setClassName(CLASS_NAME);
        qbCustomObject.setFields(fields);
        qbQueryType =  QBQueries.ADD_NEW_COMMENT;
        QBCustomObjects.updateObject(qbCustomObject, this);
    }

    private void applyComment() {
        String commentsStr = "";
        for (int i = 0; i < DataHolder.getDataHolder().getNoteComments(position).size(); ++i) {
            commentsStr += "#" + i + "-" + DataHolder.getDataHolder().getNoteComments(position).get(i) + "\n\n";
        }
        comments.setText(commentsStr);
    }

    private void setNewNote(QBCustomObject qbCustomObject) {
        Note note = new Note(qbCustomObject);
        DataHolder.getDataHolder().setNoteToNoteList(position, note);
    }

    private void showProgressDialog() {
        progressDialog = ProgressDialog.show(this, null, getResources().getString(R.string.please_wait), false, false);
    }

    @Override
    public void onSuccess(QBCustomObject qbCustomObject, Bundle bundle) {
        switch (qbQueryType) {
            case UPDATE_STATUS:
                // return QBCustomObjectResult for updateObject()
                setNewNote(qbCustomObject);
                status.setText(DataHolder.getDataHolder().getNoteStatus(position));
                break;
            case ADD_NEW_COMMENT:
                // return QBCustomObjectResult for updateObject()
                setNewNote(qbCustomObject);
                applyComment();
                break;
        }
        progressDialog.dismiss();
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

