package com.example.user.xmppchat;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.SortedList;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.xmppchat.Design_Fragment.ChatBubble;
import com.example.user.xmppchat.Design_Fragment.MessageAdapter;
import com.example.user.xmppchat.File_upload.ApiClient;
import com.example.user.xmppchat.File_upload.ApiInterface;
import com.example.user.xmppchat.File_upload.ImageResponse;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.FileUtils;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.example.user.xmppchat.R.layout.fragment_chat;

public class ChatActivity extends AppCompatActivity {
    String presence, sender, receiver;
    ListView msglist;
    static EditText msg;
    public static ArrayList<String> arrayList = new ArrayList<>();

    static boolean myMessage = true;
    View view;
    boolean tag;
    ImageView imageView;
    EmojiconEditText emojiconEditText;
    public static ChatActivity context1;
    ApiInterface apiInterface;
    String auth = "Client-ID a2181c246a3c5d0";
    String auth2 = "client_secret: c4cdb9d293cee07b0a23c790fe259a9de924d763";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context1 = this;
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.tool_tool);
        setSupportActionBar(toolbar);
        toolbar.setTitle(receiver);
        emojiconEditText = findViewById(R.id.messgae_act);
        imageView = findViewById(R.id.emoji_button);
        view = findViewById(R.id.root);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorbar));
        }
        EmojIconActions emojIcon = new EmojIconActions(this, view, emojiconEditText, imageView, "#FF7F50", "#FF7F50", "#FF7F50");

        emojIcon.ShowEmojIcon();

        emojIcon.setIconsIds(R.drawable.ic_keyboard_black_24dp, R.drawable.ic_insert_emoticon_black_24dp);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        TextView textView = findViewById(R.id.presence);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        presence = pref.getString("presence", null);
        Intent intent = getIntent();
        receiver = intent.getStringExtra("receiver");
        actionbar.setTitle(receiver);
        sender = pref.getString("sender", null);
        textView.setText(presence);
        msglist = findViewById(R.id.chat_list);
        msg = findViewById(R.id.messgae_act);
        msglist.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msglist.setStackFromBottom(true);
        msglist.setAdapter(MainActivity.adapter);
        findViewById(R.id.send_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // sendTextmsg();
                String message = msg.getText().toString();
                if (message.equals("")) {
                    Toast.makeText(ChatActivity.this, "Please input some text...", Toast.LENGTH_SHORT).show();
                } else {
                    //add message to list
                    myMessage = true;
                    tag = true;
                    ChatBubble chatBubble = new ChatBubble(message, myMessage, "text");
                    MainActivity.ChatBubbles.add(chatBubble);
                    MainActivity.adapter.notifyDataSetChanged();
                    msg.setText("");
                    tag = true;
                    MyService.xmpp.sendMessage(sender, receiver, message, ChatActivity.this, "text");
                }
            }
        });
        findViewById(R.id.send_btn_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 123);


            }
        });

        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard", "open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard", "close");
                String uri = "@drawable/ic_insert_emoticon_black_24dp";  // where myresource (without the extension) is the file

                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Drawable res = getResources().getDrawable(imageResource);
                imageView.setImageDrawable(res);
            }
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Use this method to keep on chatting.
     *
     * @param msg1
     */
    public static void chatting(String msg1, String tag) {
        myMessage = false;
        ChatBubble chatBubble = new ChatBubble(msg1, myMessage, tag);
        MainActivity.ChatBubbles.add(chatBubble);
        MainActivity.adapter.notifyDataSetChanged();
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
                    MyService.xmpp.sendMessage(sender, receiver, message, ChatActivity.this, "image");
                }

                @Override
                public void onFailure(Call<ImageResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    public static ChatActivity getInstance() {

        return context1;
    }

}
