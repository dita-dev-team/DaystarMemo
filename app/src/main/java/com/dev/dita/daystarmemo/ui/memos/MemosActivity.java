package com.dev.dita.daystarmemo.ui.memos;

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
import io.realm.Realm;
import io.realm.RealmResults;

public class MemosActivity extends AppCompatActivity {
    @BindView(R.id.memos_empty)
    TextView empty;
    @BindView(R.id.memos_list_view)
    ListView memosListView;
    @BindView(R.id.memos_new_memo)
    FloatingActionButton newMemoButton;

    private Realm realm;

    public void init() {
        realm = Realm.getDefaultInstance();
        RealmResults<Memo> memos = realm.where(Memo.class).findAllSorted("date");
        MemosListAdapter adapter = new MemosListAdapter(this, memos);
        memosListView.setAdapter(adapter);
        memosListView.setOnScrollListener(new AbsListView.OnScrollListener() {
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
        empty.setVisibility((adapter.isEmpty()) ? View.VISIBLE : View.GONE);
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
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NavUtils.navigateUpFromSameTask(this);
    }
}
