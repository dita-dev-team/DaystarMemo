package com.dev.dita.daystarmemo.ui.connections;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.controller.utils.ImageUtils;
import com.dev.dita.daystarmemo.model.database.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

public class ConnectionsListAdapter extends RealmBaseAdapter<User> {

    public ConnectionsListAdapter(Context context, OrderedRealmCollection<User> data) {
        super(context, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.connections_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        User user = adapterData.get(position);
        String username = user.username;
        viewHolder.username.setText(username);

        if (!TextUtils.isEmpty(user.image)) {
            Bitmap image = null;
            image = ImageUtils.decodeBitmapFromString(user.image);
            viewHolder.image.setImageBitmap(image);
        } else {
            Drawable image = context.getResources().getDrawable(R.drawable.default_profile);
            viewHolder.image.setImageDrawable(image);
        }

        return convertView;
    }

    public static class ViewHolder {
        @BindView(R.id.connections_list_username)
        TextView username;
        @BindView(R.id.connections_list_icon)
        CircleImageView image;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
