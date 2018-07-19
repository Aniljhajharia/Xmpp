package com.example.user.xmppchat;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.user.xmppchat.Design_Fragment.ChatBubble;

import java.util.List;

public class MessageAdapter2 extends ArrayAdapter<ChatBubble2> {

    private Activity activity;
    private List<ChatBubble2> messages;

    public MessageAdapter2(Activity context, int resource, List<ChatBubble2> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.messages = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        int layoutResource = 0; // determined by view type
        ChatBubble2 ChatBubble2 = getItem(position);
        int viewType = getItemViewType(position);

        if (ChatBubble2.myMessage()) {
            convertView = inflater.inflate(R.layout.right_chat_bubble, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            convertView = inflater.inflate(R.layout.left_chat_bubble, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        }

        //set message content
        holder.msg2.setText(ChatBubble2.getContent());
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // return a value between 0 and (getViewTypeCount - 1)
        return position % 2;
    }

    public static class ViewHolder {
        private TextView msg2;

        public ViewHolder(View v) {
            msg2 = (TextView) v.findViewById(R.id.txt_msg);
        }
    }
}

