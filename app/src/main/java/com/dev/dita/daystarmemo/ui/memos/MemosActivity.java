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
import butterknife.OnClick;
import butterknife.OnItemClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

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
    private RealmChangeListener listener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            adapter.notifyDataSetChanged();
        }
    };

    public void init() {
        realm = Realm.getDefaultInstance();
        memos = realm.where(Memo.class).equalTo("latest", true).findAllSorted("date", Sort.DESCENDING);
        memos.addChangeListener(listener);
        adapter = new MemosListAdapter(this, memos);
        //adapter = new TestAdapter(this, R.layout.memos_list_item, R.id.memo_list_username, memos.subList(0, memos.size()-1));
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

        empty.setVisibility(adapter.isEmpty() ? View.VISIBLE : View.GONE);
        setTitle("Memos");
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

    @OnClick(R.id.memos_new_memo)
    public void onNewButtonClicked() {
        startActivity(new Intent(this, NewMemoActivity.class));
    }
}
