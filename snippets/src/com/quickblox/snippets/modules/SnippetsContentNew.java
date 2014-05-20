package com.quickblox.snippets.modules;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.internal.core.helper.ContentType;
import com.quickblox.internal.core.helper.FileHelper;
import com.quickblox.internal.core.request.QBPagedRequestBuilder;
import com.quickblox.internal.module.custom.Consts;
import com.quickblox.module.content.QBContent;
import com.quickblox.module.content.model.QBFile;
import com.quickblox.module.content.model.QBFileObjectAccess;
import com.quickblox.module.content.model.amazon.PostResponse;
import com.quickblox.snippets.R;
import com.quickblox.snippets.Snippet;
import com.quickblox.snippets.Snippets;
import com.quickblox.snippets.Utils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vfite on 04.02.14.
 */
public class SnippetsContentNew extends Snippets{
    private static final String TAG = SnippetsContentNew.class.getSimpleName();

    private static final int FILE_ID = 106844;
    private static final String FILE_UID = "ce4faf98fbe84b4985570cbbbed303ab00";

    File file1 = null;
    File file2 = null;
    QBFileObjectAccess fileObjectAccess;

    public SnippetsContentNew(Context context) {
        super(context);

        snippets.add(createFileNewCallback);
        snippets.add(updateFileNewCallback);
        snippets.add(getFileWithIdNewCallback);
        snippets.add(uploadFileNewCallback);
        snippets.add(declareFileUploadNewCallback);
        snippets.add(incrementRefCountNewCallback);
        snippets.add(deleteFileNewCallback);
        snippets.add(getFileObjectAccessNewCallback);
        snippets.add(downloadFileWithUIDNewCallback);

        snippets.add(getFilesNewCallback);
        snippets.add(getTaggedListNewCallback);

        snippets.add(uploadFileTaskNewCallback);
        snippets.add(downloadFileTaskNewCallback);
        snippets.add(updateFileTaskNewCallback);

        // get file1
        int fileId = R.raw.sample_file;
        InputStream is = context.getResources().openRawResource(fileId);
        file1 = FileHelper.getFileInputStream(is, "sample_file.txt", "qb_snippets12");

        // get file1
        int fileId2 = R.raw.sample_file2;
        InputStream is2 = context.getResources().openRawResource(fileId2);
        file2 = FileHelper.getFileInputStream(is2, "sample_file2.txt", "qb_snippets12");
    }

    Snippet createFileNewCallback = new Snippet("create file") {
        @Override
        public void execute() {

            QBFile qbfile = new QBFile();
            qbfile.setName(file1.getName());
            qbfile.setPublic(true);
            qbfile.setContentType(ContentType.getContentType(file1));
            //
            QBContent.createFile(qbfile, new QBEntityCallbackImpl<QBFile>() {

                @Override
                public void onSuccess(QBFile file, Bundle params) {
                    Log.i(TAG, ">>> File" + file.toString());
                    fileObjectAccess = file.getFileObjectAccess();
                }

                @Override
                public void onError(List<String> errors) {
                      handleErrors(errors);
                }
            });
        }
    };

    Snippet updateFileNewCallback = new Snippet("update file") {
        @Override
        public void execute() {
            QBFile qbfile = new QBFile();
            qbfile.setId(20223);
            qbfile.setName("my Car1");
            QBContent.updateFile(qbfile, new QBEntityCallbackImpl<QBFile>(){

                @Override
                public void onSuccess(QBFile updatedFile, Bundle params) {
                    Log.i(TAG, ">>> File:" + updatedFile.toString());
                }


                @Override
                public void onError(List<String> errors) {
                         handleErrors(errors);
                }
            });
        }
    };

    Snippet getFileWithIdNewCallback = new Snippet("get file with id") {
        @Override
        public void execute() {
            QBContent.getFile(FILE_ID, new QBEntityCallbackImpl<QBFile>(){

                @Override
                public void onSuccess(QBFile file, Bundle params) {
                    Log.i(TAG, ">>> File:" + file.toString());
                }


                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet uploadFileNewCallback = new Snippet("upload file") {
        @Override
        public void execute() {
            String params = fileObjectAccess.getParams();   // will return from the server when creating file
            QBContent.uploadFile(file1, params, new QBEntityCallbackImpl<PostResponse>(){
                @Override
                public void onSuccess(PostResponse amazonResponse, Bundle params) {
                    Log.i(TAG, ">>> AmazonPostResponse" +amazonResponse);
                }

                @Override
                public void onError(List<String> errors) {
                        handleErrors(errors);
                }
            });
        }
    };

    Snippet declareFileUploadNewCallback = new Snippet("declare file upload") {
        @Override
        public void execute() {
            QBContent.declareFileUploaded(20237, (int) file1.length(), new QBEntityCallbackImpl() {

                @Override
                public void onSuccess() {
                    Log.i(TAG, ">>> declare file uploaded was successful");
                }

                @Override
                public void onError(List errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet incrementRefCountNewCallback = new Snippet("increment ref count") {
        @Override
        public void execute() {
            QBContent.incrementRefCount(FILE_ID, new QBEntityCallbackImpl() {

                @Override
                public void onSuccess() {
                    Log.i(TAG, ">>> count of ref increment successfully" );
                }

                @Override
                public void onError(List errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet deleteFileNewCallback = new Snippet("delete file") {
        @Override
        public void execute() {
            QBContent.deleteFile(FILE_ID, new QBEntityCallbackImpl() {

                @Override
                public void onSuccess() {
                    Log.i(TAG, ">>> file deleted successfully");
                }

                @Override
                public void onError(List errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet getFileObjectAccessNewCallback = new Snippet("get file object access") {
        @Override
        public void execute() {
            QBContent.getFileObjectAccess(FILE_ID, new QBEntityCallbackImpl<QBFileObjectAccess>() {

                @Override
                public void onSuccess(QBFileObjectAccess fileObjectAccess, Bundle params) {
                    Log.i(TAG, ">>> FileObjectAccess" + fileObjectAccess);
                }

                @Override
                public void onError(List<String> errors) {
                           handleErrors(errors);
                }
            });
        }
    };

    Snippet downloadFileWithUIDNewCallback = new Snippet("download file with UID") {
        @Override
        public void execute() {
            QBContent.downloadFile(FILE_UID, new QBEntityCallbackImpl<InputStream>() {

                @Override
                public void onSuccess(InputStream inputStream, Bundle params) {
                    byte[] content = params.getByteArray(Consts.CONTENT_TAG);       // that's downloaded file content
                    InputStream is = inputStream; // that's downloaded file content
                    String contentFromFile = Utils.getContentFromFile(inputStream);
                    Log.i(TAG, "file downloaded: "+contentFromFile);
                }

                @Override
                public void onError(List<String> errors) {
                       handleErrors(errors);
                }
            });
        }
    };

    //
    ///////////////////////////////////////////// Get files /////////////////////////////////////////////
    //
    Snippet getFilesNewCallback = new Snippet("get files") {
        @Override
        public void execute() {
            QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder(5, 2);

            QBContent.getFiles(requestBuilder, new QBEntityCallbackImpl<ArrayList<QBFile>>() {

                @Override
                public void onSuccess(ArrayList<QBFile> files, Bundle params) {
                    Log.i(TAG, ">>> File list:" + files.toString());
                }

                @Override
                public void onError(List<String> errors) {
                      handleErrors(errors);
                }
            });
        }
    };

    Snippet getTaggedListNewCallback = new Snippet("get tagged list") {
        @Override
        public void execute() {
            QBPagedRequestBuilder requestBuilder = new QBPagedRequestBuilder(20, 1);

            QBContent.getTaggedList(requestBuilder, new QBEntityCallbackImpl<ArrayList<QBFile>>() {
                @Override
                public void onSuccess(ArrayList<QBFile> files, Bundle params) {
                    Log.i(TAG, ">>> File list:" + files.toString());
                }

                @Override
                public void onError(List<String> errors) {
                           handleErrors(errors);
                }
            });
        }
    };

    //
    ///////////////////////////////////////////// Tasks /////////////////////////////////////////////
    //
    Snippet uploadFileTaskNewCallback = new Snippet("upload file task") {
        @Override
        public void execute() {

            Boolean fileIsPublic = true;
            QBContent.uploadFileTask(file1, fileIsPublic, null, new QBEntityCallbackImpl<QBFile>() {

                @Override
                public void onSuccess(QBFile qbFile, Bundle params) {
                    Log.i(TAG, ">>> QBFile:" + qbFile.toString());
                }

                @Override
                public void onError(List<String> errors) {
                      handleErrors(errors);
                }
            });
        }
    };

    Snippet downloadFileTaskNewCallback = new Snippet("download file Task") {
        final int fileId = 106844;
        @Override
        public void execute() {
            QBContent.downloadFileTask(fileId, new QBEntityCallback<InputStream>(){

                @Override
                public void onSuccess(InputStream inputStream, Bundle params) {
                    byte[] content = params.getByteArray(Consts.CONTENT_TAG);       // that's downloaded file content
                    InputStream is = inputStream; // that's downloaded file content
                    String contentFromFile = Utils.getContentFromFile(inputStream);
                    Log.i(TAG, "file downloaded: "+contentFromFile);
                }

                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(List<String> errors) {
                               handleErrors(errors);
                }
            });
        }
    };

    Snippet updateFileTaskNewCallback = new Snippet("update file Task") {
        final int fileId = 106844;
        @Override
        public void execute() {
            QBContent.updateFileTask(file2, fileId, null, new QBEntityCallbackImpl<QBFile>(){

                @Override
                public void onSuccess(QBFile qbFile, Bundle params) {
                    Log.i(TAG, ">>> file updated successful"+qbFile);
                }


                @Override
                public void onError(List<String> errors) {
                      handleErrors(errors);
                }
            });
        }
    };
}
