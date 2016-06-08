package com.dev.dita.daystarmemo.ui.connections;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.model.database.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class ConnectionsActivity extends AppCompatActivity {
    final String TAG = getClass().getName();
    @BindView(R.id.connections_empty)
    TextView empty;
    @BindView(R.id.connections_list_view)
    ListView listView;
    @BindView(R.id.connections_new_connection)
    FloatingActionButton newConnectionButton;

    private Realm realm;
    private RealmResults<User> users;
    private ConnectionsListAdapter adapter;

    public void init() {
        realm = Realm.getDefaultInstance();
        users = realm.where(User.class).findAllSorted("username");
        adapter = new ConnectionsListAdapter(this, users);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    newConnectionButton.setVisibility(View.VISIBLE);
                } else {
                    newConnectionButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        empty.setVisibility(adapter.isEmpty() ? View.VISIBLE : View.GONE);
        setTitle("Your Connections");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connections);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.connections_toolbar);
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
        users.removeChangeListeners();
        realm.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
    }

    @OnClick(R.id.connections_new_connection)
    public void onNewButtonClicked() {
        startActivity(new Intent(this, NewConnectionActivity.class));
    }
}
