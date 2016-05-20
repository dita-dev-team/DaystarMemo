package com.dev.dita.daystarmemo.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.ui.customviews.CoordinatedCircularImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class ProfileActivity extends AppCompatActivity {

    private static final int IMAGE_PICK = 1;
    private static final int IMAGE_CAPTURE = 2;

    @BindView(R.id.profile_coordinator)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.profile_image)
    CoordinatedCircularImageView profileImage;
    @BindView(R.id.profile_edit)
    FloatingActionButton bottomSheetButton;

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
        initBottomSheet();
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

    public void initBottomSheet() {
        View bottom = coordinatorLayout.findViewById(R.id.profile_bottom_sheet);
        final BottomSheetBehavior behavior = BottomSheetBehavior.from(bottom);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        bottomSheetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int state = behavior.getState();
                switch (state) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        break;

                }
            }
        });
    }
}
