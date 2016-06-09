package com.dev.dita.daystarmemo.ui.connections;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baasbox.android.BaasUser;
import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.controller.bus.UserBus;
import com.dev.dita.daystarmemo.model.baas.UserBaas;
import com.dev.dita.daystarmemo.model.database.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class NewConnectionActivity extends AppCompatActivity {

    final String TAG = getClass().getName();
    @BindView(R.id.new_connections_list_view)
    ListView listView;
    @BindView(R.id.new_connections_empty)
    TextView empty;
    ProgressDialog progressDialog;

    NewConnectionsListAdapter adapter;
    Realm realm;
    RealmResults<User> users;
    BaasUser newUser;

    public void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching Connections ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);

        listView.setEmptyView(empty);
        realm = Realm.getDefaultInstance();
        users = realm.where(User.class).findAll();
        users.addChangeListener(new RealmChangeListener<RealmResults<User>>() {
            @Override
            public void onChange(RealmResults<User> element) {
                adapter.setYourConnections(users);
                adapter.notifyDataSetChanged();
            }
        });
        setTitle("Connections");

        UserBaas.getUsers();
        progressDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_connection);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.new_connections_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        realm.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_connections, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.new_connections_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @OnItemClick(R.id.new_connections_list_view)
    public void onItemClick(int position) {
        final BaasUser user = adapter.getItem(position);
        User temp = users.where().equalTo("username", user.getName()).findFirst();
        if (temp == null) {
            new AlertDialog.Builder(this)
                    .setTitle(user.getName())
                    .setMessage("Add to your connections?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            User.addUser(user);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    @Subscribe
    public void onEvent(UserBus.GetUsersResult usersResult) {
        progressDialog.hide();
        if (!usersResult.error) {
            for (BaasUser user : usersResult.users) {
                if (user.getName().equals(BaasUser.current().getName())) {
                    usersResult.users.remove(user);
                    break;
                }
            }
            adapter = new NewConnectionsListAdapter(this, 0, usersResult.users);
            adapter.setYourConnections(users);
            listView.setAdapter(adapter);
        } else {
            Toast.makeText(this, "Unable to connect", Toast.LENGTH_SHORT).show();
        }

        //empty.setVisibility(adapter.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Subscribe
    public void onEvent(UserBus.AddUserResult userResult) {

        if (!userResult.error) {
            adapter.setYourConnections(users);
            Toast.makeText(this, "New connection added", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to add connection", Toast.LENGTH_SHORT).show();
        }
    }
}
