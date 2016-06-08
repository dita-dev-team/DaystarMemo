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

import com.baasbox.android.RequestToken;
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

/**
 * The type Memos chat activity.
 */
public class MemosChatActivity extends AppCompatActivity {

    /**
     * The List view.
     */
    @BindView(R.id.memos_chat_list_view)
    ListView listView;
    /**
     * The Edit text.
     */
    @BindView(R.id.memos_chat_edit)
    EmojiconEditText editText;
    /**
     * The Emoji button.
     */
    @BindView(R.id.memos_chat_emoji_button)
    ImageButton emojiButton;
    /**
     * The Send button.
     */
    @BindView(R.id.memos_chat_send_button)
    ImageButton sendButton;
    /**
     * The Root view.
     */
    @BindView(R.id.memos_chat_edit_view)
    View rootView;

    private EmojIconActions actions;
    private RequestToken mCurrentRequest;


    private Realm realm;
    private RealmResults<Memo> memos;
    private MemosChatListAdapter adapter;

    private String username;

    /**
     * Init.
     */
    public void init() {
        realm = Realm.getDefaultInstance();

        // Get the user for the chat
        Bundle extras = getIntent().getExtras();
        username = null;
        if (extras != null) {
            username = extras.getString("username");
        }
        // fetch all memos for this user
        memos = realm.where(User.class).equalTo("username", username).findFirst().memos.where().findAllSortedAsync("date");
        memos.addChangeListener(new RealmChangeListener<RealmResults<Memo>>() {
            @Override
            public void onChange(RealmResults<Memo> element) {
            }
        });
        adapter = new MemosChatListAdapter(this, memos);
        listView.setAdapter(adapter);
        setTitle(username);

        // Change memo status as soon as its opened
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

        // Initialize emojis
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

    /**
     * On send button clicked.
     */
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
