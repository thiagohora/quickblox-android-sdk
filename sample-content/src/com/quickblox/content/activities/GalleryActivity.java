package com.quickblox.content.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import com.quickblox.content.R;
import com.quickblox.content.adapter.GalleryAdapter;
import com.quickblox.content.helper.DataHolder;
import com.quickblox.core.QBEntityCallbackImpl;
import com.quickblox.module.content.QBContent;
import com.quickblox.module.content.model.QBFile;

import java.io.File;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: android
 * Date: 04.12.12
 * Time: 14:04
 */
public class GalleryActivity extends Activity implements AdapterView.OnItemClickListener {

    private final int SELECT_PICTURE = 0;
    private final String POSITION = "position";
    private final boolean PUBLIC_ACCESS_TRUE = true;
    private ProgressDialog progressDialog;
    private GridView gallery;
    private GalleryAdapter galleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getResources().getString(R.string.please_waite));
        progressDialog.setCancelable(false);

        gallery = (GridView) findViewById(R.id.gallery);
        galleryAdapter = new GalleryAdapter(this);
        gallery.setAdapter(galleryAdapter);
        gallery.setOnItemClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_new_img:
                displayPhoneGallery();
                break;
        }
    }

    private void displayPhoneGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                progressDialog.show();
                downImg(getPath(data.getData()));
            }
        }
    }

    private String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    private void downImg(String imgPath) {

        // ================= QuickBlox ===== Step 3 =================
        // Upload new file
        // QBContent.uploadFileTask consist of tree query : Create a file, Upload file, Declaring file uploaded
        final File img = new File(imgPath);
        QBContent.uploadFileTask(img, PUBLIC_ACCESS_TRUE, null,  new QBEntityCallbackImpl<QBFile>() {

            @Override
            public void onSuccess(QBFile qbFile, Bundle bundle) {
                progressDialog.hide();
                DataHolder.getDataHolder().addQbFile(qbFile);
                galleryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(List<String> strings) {
                progressDialog.hide();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        startShowImgActivity(position);
    }

    private void startShowImgActivity(int position) {
        Intent intent = new Intent(this, ShowImgActivity.class);
        intent.putExtra(POSITION, position);
        startActivity(intent);
    }
}
