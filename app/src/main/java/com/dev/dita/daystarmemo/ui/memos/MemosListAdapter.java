package com.dev.dita.daystarmemo.ui.memos;


import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.model.database.Memo;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

public class MemosListAdapter extends RealmBaseAdapter<Memo> implements ListAdapter {

    public MemosListAdapter(Context context, OrderedRealmCollection<Memo> data) {
        super(context, data);
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

        Memo memo = getItem(position);
        String username = memo.isMe ? memo.recipient.username : memo.sender.username;
        String body = memo.body;
        String date = (String) DateUtils.getRelativeTimeSpanString(new Date().getTime(), memo.date.getTime(), DateUtils.DAY_IN_MILLIS);

        viewHolder.username.setText(username);
        viewHolder.body.setText(body);
        viewHolder.date.setText(date);
        if (memo.status.equals("unread")) {
            viewHolder.date.setTextColor(context.getResources().getColor(R.color.baseColor1));
            viewHolder.body.setTextColor(context.getResources().getColor(R.color.baseColor1));
        }

        return convertView;
    }

    public static class ViewHolder {
        @BindView(R.id.memo_list_username)
        public TextView username;
        @BindView(R.id.memo_list_details)
        public TextView body;
        @BindView(R.id.memo_list_date)
        public TextView date;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
