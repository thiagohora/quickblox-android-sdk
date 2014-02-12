package com.quickblox.sample.test.customobject;

import android.os.Bundle;
import android.util.Log;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.internal.core.helper.Lo;
import com.quickblox.internal.core.helper.StringifyArrayList;
import com.quickblox.module.custom.QBCustomObjects;
import com.quickblox.module.custom.model.QBCustomObject;
import com.quickblox.module.custom.model.QBPermissions;
import com.quickblox.module.custom.model.QBPermissionsLevel;
import org.junit.AfterClass;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: vfite
 * Date: 28.11.13
 * Time: 15:35
 * To change this template use File | Settings | File Templates.
 */
public class TestCreateCustomObject extends CustomObjectsTestCase {
    QBCustomObject note = getFakeObject();

    public static final String TAG = "createObjects";
    static public List<QBCustomObject> qbCustomObjectList;
    public static final String PARENT_ID = "51d816e0535c12d75f006537";
    Lo lo = new Lo(this);

    private static StringifyArrayList<String> coIds = new StringifyArrayList();

    @Override
    public void setUp() throws Exception {
        super.setUp();

        if (qbCustomObjectList == null || qbCustomObjectList.isEmpty()) {
            qbCustomObjectList = new LinkedList<QBCustomObject>();
            QBCustomObject fakeObject = getFakeObject();
            qbCustomObjectList.add(fakeObject);
            qbCustomObjectList.add(getFakeObject());
        }
    }


    public void testCreateNewObjects() {

        QBCustomObjects.createObjects(qbCustomObjectList, new QBEntityCallbackImpl<ArrayList<QBCustomObject>>() {

            @Override
            public void onSuccess(ArrayList<QBCustomObject> customObjects, Bundle args) {
                for (QBCustomObject qbCustomObject:customObjects) {
                    Log.d(TAG, "posted item=" + qbCustomObjectList.get(0).getFields().toString());
                    coIds.add(qbCustomObject.getCustomObjectId());
                    QBCustomObject objectFromCollection = getObjectFromCollection(qbCustomObjectList, FIELD_COMMENTS, (String) qbCustomObject.getFields().get(FIELD_COMMENTS));
                    assertEqualsObject(objectFromCollection, qbCustomObject);
                }
            }

            @Override
            public void onError(List<String> errors) {
                fail(errors.toString());
            }
        });

    }

    public void testCreateObject() {

        note.setParentId(PARENT_ID);
        QBCustomObjects.createObject(note, new QBEntityCallbackImpl<QBCustomObject>() {

            @Override
            public void onSuccess(QBCustomObject customObject, Bundle args) {
                QBCustomObject newHero = customObject;
                coIds.add(newHero.getCustomObjectId());
                assertEquals(note.getClassName(), newHero.getClassName());
                assertEquals(note.getFields().get(FIELD_COMMENTS), newHero.getFields().get(FIELD_COMMENTS));
                assertEquals(note.getFields().get(FIELD_TITLE), newHero.getFields().get(FIELD_TITLE));
                assertEquals(note.getFields().get(FIELD_STATUS), newHero.getFields().get(FIELD_STATUS));
                assertNotNull(newHero.getParentId());
            }

            @Override
            public void onError(List<String> errors) {
               fail(errors.toString());
            }
        });

    }

    public void testCreateObjectWithPermissions() {
        QBPermissions qbPermissions = new QBPermissions();
        qbPermissions.setReadPermission(QBPermissionsLevel.OPEN);
        ArrayList<String> userIds = new ArrayList<String>();
        userIds.add("13163");
        qbPermissions.setDeletePermission(QBPermissionsLevel.OPEN_FOR_USER_IDS, userIds);
        qbPermissions.setUpdatePermission(QBPermissionsLevel.OWNER);
        note.setPermission(qbPermissions);
        QBCustomObjects.createObject(note, new QBEntityCallbackImpl<QBCustomObject>(){

            @Override
            public void onSuccess(QBCustomObject newHero, Bundle args) {
                lo.g(newHero.toString());
                coIds.add(newHero.getCustomObjectId());
                QBPermissions qbPermissions =    newHero.getPermission();
                assertNotNull(qbPermissions);
                assertNotNull(qbPermissions.getReadLevel());
                assertNotNull(qbPermissions.getUpdateLevel());
                assertNotNull(qbPermissions.getDeleteLevel() );
                assertEquals(qbPermissions.getReadLevel().getAccess(), QBPermissionsLevel.OPEN);
                assertEquals(qbPermissions.getDeleteLevel().getAccess(), QBPermissionsLevel.OPEN_FOR_USER_IDS);
                assertEquals(qbPermissions.getUpdateLevel().getAccess(), QBPermissionsLevel.OWNER);
            }

            @Override
            public void onError(List<String> errors) {
                fail(errors.toString());
            }
        });

    }

    @AfterClass
    public static void testCleanUp(){
        if (coIds != null && !coIds.isEmpty()) {
            QBCustomObjects.deleteObjects(CLASS_NAME, coIds, (QBEntityCallback)null);
        }
    }

}

