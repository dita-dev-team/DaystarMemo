package com.dev.dita.daystarmemo.ui.memos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.model.database.User;
import com.dev.dita.daystarmemo.model.objects.Recipient;
import com.dev.dita.daystarmemo.ui.customviews.RecipientsCompletionView;
import com.tokenautocomplete.TokenCompleteTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
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
}
