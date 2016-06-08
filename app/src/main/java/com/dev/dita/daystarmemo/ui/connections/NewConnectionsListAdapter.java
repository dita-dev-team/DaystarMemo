package com.dev.dita.daystarmemo.ui.connections;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.baasbox.android.BaasUser;
import com.dev.dita.daystarmemo.R;
import com.dev.dita.daystarmemo.controller.utils.ImageUtils;
import com.dev.dita.daystarmemo.model.database.User;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class NewConnectionsListAdapter extends ArrayAdapter<BaasUser> {

    private List<User> yourConnections;
    private int addedColor;

    public NewConnectionsListAdapter(Context context, int resource, List<BaasUser> objects) {
        super(context, resource, objects);
        addedColor = Color.parseColor("#c4c4c4");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.new_connection_list_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BaasUser user = getItem(position);
        String username = user.getName();
        String name = "";
        String imageSrc = "";

        if (user.getScope(BaasUser.Scope.FRIEND) != null) {
            name = user.getScope(BaasUser.Scope.FRIEND).getString("name", "");
            imageSrc = user.getScope(BaasUser.Scope.FRIEND).getString("image", "");
        }

        viewHolder.username.setText(username);
        viewHolder.name.setText(name);

        if (!TextUtils.isEmpty(imageSrc)) {
            Bitmap image = ImageUtils.decodeBitmapFromString(imageSrc);
            viewHolder.image.setImageBitmap(image);
        }

        for (User temp : yourConnections) {
            if (temp.username.equals(username)) {
                viewHolder.username.setText(username + " (added)");
                viewHolder.username.setTextColor(addedColor);
                break;
            }
        }

        return convertView;
    }

    public void setYourConnections(List<User> yourConnections) {
        this.yourConnections = yourConnections;
    }

    public static class ViewHolder {
        @BindView(R.id.new_connections_list_username)
        TextView username;
        @BindView(R.id.new_connections_list_name)
        TextView name;
        @BindView(R.id.new_connections_list_icon)
        CircleImageView image;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
