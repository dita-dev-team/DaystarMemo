package com.dev.dita.daystarmemo.ui.memos;

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
import com.dev.dita.daystarmemo.model.database.Memo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MemosActivity extends AppCompatActivity {
    final String TAG = getClass().getName();
    @BindView(R.id.memos_empty)
    TextView empty;
    @BindView(R.id.memos_list_view)
    ListView listView;
    @BindView(R.id.memos_new_memo)
    FloatingActionButton newMemoButton;

    private Realm realm;
    private RealmResults<Memo> memos;
    private MemosListAdapter adapter;

    public void init() {
        realm = Realm.getDefaultInstance();

        adapter = new MemosListAdapter(this, null);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    newMemoButton.setVisibility(View.VISIBLE);
                } else {
                    newMemoButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        memos = realm.where(Memo.class).equalTo("latest", true).findAllSortedAsync("date");
        memos.addChangeListener(new RealmChangeListener<RealmResults<Memo>>() {
            @Override
            public void onChange(RealmResults<Memo> element) {
                adapter.updateData(element);
                empty.setVisibility((adapter.isEmpty()) ? View.VISIBLE : View.GONE);
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memos);
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

    @OnItemClick(R.id.memos_list_view)
    public void onItemClick(int position) {
        Memo memo = adapter.getItem(position);
        String username = memo.isMe ? memo.recipient.username : memo.sender.username;
        Intent intent = new Intent(MemosActivity.this, MemosChatActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}
