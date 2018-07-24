package com.example.user.xmppchat.Design_Fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.user.xmppchat.ChatActivity;
import com.example.user.xmppchat.R;

import java.util.List;

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

        if (ChatBubble.myMessage()) {
            convertView = inflater.inflate(R.layout.right_chat_bubble, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            if (ChatBubble.Tag().equals("text")) {
                holder.msg.setText(ChatBubble.getContent());
                holder.imageView.setVisibility(View.GONE);
                holder.videoView.setVisibility(View.GONE);
            } else if (ChatBubble.Tag().equals("image")) {
                holder.videoView.setVisibility(View.GONE);
                holder.msg.setVisibility(View.GONE);
                Glide.with(activity).load(ChatBubble.getContent()).into(holder.imageView);
            } else {
                holder.imageView.setVisibility(View.GONE);
                holder.msg.setVisibility(View.GONE);
                video_url = ChatBubble.getContent();
                pd = new ProgressDialog(activity);
                pd.setMessage("Buffering video please wait...");
                pd.show();
                if (mediaController == null) {
                    mediaController = new MediaController(activity);

                    // Set the videoView that acts as the anchor for the MediaController.
                    mediaController.setAnchorView(holder.videoView);


                    // Set MediaController for VideoView
                    holder.videoView.setMediaController(mediaController);
                }
                Uri uri = Uri.parse(video_url);
                holder.videoView.requestFocus();
                holder.videoView.setVideoURI(uri);
                holder.videoView.start();
                holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        //close the progress dialog when buffering is done
                        pd.dismiss();
                    }
                });

            }

            //layoutResource = R.layout.right_chat_bubble;
        } else {
            convertView = inflater.inflate(R.layout.left_chat_bubble, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            if (ChatBubble.Tag().equals("text")) {
                holder.videoView.setVisibility(View.GONE);
                holder.msg.setText(ChatBubble.getContent());
                holder.imageView.setVisibility(View.GONE);
            } else if (ChatBubble.Tag().equals("image")) {
                holder.videoView.setVisibility(View.GONE);
                holder.msg.setVisibility(View.GONE);
                Glide.with(activity).load(ChatBubble.getContent()).into(holder.imageView);
            } else {
                holder.imageView.setVisibility(View.GONE);
                holder.msg.setVisibility(View.GONE);
                video_url = ChatBubble.getContent();
                pd = new ProgressDialog(activity);
                pd.setMessage("Buffering video please wait...");
                pd.show();

                Uri uri = Uri.parse(video_url);
                holder.videoView.setVideoURI(uri);
                holder.videoView.start();
                holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        //close the progress dialog when buffering is done
                        pd.dismiss();
                    }
                });
            }

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
        private VideoView videoView;

        public ViewHolder(View v) {
            msg = (TextView) v.findViewById(R.id.txt_msg);
            imageView = (ImageView) v.findViewById(R.id.img);
            videoView = (VideoView) v.findViewById(R.id.video);
        }
    }
}