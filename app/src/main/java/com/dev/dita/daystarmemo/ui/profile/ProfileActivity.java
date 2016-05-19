package com.dev.dita.daystarmemo.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.ui.customviews.CoordinatedCircularImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class ProfileActivity extends AppCompatActivity {

    private static final int IMAGE_PICK = 1;
    private static final int IMAGE_CAPTURE = 2;

    @BindView(R.id.profile_image)
    CoordinatedCircularImageView profileImage;

    public void init() {

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case IMAGE_PICK:
                    imageFromGallery(resultCode, data);
                    break;
                case IMAGE_CAPTURE:
                    imageFromCamera(resultCode, data);
                    break;
                default:
                    break;
            }
        }
    }

    @OnClick(R.id.profile_image)
    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, IMAGE_CAPTURE);
    }

    @OnLongClick(R.id.profile_image)
    public boolean chooseProfileImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Photo"), IMAGE_PICK);
        return true;
    }

    public void imageFromCamera(int resultCode, Intent data) {
        profileImage.setImageBitmap((Bitmap) data.getExtras().get("data"));
    }

    public void imageFromGallery(int resultCode, Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        profileImage.setImageBitmap(BitmapFactory.decodeFile(filePath));
    }
}
