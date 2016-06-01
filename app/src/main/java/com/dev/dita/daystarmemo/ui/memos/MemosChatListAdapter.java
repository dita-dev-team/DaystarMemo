package com.dev.dita.daystarmemo.ui.memos;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.model.database.Memo;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

public class MemosChatListAdapter extends RealmBaseAdapter<Memo> {
    final String TAG = getClass().getName();
    public MemosChatListAdapter(Context context, OrderedRealmCollection<Memo> data) {
        super(context, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.memos_chat_list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Memo memo = adapterData.get(position);
        Log.i("ChatLIst", memo.isMe.toString());
        setAlignment(viewHolder, memo.isMe);
        viewHolder.message.setText(memo.body);
        viewHolder.info.setText(DateUtils.formatDateTime(context, memo.date.getTime(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE));
        return convertView;
    }

    private void setAlignment(ViewHolder holder, Boolean isMe) {
        if (!isMe) {
            holder.mainContent.setBackgroundResource(R.drawable.memo_chat_item_background1);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.image.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_LEFT);
            holder.image.setLayoutParams(layoutParams);

            LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) holder.info.getLayoutParams();
            layoutParams1.gravity = Gravity.LEFT;
            holder.info.setLayoutParams(layoutParams1);
        } else {
            holder.image.setVisibility(View.GONE);
            holder.mainContent.setBackgroundResource(R.drawable.memo_chat_item_background2);

            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.content.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.content.setLayoutParams(layoutParams);

            LinearLayout.LayoutParams layoutParams1 = (LinearLayout.LayoutParams) holder.info.getLayoutParams();
            layoutParams1.gravity = Gravity.RIGHT;
            holder.info.setLayoutParams(layoutParams1);
        }
    }

    public static class ViewHolder {
        @BindView(R.id.memo_chat_item_message)
        public TextView message;
        @BindView(R.id.memo_chat_item_info)
        public TextView info;
        @BindView(R.id.memo_chat_item_content)
        public LinearLayout content;
        @BindView(R.id.memo_chat_item_main_content)
        public LinearLayout mainContent;
        @BindView(R.id.memo_chat_item_user_image)
        CircleImageView image;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
