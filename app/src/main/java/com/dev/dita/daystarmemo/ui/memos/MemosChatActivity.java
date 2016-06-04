package com.dev.dita.daystarmemo.ui.memos;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.model.database.Memo;
import com.dev.dita.daystarmemo.model.database.User;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MemosChatActivity extends AppCompatActivity {

    @BindView(R.id.memos_chat_list_view)
    ListView listView;
    @BindView(R.id.memos_chat_edit)
    EmojiconEditText editText;
    @BindView(R.id.memos_chat_emoji_button)
    ImageButton emojiButton;
    @BindView(R.id.memos_chat_send_button)
    ImageButton sendButton;
    @BindView(R.id.memos_chat_edit_view)
    View rootView;

    private EmojIconActions actions;


    private Realm realm;
    private RealmResults<Memo> memos;
    private MemosChatListAdapter adapter;

    private String username;

    public void init() {
        realm = Realm.getDefaultInstance();

        Bundle extras = getIntent().getExtras();
        username = null;
        if (extras != null) {
            username = extras.getString("username");
        }
        memos = realm.where(User.class).equalTo("username", username).findFirst().memos.where().findAllSortedAsync("date");
        memos.addChangeListener(new RealmChangeListener<RealmResults<Memo>>() {
            @Override
            public void onChange(RealmResults<Memo> element) {
            }
        });
        adapter = new MemosChatListAdapter(this, memos);
        listView.setAdapter(adapter);
        setTitle(username);

        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                User user = realm.where(User.class).equalTo("username", username).findFirst();
                RealmResults<Memo> memos = user.memos.where().equalTo("status", "unread").findAll();
                for (Memo memo : memos) {
                    memo.status = "read";
                    Log.i("Status Changed", "changed");
                }

            }
        });

        actions = new EmojIconActions(this, rootView, editText, emojiButton);
        actions.ShowEmojIcon();
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


    @OnClick(R.id.memos_chat_emoji_button)
    public void onEmojiButtonClicked() {
        if (!emojiButton.isSelected()) {
            emojiButton.setSelected(true);
        } else {
            emojiButton.setSelected(false);
        }
    }

    @OnClick(R.id.memos_chat_send_button)
    public void onSendButtonClicked() {
        if (!TextUtils.isEmpty(editText.getText().toString())) {
            final String text = editText.getText().toString();
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    Memo memo = new Memo();
                    memo.isMe = true;
                    memo.recipient = realm.where(User.class).equalTo("username", username).findFirst();
                    memo.body = text;
                    if (memo.recipient.memos.size() > 0) {
                        RealmResults<Memo> memos = memo.recipient.memos.where().equalTo("latest", true).findAll();
                        for (Memo temp : memos) {
                            temp.latest = false;
                        }
                    }
                    memo.latest = true;
                    memo.status = "toBeSent";
                    memo.date = new Date();
                    memo.recipient.memos.add(memo);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    editText.getText().clear();
                }
            });
        }
    }

}
