package com.dev.dita.daystarmemo.ui.memos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.controller.bus.MemoBus;
import com.dev.dita.daystarmemo.model.baas.MemoBaas;
import com.dev.dita.daystarmemo.model.database.Memo;
import com.dev.dita.daystarmemo.model.database.User;
import com.dev.dita.daystarmemo.model.objects.Recipient;
import com.dev.dita.daystarmemo.ui.customviews.RecipientsCompletionView;
import com.tokenautocomplete.TokenCompleteTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * The type New memo activity.
 */
public class NewMemoActivity extends AppCompatActivity implements TokenCompleteTextView.TokenListener<Recipient> {

    @BindView(R.id.new_memo_recipient)
    RecipientsCompletionView recipients;
    @BindView(R.id.new_memo_edit)
    EmojiconEditText editText;
    @BindView(R.id.new_memo_emoji_button)
    ImageButton emojiButton;
    @BindView(R.id.new_memo_send_button)
    ImageButton sendButton;
    @BindView(R.id.new_memo_edit_view)
    View rootView;
    private ImageButton closeButton;
    private EmojIconActions actions;

    private Realm realm;
    private RealmResults<User> users;

    private int memoCount;

    /**
     * Init.
     */
    public void init() {
        // Load all users from database
        realm = Realm.getDefaultInstance();
        users = realm.where(User.class).findAll();
        ArrayList<Recipient> recipientsList = new ArrayList<>();
        for (User user : users) {
            recipientsList.add(new Recipient(user.username, user.name));
        }
        recipients.performBestGuess(false);
        // Setup recipients
        recipients.setAdapter(new ArrayAdapter<Recipient>(this, android.R.layout.simple_list_item_1, recipientsList));
        recipients.setTokenListener(this);
        // Setup emojis
        actions = new EmojIconActions(this, rootView, editText, emojiButton);
        actions.ShowEmojIcon();

        emojiButton.setEnabled(false);
        sendButton.setEnabled(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_memo);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Setup custom toolbar
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.memos_actionbar, null);
        closeButton = (ImageButton) view.findViewById(R.id.new_memo_close);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getSupportActionBar().setCustomView(view);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onTokenAdded(Recipient token) {
        // Enable buttons when a recipient is added
        emojiButton.setEnabled(true);
        sendButton.setEnabled(true);
    }

    @Override
    public void onTokenRemoved(Recipient token) {
        // Disable buttons if recipient list is empty
        if (!hasRecipient()) {
            emojiButton.setEnabled(false);
            sendButton.setEnabled(false);
        }
    }

    public boolean hasRecipient() {
        return recipients.getObjects().size() > 0;
    }

    @OnClick(R.id.new_memo_send_button)
    public void onSendButtonClicked() {
        final String text = editText.getText().toString();

        if (!TextUtils.isEmpty(text)) {
            memoCount = recipients.getObjects().size();
            Memo memo = new Memo();
            memo.body = text;
            memo.date = new Date();
            MemoBaas.sendMemo(memo, recipients.getObjects());

            /*for (final Recipient recipient : recipients.getObjects()) {
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Memo memo = new Memo();
                        memo.isMe = true;
                        memo.recipient = realm.where(User.class).equalTo("username", recipient.username).findFirst();
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
                        Memo memo = realm.where(Memo.class).equalTo("recipient.username", recipient.username).equalTo("latest", true).findFirst();
                        MemoBaas.sendMemo(memo, recipients.);;
                    }
                });
            }*/
        }
    }

    @Subscribe
    public void onEvent(MemoBus.SendMemoResult result) {
        if (result.error) {
            Toast.makeText(this, "Failed to send memo", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Memo sent successfully", Toast.LENGTH_SHORT).show();
        }
    }
}
