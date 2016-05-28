package com.dev.dita.daystarmemo.ui.memos;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.controller.utils.DateUtils;
import com.dev.dita.daystarmemo.model.database.Memo;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

public class MemosListAdapter extends RealmBaseAdapter<Memo> implements ListAdapter {
    public MemosListAdapter(Context context, OrderedRealmCollection<Memo> data) {
        super(context, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.memos_list_item, parent, false);
            String username = adapterData.get(position).sender.username;
            String body = adapterData.get(position).body;
            String date = DateUtils.dateToString(adapterData.get(position).date);
            TextView usernameTextView = (TextView) convertView.findViewById(R.id.memo_list_username);
            TextView bodyTextView = (TextView) convertView.findViewById(R.id.memo_list_details);
            TextView dateTextView = (TextView) convertView.findViewById(R.id.memo_list_date);
            usernameTextView.setText(username);
            bodyTextView.setText(body);
            dateTextView.setText(date);
            if (adapterData.get(position).status.equals("unread")) {
                dateTextView.setTextColor(context.getResources().getColor(R.color.baseColor1));
                bodyTextView.setTextColor(context.getResources().getColor(R.color.baseColor1));
            }
        }

        return convertView;

    }
}
