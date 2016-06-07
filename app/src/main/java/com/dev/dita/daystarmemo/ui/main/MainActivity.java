package com.dev.dita.daystarmemo.ui.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.BaasBox;
import com.baasbox.android.BaasHandler;
import com.baasbox.android.BaasResult;
import com.baasbox.android.BaasUser;
import com.baasbox.android.json.JsonObject;
import com.dev.dita.daystarmemo.PrefSettings;
import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.controller.bus.UserBus;
import com.dev.dita.daystarmemo.controller.utils.ImageUtils;
import com.dev.dita.daystarmemo.controller.utils.UIUtils;
import com.dev.dita.daystarmemo.model.baas.User;
import com.dev.dita.daystarmemo.ui.memos.MemosActivity;
import com.dev.dita.daystarmemo.ui.profile.ProfileActivity;
import com.dev.dita.daystarmemo.ui.welcome.WelcomeActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getName();

    /**
     * The User name.
     */
    TextView userName;
    /**
     * The User email.
     */
    TextView userEmail;
    /**
     * The User image.
     */
    CircleImageView userImage;

    /**
     * The Swipe refresh layout.
     */
    @BindView(R.id.main_refresh_animation)
    SwipeRefreshLayout swipeRefreshLayout;

    /**
     * Init.
     */
    public void init() {
        swipeRefreshLayout.setColorSchemeResources(R.color.baseColor1, R.color.baseColor2);
        UIUtils.setAnimation(swipeRefreshLayout, false);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);

        userImage = (CircleImageView) headerView.findViewById(R.id.nav_profile_image);
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

        userName = (TextView) headerView.findViewById(R.id.nav_name);
        userEmail = (TextView) headerView.findViewById(R.id.nav_email);
        initUser();
        Log.d(TAG, String.valueOf(BaasBox.messagingService().isEnabled()));
        if (!BaasBox.messagingService().isEnabled()) {
            BaasBox.messagingService().enable(new BaasHandler<Void>() {
                @Override
                public void handle(BaasResult<Void> baasResult) {
                    if (baasResult.isSuccess()) {
                        Log.d(TAG, "Successful");
                    } else {
                        Log.e(TAG, "Failed");
                    }
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!PrefSettings.isLoggedIn(this)) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        }
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_memo) {
            startActivity(new Intent(MainActivity.this, MemosActivity.class));
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_logout) {
            // Show confirmation dialog to confirm logging out
            new AlertDialog.Builder(this)
                    .setTitle("Logging out")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UIUtils.setAnimation(swipeRefreshLayout, true);
                            User.logoutUser();

                        }
                    })
                    .setNegativeButton("No", null)
                    .show();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * On event.
     *
     * @param logoutResult the logout result
     */
    @Subscribe
    public void onEvent(UserBus.LogoutResult logoutResult) {
        UIUtils.setAnimation(swipeRefreshLayout, false);
        if (logoutResult.error) {
            Toast.makeText(this, "Unable to logout", Toast.LENGTH_SHORT).show();
        } else {
            //PrefSettings.setLoggedIn(this, false);
            PrefSettings.clear(this);
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        }
    }

    /**
     * On event.
     *
     * @param profileUpdatedEvent the profile updated event
     */
    @Subscribe
    public void onEvent(UserBus.ProfileUpdatedEvent profileUpdatedEvent) {
        initUser();
    }

    /**
     * Init user.
     */
    public void initUser() {
        // Set the default settings if none exist yet
        if (!PrefSettings.keyExists(this, "username")) {
            JsonObject details = BaasUser.current().getScope(BaasUser.Scope.FRIEND);
            PrefSettings.setValue(this, "username", BaasUser.current().getName());
            PrefSettings.setValue(this, "name", details.getString("name", getString(R.string.default_name)));
            PrefSettings.setValue(this, "email", details.getString("email", getString(R.string.default_email)));
            PrefSettings.setValue(this, "password", BaasUser.current().getPassword());
            PrefSettings.setValue(this, "token", BaasUser.current().getToken());
            PrefSettings.setValue(this, "image", details.getString("image", ""));
        }

        // Load the profile from setting
        userName.setText(PrefSettings.getValue(this, "name"));
        userEmail.setText(PrefSettings.getValue(this, "email"));
        String imageSrc = PrefSettings.getValue(this, "image");
        if (!TextUtils.isEmpty(imageSrc)) {
            Bitmap image = ImageUtils.decodeBitmapFromString(imageSrc);
            if (image != null) {
                userImage.setImageBitmap(image);
            }
        }
    }

}
