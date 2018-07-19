package com.example.user.xmppchat;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import com.example.user.xmppchat.Design_Fragment.ChatBubble;
import com.example.user.xmppchat.File_upload.ApiClient;
import com.example.user.xmppchat.File_upload.ApiInterface;
import com.example.user.xmppchat.File_upload.ImageResponse;

import org.jivesoftware.smack.SmackException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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

            MyService.xmpp.sendMessage_grp(sender, receiver, message, this,"text");


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

    public void chatting_grp(String msg1, boolean myMessage,String tag) throws SmackException.NotConnectedException {
        ChatBubble2 chatBubble2 = new ChatBubble2(msg1, myMessage,tag);
        ChatBubbles2.add(chatBubble2);
        adapter2.notifyDataSetChanged();
        msg.setText("");

    }

    public boolean ismyMessage(boolean myMessage) {
        return myMessage;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123 && resultCode == Activity.RESULT_OK) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (cursor == null)
                return;

            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            File file = new File(filePath);

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("upload", file.getName(), reqFile);
            RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload_test");


            // retrofit2.Call<okhttp3.ResponseBody> req =apiInterface.postImage(body, name);
            ApiClient apiClient = new ApiClient();
            Retrofit retrofit = apiClient.getClient();
            ApiInterface api = retrofit.create(ApiInterface.class);
            Call<ImageResponse> req = api.postImage("", "", MultipartBody.Part.createFormData(
                    "image",
                    file.getName(),
                    RequestBody.create(MediaType.parse("image/*"), file)
            ));

            req.enqueue(new Callback<ImageResponse>() {
                @Override
                public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                    Log.d("Tag", "" + response.body());
                    ImageResponse data = new ImageResponse();
                    String message = response.body().data.link;
                    myMessage = true;
                    ChatBubble chatBubble = new ChatBubble(message, myMessage, "image");
                    MainActivity.ChatBubbles.add(chatBubble);
                    MainActivity.adapter.notifyDataSetChanged();
                    msg.setText("");
                    try {
                        MyService.xmpp.sendMessage_grp(sender, receiver, message, Group_ChatActivity.this, "image");
                    } catch (SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ImageResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
}

