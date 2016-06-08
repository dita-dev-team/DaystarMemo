package com.dev.dita.daystarmemo.ui.profile;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.BaasUser;
import com.dev.dita.daystarmemo.PrefSettings;
import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.controller.bus.UserBus;
import com.dev.dita.daystarmemo.controller.utils.ImageUtils;
import com.dev.dita.daystarmemo.controller.utils.UIUtils;
import com.dev.dita.daystarmemo.model.baas.UserBaas;
import com.dev.dita.daystarmemo.ui.customviews.CoordinatedCircularImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * The type Profile activity.
 */
public class ProfileActivity extends AppCompatActivity {

    private final static String TAG = ProfileActivity.class.getName();

    private static final int IMAGE_PICK = 1;
    private static final int IMAGE_CAPTURE = 2;

    /**
     * The Swipe refresh layout.
     */
    @BindView(R.id.profile_refresh_animation)
    SwipeRefreshLayout swipeRefreshLayout;
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
        swipeRefreshLayout.setColorSchemeResources(R.color.baseColor1, R.color.baseColor2);
        UIUtils.setAnimation(swipeRefreshLayout, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        EventBus.getDefault().register(this);
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

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * Change image.
     */
    @OnClick(R.id.profile_image)
    public void changeImage() {
        final CharSequence[] items = {
                "Take Photo",
                "Choose from Gallery",
                "Remove Photo"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("Change Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        captureImage();
                        break;
                    case 1:
                        chooseImage();
                        break;
                    case 2:
                        String image = PrefSettings.getValue(ProfileActivity.this, "image");
                        if (!TextUtils.isEmpty(image)) {
                            new AlertDialog.Builder(ProfileActivity.this)
                                    .setMessage("Are you sure?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            profileImage.setImageDrawable(getResources().getDrawable(R.drawable.default_profile));
                                            saveButton.setEnabled(true);

                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .show();

                        }
                        break;
                }
            }
        });
        builder.show();
    }

    /**
     * Capture image.
     */
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
     * Choose image boolean.
     *
     * @return the boolean
     */
    public boolean chooseImage() {
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
     * Change password.
     */
    @OnClick(R.id.change_password_button)
    public void changePassword() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);
        final EditText oldEditText = new EditText(this);
        oldEditText.setHint("Old Password");
        oldEditText.setTextSize(16);
        oldEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        final EditText newEditText = new EditText(this);
        newEditText.setHint("New Password");
        newEditText.setTextSize(16);
        newEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(oldEditText);
        layout.addView(newEditText);
        new AlertDialog.Builder(this)
                .setTitle("Change password")
                .setView(layout)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String oldPassword = oldEditText.getText().toString();
                        String newPassword = newEditText.getText().toString();
                        if (!oldPassword.equals(BaasUser.current().getPassword())) {
                            Toast.makeText(getApplicationContext(), "Old password doesn't match", Toast.LENGTH_SHORT).show();
                            changePassword();
                        } else {
                            UserBaas.changePassword(newPassword);
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();

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
            Bitmap bitmap = ImageUtils.decodeBitmapFromString(image);
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
        Map<String, String> details = new HashMap<>();
        details.put("name", nameEditText.getText().toString());
        details.put("email", emailEditText.getText().toString());
        profileImage.buildDrawingCache();
        Bitmap bitmap = profileImage.getDrawingCache();
        details.put("image", ImageUtils.decodeBitmapToString(bitmap));
        // Save profile to remote server
        UserBaas.updateUserProfile(details);
    }

    /**
     * On event.
     *
     * @param remoteResult the remote result
     */
    @Subscribe
    public void onEvent(UserBus.ProfileUpdatedRemoteResult remoteResult) {
        if (remoteResult.error) {
            Toast.makeText(this, "Profile update failed", Toast.LENGTH_LONG).show();
        } else {
            // Save profile to settings
            nameTextView.setText(nameEditText.getText().toString());
            emailTextView.setText(emailEditText.getText().toString());
            profileImage.buildDrawingCache();
            Bitmap bitmap = profileImage.getDrawingCache();
            String image = ImageUtils.decodeBitmapToString(bitmap);
            PrefSettings.setValue(this, "image", image);
            PrefSettings.setValue(this, "name", nameEditText.getText().toString());
            PrefSettings.setValue(this, "email", emailEditText.getText().toString());
            EventBus.getDefault().post(new UserBus.ProfileUpdatedEvent());
            Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * On event.
     *
     * @param changeResult the change result
     */
    @Subscribe
    public void onEvent(UserBus.PasswordChangeResult changeResult) {
        if (changeResult.error) {
            Toast.makeText(this, "Unable to change password", Toast.LENGTH_LONG).show();
        } else {
            PrefSettings.setValue(this, "password", BaasUser.current().getPassword());
            // login the user after password is changed to refresh token
            UserBaas.loginUser(PrefSettings.getValue(this, "username"), PrefSettings.getValue(this, "password"));
            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_LONG).show();
        }
    }
}
