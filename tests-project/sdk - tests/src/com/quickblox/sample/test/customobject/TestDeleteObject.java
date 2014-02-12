package com.quickblox.sample.test.customobject;

import android.os.Bundle;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.internal.core.helper.StringifyArrayList;
import com.quickblox.internal.module.custom.Consts;
import com.quickblox.module.custom.QBCustomObjects;
import com.quickblox.module.custom.model.QBCustomObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by vfite on 10.12.13.
 */
public class TestDeleteObject extends CustomObjectsTestCase {

    QBCustomObject note = getFakeObject();

    private StringifyArrayList<String> coIds = new StringifyArrayList();
    private LinkedList<QBCustomObject> qbCustomObjectList;

    @Override

    public void setUp() throws Exception {
        super.setUp();
    }

    public void createObjects(int size) {

        qbCustomObjectList = new LinkedList<QBCustomObject>();
        for(int i = 0; i < size; i++) {
            qbCustomObjectList.add(getFakeObject());
        }
        coIds.clear();
        QBCustomObjects.createObjects(qbCustomObjectList, new QBEntityCallbackImpl<ArrayList<QBCustomObject>>(){
            @Override
            public void onSuccess(ArrayList<QBCustomObject> customObjects, Bundle args) {
                for (QBCustomObject qbCustomObject : customObjects) {
                    coIds.add(qbCustomObject.getCustomObjectId());
                }
            }

            @Override
            public void onError(List<String> errors) {
                fail(errors.toString());
            }
        });
    }


    public void testDeleteObjects() {

        final int COUNT_OBJ =3;

        createObjects(COUNT_OBJ);
        QBCustomObjects.deleteObjects(CLASS_NAME, coIds, new QBEntityCallbackImpl<ArrayList<String>>() {

            @Override
            public void onSuccess(ArrayList<String> deletedObjects, Bundle args) {
                assertNotNull(deletedObjects);
                assertTrue(deletedObjects.size() == COUNT_OBJ);
                assertTrue(args.getStringArrayList(Consts.WRONG_PERMISSIONS_IDS).isEmpty());
                assertTrue(args.getStringArrayList(Consts.NOT_FOUND_IDS).isEmpty());
            }

            @Override
            public void onError(List<String> errors) {
                fail(errors.toString());
            }
        });

    }

    public void testDeleteObject() {

        QBCustomObjects.createObject(note, new QBEntityCallbackImpl<QBCustomObject>() {

            @Override
            public void onSuccess(QBCustomObject qbCustomObject, Bundle args) {
                assertNotNull(qbCustomObject);
            }

            @Override
            public void onError(List<String> errors) {
                fail(errors.toString());
            }
        });

        QBCustomObjects.deleteObject(note,  new QBEntityCallbackImpl<Void>() {

            @Override
            public void onSuccess() {
                super.onSuccess();
            }

            @Override
            public void onError(List<String> errors) {
                fail(errors.toString());
            }
        });

    }


    public void testDeleteObjectById() {

        createObjects(1);
        String id = coIds.get(0);
        QBCustomObjects.deleteObject(CLASS_NAME, id, new QBEntityCallbackImpl() {
            @Override
            public void onSuccess() {
                super.onSuccess();
            }

            @Override
            public void onError(List errors) {
                fail(errors.toString());
            }
        });
    }

   /* @AfterClass
    public static void testCleanUp() {
        if (coIds != null && !coIds.isEmpty()) {
            QBCustomObjects.deleteObjects(CLASS_NAME, coIds, null);
        }
    }*/
}
