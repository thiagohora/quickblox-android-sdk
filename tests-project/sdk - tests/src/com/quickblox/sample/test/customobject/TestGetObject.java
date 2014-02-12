package com.quickblox.sample.test.customobject;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.QBErrors;
import com.quickblox.internal.core.exception.QBResponseException;
import com.quickblox.internal.core.helper.Lo;
import com.quickblox.module.custom.QBCustomObjects;
import com.quickblox.module.custom.model.QBCustomObject;
import com.quickblox.module.custom.model.QBPermissions;
import org.apache.http.HttpStatus;
import org.junit.AfterClass;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vfite on 10.12.13.
 */
public class TestGetObject extends CustomObjectsTestCase {
    private static final String TAG = "DELETE NOTE";
    static QBCustomObject note;// = getFakeObject();

    Lo lo = new Lo(this);

    public static final int[] ERROR_STATUSES = new int[]{HttpStatus.SC_NOT_FOUND, HttpStatus.SC_FORBIDDEN, HttpStatus.SC_UNPROCESSABLE_ENTITY};

    @Override
    public void setUp() throws Exception {
        super.setUp();
        if(note == null || note.getCustomObjectId()==null){
            note = getFakeObject();
            QBCustomObjects.createObject(note, (QBEntityCallback)null);
        }
    }


    public void testGetObject() throws QBResponseException{
        //note = new QBCustomObject(CLASS_NAME, NOTE_ID);
            QBCustomObject newHero = QBCustomObjects.getObject(note.getClassName(), note.getCustomObjectId());
            assertEquals(note.getClassName(), newHero.getClassName());
            assertEquals(note.getFields().get(FIELD_TITLE), newHero.getFields().get(FIELD_TITLE));
            assertEquals(note.getFields().get(FIELD_COMMENTS), newHero.getFields().get(FIELD_COMMENTS));
            assertEquals(note.getFields().get(FIELD_STATUS), newHero.getFields().get(FIELD_STATUS));
    }


    public void testGetObjectWithPermissions() throws QBResponseException {
        //note = new QBCustomObject(CLASS_NAME, NOTE_ID);
        QBPermissions qbPermissions  = QBCustomObjects.getObjectPermissions(CLASS_NAME, note.getCustomObjectId());
        lo.g(qbPermissions.toString());
        assertNotNull(qbPermissions.getReadLevel());
        assertNotNull(qbPermissions.getUpdateLevel());
        assertNotNull(qbPermissions.getDeleteLevel() );
    }

    public void testGetObjectSpecifyOutputParams() throws QBResponseException {

        List<Object> outputParams = new ArrayList<Object>();
        outputParams.add(FIELD_TITLE);
        outputParams.add(FIELD_STATUS);
        QBCustomObject newHero = QBCustomObjects.getObject(note.getClassName(), note.getCustomObjectId(), outputParams);

        assertEquals(note.getClassName(), newHero.getClassName());
        assertEquals(note.getFields().get(FIELD_TITLE), newHero.getFields().get(FIELD_TITLE));
        assertEquals(note.getFields().get(FIELD_STATUS), newHero.getFields().get(FIELD_STATUS));
        assertNull( newHero.getFields().get(FIELD_COMMENTS));
        assertNull(newHero.getFields().get(FIELD_LICENSE));

    }


    public void testGetObjectByIdIncorrectClassNameAndId() {

        try {
            QBCustomObjects.getObject("nonexistentClass", "nonexistentId");
        } catch (QBResponseException e) {
            assertError(new String[]{QBErrors.UNDEFINED_CLASS, QBErrors.RESOURCE_NOT_FOUND}, e.getErrors().get(0));
        }
    }


    public void testGetObjectByIdIncorrectId() {

        try {
            QBCustomObjects.getObject(CLASS_NAME, "nonexistentId");
        } catch (QBResponseException e) {
            assertError(new String[]{QBErrors.UNDEFINED_CLASS, QBErrors.RESOURCE_NOT_FOUND}, e.getErrors().get(0));
        }

    }



    public void testGetObjectById() throws QBResponseException {

        String id = note.getCustomObjectId();
        QBCustomObject newHero = QBCustomObjects.getObject(CLASS_NAME, id);
        assertEquals(note.getClassName(), newHero.getClassName());
        assertEqualsObject(note, newHero);
    }

    @AfterClass
    public static void testCleanUp(){
                     if(note!=null && note.getCustomObjectId()!=null){
                         QBCustomObjects.deleteObject(note, (QBEntityCallback)null);
                     }
    }

}
