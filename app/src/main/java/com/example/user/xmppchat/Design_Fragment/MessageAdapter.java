package com.example.user.xmppchat.Design_Fragment;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.xmppchat.ChatActivity;
import com.example.user.xmppchat.R;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<ChatBubble> {

    private Activity activity;
    private List<ChatBubble> messages;

    public MessageAdapter(Activity context, int resource, List<ChatBubble> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.messages = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        int layoutResource = 0; // determined by view type
        ChatBubble ChatBubble = getItem(position);
        int viewType = getItemViewType(position);

        if (ChatBubble.myMessage()) {
            convertView = inflater.inflate(R.layout.right_chat_bubble, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            if (ChatBubble.Tag().equals("text")) {
                holder.msg.setText(ChatBubble.getContent());
                holder.imageView.setVisibility(View.GONE);
            } else {

                holder.msg.setVisibility(View.GONE);
                Glide.with(activity).load(ChatBubble.getContent()).into(holder.imageView);
            }

            //layoutResource = R.layout.right_chat_bubble;
        } else {
            convertView = inflater.inflate(R.layout.left_chat_bubble, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            if (ChatBubble.Tag().equals("text")) {
                holder.msg.setText(ChatBubble.getContent());
                holder.imageView.setVisibility(View.GONE);
            } else {

                holder.msg.setVisibility(View.GONE);
                Glide.with(activity).load(ChatBubble.getContent()).into(holder.imageView);
            }


            // holder.msg.setText(ChatBubble.getContent());


            //layoutResource = R.layout.left_chat_bubble;
        }


        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        // return the total number of view types. this value should never change
        // at runtime. Value 2 is returned because of left and right views.
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // return a value between 0 and (getViewTypeCount - 1)
        return position % 2;
    }

    public static class ViewHolder {
        private TextView msg;
        private ImageView imageView;

        public ViewHolder(View v) {
            msg = (TextView) v.findViewById(R.id.txt_msg);
            imageView = (ImageView) v.findViewById(R.id.img);
        }
    }
}