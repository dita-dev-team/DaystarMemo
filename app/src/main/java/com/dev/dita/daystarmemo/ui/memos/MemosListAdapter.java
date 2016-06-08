package com.dev.dita.daystarmemo.ui.memos;


import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.TextPaint;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.model.database.Memo;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * The type Memos list adapter.
 */
public class MemosListAdapter extends RealmBaseAdapter<Memo> {

    final Date NOW = new Date();
    int color;
    int defaultColor;

    public MemosListAdapter(Context context, OrderedRealmCollection<Memo> data) {
        super(context, data);
        color = context.getResources().getColor(R.color.baseColor1);
        defaultColor = Color.parseColor("#c4c4c4");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.memos_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Memo memo = adapterData.get(position);
        String username = memo.isMe ? memo.recipient.username : memo.sender.username;
        String body = memo.body;
        body = memo.isMe ? "<b>You</b>: " + body : body;
        String date = (String) DateUtils.getRelativeTimeSpanString(memo.date.getTime(), NOW.getTime(), DateUtils.DAY_IN_MILLIS);

        // Truncate body text if its long than the max width
        int maxWidth = parent.getWidth();
        TextPaint textPaint = viewHolder.body.getPaint();
        int chars = textPaint.breakText(body, true, maxWidth, null);
        if (chars < body.length()) {
            body = body.substring(0, chars);
        }

        viewHolder.username.setText(username);
        viewHolder.body.setText(Html.fromHtml(body));
        viewHolder.date.setText(date);

        // Set color of row based on memo status
        if (memo.status.equalsIgnoreCase("unread")) {
            viewHolder.date.setTextColor(color);
            viewHolder.body.setTextColor(color);
        } else {
            viewHolder.date.setTextColor(defaultColor);
            viewHolder.body.setTextColor(defaultColor);
        }

        return convertView;
    }

    public static class ViewHolder {
        @BindView(R.id.memo_list_username)
        public TextView username;
        @BindView(R.id.memo_list_details)
        public EmojiconTextView body;
        @BindView(R.id.memo_list_date)
        public TextView date;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
