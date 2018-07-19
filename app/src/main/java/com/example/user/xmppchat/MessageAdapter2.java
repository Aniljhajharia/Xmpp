package com.example.user.xmppchat;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.user.xmppchat.Design_Fragment.Chat;
import com.example.user.xmppchat.Design_Fragment.ChatBubble;
import com.example.user.xmppchat.Design_Fragment.MessageAdapter;

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
//            convertView = inflater.inflate(R.layout.right_chat_bubble, parent, false);
//            holder = new ViewHolder(convertView);
//            convertView.setTag(holder);
            convertView = inflater.inflate(R.layout.right_chat_bubble, parent, false);
            holder = new MessageAdapter2.ViewHolder(convertView);
            convertView.setTag(holder);
           if(ChatBubble2.Tag()!=null)
           {
               if (ChatBubble2.Tag().equals("text")) {
                   holder.msg2.setText(ChatBubble2.getContent());
                   holder.imageView.setVisibility(View.GONE);
               } else {

                   holder.msg2.setVisibility(View.GONE);
                   Glide.with(activity).load(ChatBubble2.getContent()).into(holder.imageView);
               }
           }

        } else {
//            convertView = inflater.inflate(R.layout.left_chat_bubble, parent, false);
//            holder = new ViewHolder(convertView);
//            convertView.setTag(holder);
            convertView = inflater.inflate(R.layout.left_chat_bubble, parent, false);
            holder = new MessageAdapter2.ViewHolder(convertView);
            convertView.setTag(holder);
            if(ChatBubble2.Tag()!=null)
            {
                if (ChatBubble2.Tag().equals("text")) {
                    holder.msg2.setText(ChatBubble2.getContent());
                    holder.imageView.setVisibility(View.GONE);
                } else {

                    holder.msg2.setVisibility(View.GONE);
                    Glide.with(activity).load(ChatBubble2.getContent()).into(holder.imageView);
                }
            }


        }

        //set message content

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
        private ImageView imageView;
        public ViewHolder(View v) {
            msg2 = (TextView) v.findViewById(R.id.txt_msg);
            imageView = (ImageView) v.findViewById(R.id.img);
        }
    }
}

