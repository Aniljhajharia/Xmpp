package com.example.user.xmppchat;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class Group_ChatActivity extends AppCompatActivity {
    public static String presence, sender, receiver;
    ListView msglist;
    EditText msg;
    public static Group_ChatActivity context;
    public static ArrayList<String> arrayList_grp = new ArrayList<>();
    public static List<ChatBubble2> ChatBubbles2 = new ArrayList<>();
    public static ArrayAdapter<ChatBubble2> adapter2;
    boolean myMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_group__chat);
        adapter2 = new MessageAdapter2(this, R.layout.left_chat_bubble, ChatBubbles2);
        Toolbar toolbar = findViewById(R.id.tool_tool_grp);
        setSupportActionBar(toolbar);
        toolbar.setTitle(receiver);
        EmojiconEditText emojiconEditText = findViewById(R.id.grp_messgae);
        ImageView imageView = findViewById(R.id.emoji_grp);
        View view = findViewById(R.id.root_grp);
        EmojIconActions emojIcon = new EmojIconActions(this, view, emojiconEditText, imageView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorbar));
        }
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.drawable.ic_keyboard_black_24dp, R.drawable.ic_insert_emoticon_black_24dp);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        TextView textView = findViewById(R.id.presence);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        final Intent intent = getIntent();
        receiver = intent.getStringExtra("receiver");

        actionbar.setTitle(receiver);

        sender = pref.getString("sender", null);
        msglist = findViewById(R.id.grp_chat_list);
        msg = findViewById(R.id.grp_messgae);
        context = this;
        msglist.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msglist.setStackFromBottom(true);
        msglist.setAdapter(adapter2);
        findViewById(R.id.grp_end_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendTextmsg_grp();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.send_btn_image_grp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 123);

            }
        });
    }

    public void sendTextmsg_grp() throws SmackException.NotConnectedException {
        String message = msg.getText().toString();
        if (!message.equalsIgnoreCase("")) {

            MyService.xmpp.sendMessage_grp(sender, receiver, message, this);


        }
    }

    public static Group_ChatActivity getContext() {
        return context;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorite2) {
            Intent intent = new Intent(Group_ChatActivity.this, Invitation.class);
            intent.putExtra("GroupName", receiver);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void chatting_grp(String msg1, boolean myMessage) throws SmackException.NotConnectedException {
        ChatBubble2 chatBubble2 = new ChatBubble2(msg1, myMessage);
        ChatBubbles2.add(chatBubble2);
        adapter2.notifyDataSetChanged();
        msg.setText("");

    }

    public boolean ismyMessage(boolean myMessage) {
        return myMessage;
    }
}

