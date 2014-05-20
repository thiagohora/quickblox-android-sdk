package com.quickblox.snippets.modules;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.internal.core.exception.QBResponseException;
import com.quickblox.internal.core.helper.StringifyArrayList;
import com.quickblox.internal.module.custom.Consts;
import com.quickblox.internal.module.custom.request.QBCustomObjectRequestBuilder;
import com.quickblox.internal.module.custom.request.QBCustomObjectUpdateBuilder;
import com.quickblox.module.custom.QBCustomObjects;
import com.quickblox.module.custom.QBCustomObjectsFiles;
import com.quickblox.module.custom.model.QBCustomObject;
import com.quickblox.module.custom.model.QBCustomObjectFileField;
import com.quickblox.module.custom.model.QBPermissions;
import com.quickblox.snippets.*;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * Created by vfite on 22.01.14.
 */
public class SnippetsCONew extends Snippets{

    private static final String TAG = SnippetsCONew.class.getSimpleName();
    // Define custom object model in QB Admin Panel
    // http://quickblox.com/developers/Custom_Objects
    //
    private final String CLASS_NAME = "SuperSample";
    private final String RATING_FIELD = "rating";
    private final String DESCRIPTION_FIELD = "description";
    private final String AVATAR_FIELD = "avatar";
    File file1 = null;
    File file2 = null;

    private final String NOTE1_ID = "51ed45c8535c120845008fcd";

    public SnippetsCONew(Context context) {
        super(context);

        snippets.add(createCustomObjectNewCallback);
        snippets.add(createCustomObjectsNewCallback);
        snippets.add(deleteCustomObjectsSynchronous);
        snippets.add(deleteCustomObjectNewCallback);
        snippets.add(deleteCustomObjectsNewCallback);
        snippets.add(getCustomObjectsNewCallback);
        snippets.add(getCustomObjectsSynchronous);
        snippets.add(updateCustomObjectNewCallback);
        snippets.add(getCountCustomsObjectNewCallback);
        snippets.add(getGetCustomObjectsByIdsNewCallback);
        snippets.add(getCustomObjectPermissionByIdNewCallback);
        snippets.add(uploadFileNewCallback);
        snippets.add(updateFileNewCallback);
        snippets.add(deleteFileNewCallback);
        snippets.add(downloadFileSynchronous);

        // get file
        file1 = Utils.getFileFromRawResource(R.raw.sample_file, context);
        file2 = Utils.getFileFromRawResource(R.raw.sample_file2, context);
    }


    Snippet getCustomObjectsNewCallback = new Snippet("get objects with new callback") {
        @Override
        public void execute() {
            QBCustomObjects.getObjects(CLASS_NAME, (List<Object>) null, new QBEntityCallbackImpl<ArrayList<QBCustomObject>>() {

                @Override
                public void onSuccess(ArrayList<QBCustomObject> customObjects, Bundle params) {
                    int skip = params.getInt(Consts.SKIP);
                    int limit = params.getInt(Consts.LIMIT);
                    Log.i(TAG, "limit=" + limit + " skip=" + skip);
                    Log.i(TAG, ">>> custom object list: " + customObjects.toString());
                }

                @Override
                public void onError(List<String> eroors) {
                    handleErrors(eroors);
                }
            });
        }
    };

    Snippet getCustomObjectsSynchronous = new AsyncSnippet("get objects synchronous", context) {

        @Override
        public void executeAsync() {
            Bundle params = new Bundle();
            ArrayList<QBCustomObject> objects = null;
            try {
                objects = QBCustomObjects.getObjects(CLASS_NAME, (List<Object>) null, params);
            } catch (QBResponseException e) {
                Log.i(TAG, "errors=" + e.getLocalizedMessage());
                setException(e);
            }
            if (objects != null) {
                int skip = params.getInt(Consts.SKIP);
                int limit = params.getInt(Consts.LIMIT);
                Log.i(TAG, "limit=" + limit + " skip=" + skip);
                Log.i(TAG, ">>> custom object list: " + objects.toString());
            }
        }

    };

    Snippet createCustomObjectNewCallback = new Snippet("create object new callback") {
        @Override
        public void execute() {
            // Create new record
            //
            QBCustomObject newRecord = new QBCustomObject(CLASS_NAME);
            newRecord.put(RATING_FIELD, 99);
            newRecord.put(DESCRIPTION_FIELD, "Hello world");
           /* newRecord.setParentId("50d9bf2d535c12344701c43a");
            //
            // set permissions:
            // READ
            QBPermissions permissions = new QBPermissions();
            permissions.setReadPermission(QBPermissionsLevel.OPEN);
            //
            // DELETE
            ArrayList<String> openPermissionsForUserIDS = new  ArrayList<String>();
            openPermissionsForUserIDS.add("33");
            openPermissionsForUserIDS.add("92");
            permissions.setDeletePermission(QBPermissionsLevel.OPEN_FOR_USER_IDS, openPermissionsForUserIDS);
            //
            // UPDATE
            permissions.setUpdatePermission(QBPermissionsLevel.OWNER);
            newRecord.setPermission(permissions);*/

            QBCustomObjects.createObject(newRecord, new QBEntityCallbackImpl<QBCustomObject>() {
                @Override
                public void onSuccess(QBCustomObject object, Bundle params) {
                    Log.i(TAG, ">>> created record: " + object);
                }

                @Override
                public void onError(List<String> eroors) {
                    handleErrors(eroors);
                }
            });
        }
    };

    Snippet createCustomObjectsNewCallback = new Snippet("create objects") {

        public static final int NUM_RECORDS = 4;

        private QBCustomObject createObject() {
            Random random = new Random();
            QBCustomObject customObject = new QBCustomObject(CLASS_NAME);
            customObject.put(RATING_FIELD, random.nextInt(100));
            customObject.put(DESCRIPTION_FIELD, "Hello world");
            return customObject;
        }


        @Override
        public void execute() {
            List<QBCustomObject> qbCustomObjectList = new ArrayList<QBCustomObject>(NUM_RECORDS);
            for (int i = 0; i < NUM_RECORDS; i++) {
                QBCustomObject qbCustomObject = createObject();
                qbCustomObjectList.add(qbCustomObject);
            }

            QBCustomObjects.createObjects(qbCustomObjectList, new QBEntityCallbackImpl<ArrayList<QBCustomObject>>() {
                @Override
                public void onSuccess(ArrayList<QBCustomObject> qbCustomObjects, Bundle args) {
                    super.onSuccess(qbCustomObjects, args);
                    Log.i(TAG, ">>> custom object list: " + qbCustomObjects);
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }
            });
        }
    };

    Snippet getGetCustomObjectsByIdsNewCallback = new Snippet("get custom objects by ids ne callback") {
        @Override
        public void execute() {

            StringifyArrayList<String> coIDs = new StringifyArrayList<String>();
            coIDs.add("50e67e6e535c121c66004c74");
            coIDs.add("50e67e6d535c127f66004f47");
            coIDs.add("50e67e6b535c121c66004c72");
            coIDs.add("50e59f81535c121c660015fd");

            QBCustomObjects.getObjects(CLASS_NAME, new QBEntityCallbackImpl<ArrayList<QBCustomObject>>() {
                @Override
                public void onSuccess(ArrayList<QBCustomObject> objects, Bundle params) {
                    Log.i(TAG, ">>> custom objects: " + objects.toString());
                }

                @Override
                public void onError(List<String> errors) {
                    handleErrors(errors);
                }

            });
        }
    };

    Snippet getCountCustomsObjectNewCallback = new Snippet("get count custom objects new") {
        @Override
        public void execute() {
            String fieldName = "title";
            String fieldForSort = "integer_field";
            QBCustomObjectRequestBuilder requestBuilder = new QBCustomObjectRequestBuilder();
//            requestBuilder.sortAsc(fieldName);
//            requestBuilder.sortDesc(fieldName);

            // search records which contains exactly specified value
//            String fieldValue = "1";
//            requestBuilder.eq(fieldName, fieldValue);

            // Limit search results to N records. Useful for pagination. Maximum value - 100 (by default). If limit is equal to -1 only last record will be returned
//            requestBuilder.setPagesLimit(2);

            //Skip N records in search results. Useful for pagination. Default (if not specified): 0
            requestBuilder.setPagesLimit(15);

            // Search record with field which contains value according to specified value and operator
//            requestBuilder.lt("integer_field", 60);
//            requestBuilder.lte(fieldForSort, 1);
//            requestBuilder.gt(fieldForSort, 60);
//            requestBuilder.gte(fieldForSort, 99);
//            requestBuilder.ne(fieldForSort, 99);
//            requestBuilder.ctn(fieldName, "son");

            // for arrays
//            ArrayList<String> healthList = new ArrayList<String>();
//            healthList.add("man");
//            healthList.add("girl");
//            requestBuilder.in("tags", "man", "girl");
//            requestBuilder.or(fieldName, "sam", "igor");
//            requestBuilder.nin("tags", healthList);
//            requestBuilder.count();


            List<Object> objectList = new ArrayList<Object>();
            objectList.add(fieldName);
            QBCustomObjects.countObjects(CLASS_NAME, requestBuilder, new QBEntityCallbackImpl<Integer>() {

                @Override
                public void onSuccess(Integer counnt, Bundle params) {
                    Log.i(TAG, "count=" + counnt);
                }

                @Override
                public void onError(List<String> eroors) {
                    handleErrors(eroors);
                }
            }

            );
        }
    };

    Snippet getCustomObjectPermissionByIdNewCallback = new Snippet("get object permissions new callback") {
        @Override
        public void execute() {
            String OBJ_ID = "52b88399535c12c51c001140";

            QBCustomObjects.getObjectPermissions(CLASS_NAME, OBJ_ID, new QBEntityCallbackImpl<QBPermissions>() {

                @Override
                public void onSuccess(QBPermissions permissions, Bundle params) {
                    Log.i(TAG, ">>> custom object's permissions: " + permissions.toString());
                }

                @Override
                public void onError(List<String> eroors) {
                    handleErrors(eroors);
                }
            });
        }
    };

    Snippet deleteCustomObjectNewCallback = new Snippet("delete object on new callback") {
        @Override
        public void execute() {

            QBCustomObjects.deleteObject(CLASS_NAME, "af3514342afbbb3555", new QBEntityCallbackImpl() {


                @Override
                public void onSuccess() {
                    Log.i(TAG, ">>> custom object deleted OK");
                }

                @Override
                public void onError(List eroors) {
                    handleErrors(eroors);
                }
            });
        }
    };

    Snippet deleteCustomObjectsSynchronous = new AsyncSnippet("delete objects synchronous", context) {

        @Override
        public void executeAsync() {
            StringifyArrayList<String> deleteIds = new StringifyArrayList<String>();
            deleteIds.add("529f7ef5bf7b772b775a7771");
            deleteIds.add("529f7ef5bf7b772b775a7772");
            deleteIds.add("51ed3685535c12ecb0018a47");
            Bundle params = new Bundle();
            ArrayList<String> deleted = null;
            try {
                deleted = QBCustomObjects.deleteObjects(CLASS_NAME, deleteIds, params);
            } catch (QBResponseException e) {
                setException(e);
            }
            if(deleted != null){
                Log.i(TAG, ">>> deletedObjs: " + deleted.toString());
                ArrayList<String> notFound = params.getStringArrayList(Consts.NOT_FOUND_IDS);
                ArrayList<String> wrongPermissions = params.getStringArrayList(Consts.WRONG_PERMISSIONS_IDS);
                Log.i(TAG, ">>> notFoundObjs: " + notFound.toString());
                Log.i(TAG, ">>> wrongPermissionsObjs: " + wrongPermissions.toString());
            }
        }
    };

    Snippet deleteCustomObjectsNewCallback = new Snippet("delete objects new callback") {
        @Override
        public void execute() {

            StringifyArrayList<String> deleteIds = new StringifyArrayList<String>();
            deleteIds.add("529f7ef5bf7b772b775a776f");
            deleteIds.add("529f7ef5bf7b772b775a7770");
            deleteIds.add("51ed3685535c12ecb0018a47");
            deleteIds.add("52b95057535c1261bf0065e5");
            QBCustomObjects.deleteObjects(CLASS_NAME, deleteIds, new QBEntityCallbackImpl<ArrayList<String>>() {

                @Override
                public void onSuccess(ArrayList<String> deletedObjects, Bundle params) {
                    Log.i(TAG, ">>> deletedObjs: " + deletedObjects.toString());
                    ArrayList<String> notFound = params.getStringArrayList(Consts.NOT_FOUND_IDS);
                    ArrayList<String> wrongPermissions = params.getStringArrayList(Consts.WRONG_PERMISSIONS_IDS);
                    Log.i(TAG, ">>> notFoundObjs: " + notFound.toString());
                    Log.i(TAG, ">>> wrongPermissionsObjs: " + wrongPermissions.toString());
                }

                @Override
                public void onError(List<String> eroors) {
                    handleErrors(eroors);
                }
            });
        }
    };

    Snippet updateCustomObjectNewCallback = new Snippet("update object new callback") {
        @Override
        public void execute() {
            QBCustomObject record = new QBCustomObject();
            //
            // set Class name and record ID:
            record.setClassName(CLASS_NAME);
            record.setCustomObjectId("52b30274535c12fbf80121bd");
            //
            // set fields:
            HashMap<String, Object> fields = new HashMap<String, Object>();
            fields.put(DESCRIPTION_FIELD, "Hello world");
            fields.put(RATING_FIELD, 10);
            record.setFields(fields);
            //
           /* // update permissions:
            // READ
            QBPermissions permissions = new QBPermissions();
            permissions.setReadPermission(QBPermissionsLevel.OPEN);
            //
            // DELETE
            ArrayList<String> openPermissionsForUserIDS = new  ArrayList<String>();
            openPermissionsForUserIDS.add("33");
            openPermissionsForUserIDS.add("92");
            permissions.setDeletePermission(QBPermissionsLevel.OPEN_FOR_USER_IDS, openPermissionsForUserIDS);
            //
            // UPDATE
            permissions.setUpdatePermission(QBPermissionsLevel.OWNER);
            record.setPermission(permissions);*/

            QBCustomObjects.updateObject(record, (QBCustomObjectUpdateBuilder) null, new QBEntityCallbackImpl<QBCustomObject>() {
                @Override
                public void onSuccess(QBCustomObject object, Bundle params) {
                    Log.i(TAG, ">>> updated record: : " + object.toString());
                }

                @Override
                public void onError(List<String> eroors) {
                    handleErrors(eroors);
                }
            });
        }
    };

    Snippet uploadFileNewCallback = new Snippet("upload CO file new") {
        @Override
        public void execute() {
            QBCustomObject qbCustomObject = new QBCustomObject(CLASS_NAME, NOTE1_ID);
            QBCustomObjectsFiles.uploadFile(file1, qbCustomObject, AVATAR_FIELD, new QBEntityCallbackImpl<QBCustomObjectFileField>() {

                @Override
                public void onSuccess(QBCustomObjectFileField uploadFileResult, Bundle params) {
                    Log.i(TAG, ">>>upload response:" + uploadFileResult.getFileName() + " " + uploadFileResult.getFileId() + " " +
                            uploadFileResult.getContentType());
                }

                @Override
                public void onError(List<String> eroors) {
                       handleErrors(eroors);
                }
            });
        }
    };

    Snippet updateFileNewCallback = new Snippet("update CO file new") {
        @Override
        public void execute() {
            QBCustomObject qbCustomObject = new QBCustomObject(CLASS_NAME, NOTE1_ID);
            QBCustomObjectsFiles.uploadFile(file2, qbCustomObject, AVATAR_FIELD, new QBEntityCallbackImpl<QBCustomObjectFileField>() {

                @Override
                public void onSuccess(QBCustomObjectFileField updatedResult, Bundle params) {
                    Log.i(TAG, ">>>updated successful");
                }

                @Override
                public void onError(List eroors) {
                    handleErrors(eroors);
                }
            });
        }
    };

    Snippet deleteFileNewCallback = new Snippet("delete CO file") {
        @Override
        public void execute() {
            QBCustomObject qbCustomObject = new QBCustomObject(CLASS_NAME, NOTE1_ID);
            QBCustomObjectsFiles.deleteFile(qbCustomObject, AVATAR_FIELD, new QBEntityCallbackImpl() {

                @Override
                public void onSuccess() {
                    Log.i(TAG, "deleted succesfull");
                }

                @Override
                public void onError(List eroors) {
                     handleErrors(eroors);
                }
            });
        }
    };


    AsyncSnippet downloadFileSynchronous = new AsyncSnippet("download CO file synchronous", context) {
        @Override
        public void executeAsync() {
            QBCustomObject qbCustomObject = new QBCustomObject(CLASS_NAME, NOTE1_ID);
            Bundle bundle = new Bundle();
            InputStream inputStream = null;
            try {
                inputStream = QBCustomObjectsFiles.downloadFile(qbCustomObject, AVATAR_FIELD, bundle);
            } catch (QBResponseException e) {
                setException(e);
            }
            if(inputStream != null){
                byte[] content = bundle.getByteArray(Consts.CONTENT_TAG);
                String contentFromFile = Utils.getContentFromFile(inputStream);
                Log.i(TAG, "file downloaded: "+contentFromFile);
            }
        }
    };

}
