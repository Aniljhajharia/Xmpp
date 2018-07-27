package com.example.user.xmppchat.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.user.xmppchat.Message_contents.ChatBubble;
import com.example.user.xmppchat.R;

import java.util.List;

import cn.jzvd.JZVideoPlayerStandard;

public class MessageAdapter extends ArrayAdapter<ChatBubble> {

    private Activity activity;
    private List<ChatBubble> messages;
    String video_url;
    ProgressDialog pd;
    private MediaController mediaController;

    public MessageAdapter(Activity context, int resource, List<ChatBubble> objects) {
        super(context, resource, objects);
        this.activity = context;
        this.messages = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        int layoutResource = 0; // determined by view type
        ChatBubble ChatBubble = getItem(position);
        int viewType = getItemViewType(position);

          //to check if message is from friend or own message, if message is of user then set to right bubble

        if (ChatBubble.myMessage()) {
            convertView = inflater.inflate(R.layout.right_chat_bubble, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

             // to check if message is text
            if (ChatBubble.Tag().equals("text")) {
                holder.msg.setText(ChatBubble.getContent());
                holder.imageView.setVisibility(View.GONE);
                holder.jzVideoPlayerStandard.setVisibility(View.GONE);

                 // to check if message is image

            } else if (ChatBubble.Tag().equals("image")) {
                holder.jzVideoPlayerStandard.setVisibility(View.GONE);
                holder.msg.setVisibility(View.GONE);
                Glide.with(activity).load(ChatBubble.getContent()).into(holder.imageView);
            }

             // if message is video

            else {
                holder.imageView.setVisibility(View.GONE);
                holder.msg.setVisibility(View.GONE);
                video_url = ChatBubble.getContent();


                holder.jzVideoPlayerStandard.setUp(video_url,
                        JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL,
                        "");
            }


        }

         // if message is not of user then set to left bubble

        else {
            convertView = inflater.inflate(R.layout.left_chat_bubble, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            if (ChatBubble.Tag().equals("text")) {
                holder.jzVideoPlayerStandard.setVisibility(View.GONE);
                holder.msg.setText(ChatBubble.getContent());
                holder.imageView.setVisibility(View.GONE);
            } else if (ChatBubble.Tag().equals("image")) {
                holder.jzVideoPlayerStandard.setVisibility(View.GONE);
                holder.msg.setVisibility(View.GONE);
                Glide.with(activity).load(ChatBubble.getContent()).into(holder.imageView);
            } else {
                holder.imageView.setVisibility(View.GONE);
                holder.msg.setVisibility(View.GONE);
                video_url = ChatBubble.getContent();
                holder.jzVideoPlayerStandard.setUp(video_url,
                        JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL,
                        "");
            }

        }


        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    public static class ViewHolder {
        private TextView msg;
        private ImageView imageView;
        //private VideoView videoView;
        JZVideoPlayerStandard jzVideoPlayerStandard;

        public ViewHolder(View v) {
            msg = (TextView) v.findViewById(R.id.txt_msg);
            imageView = (ImageView) v.findViewById(R.id.img);
            jzVideoPlayerStandard = (JZVideoPlayerStandard) v.findViewById(R.id.videoplayer);
        }
    }
}