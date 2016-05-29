package com.dev.dita.daystarmemo.ui.memos;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.model.database.Memo;
import com.dev.dita.daystarmemo.model.database.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class MemosChatActivity extends AppCompatActivity {

    @BindView(R.id.memos_chat_list_view)
    ListView listView;

    private Realm realm;
    private RealmResults<Memo> memos;
    private MemosChatListAdapter adapter;

    public void init() {
        realm = Realm.getDefaultInstance();

        adapter = new MemosChatListAdapter(this, null);
        listView.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();
        String username = null;
        if (extras != null) {
            username = extras.getString("username");
        }
        memos = realm.where(User.class).equalTo("username", username).findFirst().memos.where().findAllSortedAsync("date", Sort.DESCENDING);
        memos.addChangeListener(new RealmChangeListener<RealmResults<Memo>>() {
            @Override
            public void onChange(RealmResults<Memo> element) {
                adapter.updateData(element);

            }
        });
        setTitle(username);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memos_chat);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        memos.removeChangeListeners();
        realm.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
    }

}
