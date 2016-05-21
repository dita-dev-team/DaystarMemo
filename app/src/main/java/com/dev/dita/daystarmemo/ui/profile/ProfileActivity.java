package com.dev.dita.daystarmemo.ui.profile;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.dita.daystarmemo.PrefSettings;
import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.controller.bus.UserBus;
import com.dev.dita.daystarmemo.controller.utils.ImageUtils;
import com.dev.dita.daystarmemo.controller.utils.UIUtils;
import com.dev.dita.daystarmemo.ui.customviews.CoordinatedCircularImageView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import butterknife.OnTextChanged;

/**
 * The type Profile activity.
 */
public class ProfileActivity extends AppCompatActivity {

    private final static String TAG = ProfileActivity.class.getName();

    private static final int IMAGE_PICK = 1;
    private static final int IMAGE_CAPTURE = 2;
    /**
     * The Coordinator layout.
     */
    @BindView(R.id.profile_coordinator)
    CoordinatorLayout coordinatorLayout;
    /**
     * The Profile image.
     */
    @BindView(R.id.profile_image)
    CoordinatedCircularImageView profileImage;
    /**
     * The Bottom sheet button.
     */
    @BindView(R.id.profile_edit)
    FloatingActionButton bottomSheetButton;
    /**
     * The Save button.
     */
    @BindView(R.id.profile_save_button)
    Button saveButton;
    /**
     * The Name text view.
     */
    @BindView(R.id.profile_name)
    TextView nameTextView;
    /**
     * The Email text view.
     */
    @BindView(R.id.profile_email)
    TextView emailTextView;
    /**
     * The Name edit text.
     */
    @BindView(R.id.profile_edit_name)
    TextInputEditText nameEditText;
    /**
     * The Email edit text.
     */
    @BindView(R.id.profile_edit_email)
    TextInputEditText emailEditText;
    private Uri imageUri;

    /**
     * Init.
     */
    public void init() {
        initBottomSheet();
        loadProfile();
        nameEditText.setText(nameTextView.getText());
        emailEditText.setText(emailTextView.getText());
        saveButton.setEnabled(false);
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
        init();
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

    /**
     * Capture image.
     */
    @OnClick(R.id.profile_image)
    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            String filename = "profile_image";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, filename);
            imageUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
            );
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        }
        startActivityForResult(intent, IMAGE_CAPTURE);
    }

    /**
     * Choose profile image boolean.
     *
     * @return the boolean
     */
    @OnLongClick(R.id.profile_image)
    public boolean chooseProfileImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Photo"), IMAGE_PICK);
        return true;
    }

    /**
     * Name changed.
     *
     * @param text the text
     */
    @OnTextChanged(R.id.profile_edit_name)
    public void nameChanged(CharSequence text) {
        if (!text.equals(nameTextView.getText())) {
            saveButton.setEnabled(true);
        }
    }

    /**
     * Email changed.
     *
     * @param text the text
     */
    @OnTextChanged(R.id.profile_edit_email)
    public void emailChanged(CharSequence text) {
        if (!text.equals(emailTextView.getText())) {
            saveButton.setEnabled(true);
        }
    }

    /**
     * Image from camera.
     *
     * @param resultCode the result code
     * @param data       the data
     */
    public void imageFromCamera(int resultCode, Intent data) {

        profileImage.setImageBitmap((Bitmap) data.getExtras().get("data"));
        saveButton.setEnabled(true);
    }

    /**
     * Image from gallery.
     *
     * @param resultCode the result code
     * @param data       the data
     */
    public void imageFromGallery(int resultCode, Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        profileImage.setImageBitmap(BitmapFactory.decodeFile(filePath));
        saveButton.setEnabled(true);
    }

    /**
     * Save image.
     */
    public void saveImage() {
        profileImage.buildDrawingCache();
        Bitmap bitmap = profileImage.getDrawingCache();
        String image = ImageUtils.decodeBitmaptoString(bitmap);
        PrefSettings.setValue(this, "image", image);
    }

    /**
     * Init bottom sheet.
     */
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
                if (state == BottomSheetBehavior.STATE_COLLAPSED || state == BottomSheetBehavior.STATE_DRAGGING) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else if (state == BottomSheetBehavior.STATE_EXPANDED || state == BottomSheetBehavior.STATE_HIDDEN) {
                    UIUtils.hideKeyboard(ProfileActivity.this);
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
            }
        });
    }

    /**
     * Load profile.
     */
    public void loadProfile() {
        // load profile from settings
        nameTextView.setText(PrefSettings.getValue(this, "name"));
        emailTextView.setText(PrefSettings.getValue(this, "email"));
        String image = PrefSettings.getValue(this, "image");
        if (!TextUtils.isEmpty(image)) {
            Bitmap bitmap = ImageUtils.decodeBitmapfromString(image);
            if (bitmap != null) {
                profileImage.setImageBitmap(bitmap);
            }
        }
    }

    /**
     * Save profile.
     */
    @OnClick(R.id.profile_save_button)
    public void saveProfile() {
        // Save profile to settings
        PrefSettings.setValue(this, "name", nameEditText.getText().toString());
        PrefSettings.setValue(this, "email", emailEditText.getText().toString());
        saveImage();

        nameTextView.setText(nameEditText.getText().toString());
        emailTextView.setText(emailEditText.getText().toString());
        EventBus.getDefault().post(new UserBus.ProfileUpdatedEvent());
        Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
    }
}
