package com.example.user.xmppchat.Activities;


import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.user.xmppchat.Adapters.MessageAdapter2;
import com.example.user.xmppchat.File_upload.ApiClient;
import com.example.user.xmppchat.File_upload.ApiInterface;
import com.example.user.xmppchat.File_upload.ImageResponse;
import com.example.user.xmppchat.Message_contents.ChatBubble2;
import com.example.user.xmppchat.R;
import com.example.user.xmppchat.Service_And_Connections.MyService;
import com.example.user.xmppchat.Service_And_Connections.MyXMPP;

import org.jivesoftware.smack.SmackException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private MyService mService;
    public static String presence, sender, receiver;
    ListView msglist2;
    EditText msg;
    Cloudinary cloudinary;
    Map map2 = new HashMap();
    public static Group_ChatActivity context;
    public static ArrayList<String> arrayList_grp = new ArrayList<>();
    public static List<ChatBubble2> ChatBubbles2 = new ArrayList<>();
    public static ArrayAdapter<ChatBubble2> adapter2;
    boolean myMessage;
    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(final ComponentName name, final IBinder service) {
            mService = ((MyService.LocalBinder<MyService>) service).getService();
            Log.d("TAG", "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(final ComponentName name) {
            mService = null;
            Log.d("TAG", "onServiceDisconnected");
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group__chat);
        doBindService();
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
        msglist2 = findViewById(R.id.grp_chat_list);
        msg = findViewById(R.id.grp_messgae);
        context = this;
        msglist2.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msglist2.setStackFromBottom(true);
        msglist2.setAdapter(adapter2);
        msglist2.setLongClickable(true);
        msglist2.setClickable(true);

          //for delete selected item in list on long press

        msglist2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {

                final Dialog dialog = new Dialog(Group_ChatActivity.this);
                dialog.setContentView(R.layout.dialog_delete);
                dialog.setTitle("Custom Dialog");
                dialog.show();
                dialog.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChatBubbles2.remove(position);

                        adapter2.notifyDataSetChanged();

                        Toast.makeText(Group_ChatActivity.this, "Item Deleted", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });

                return true;
            }

        });

         //to send text in joined selected group

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

         //to send image and video in joined group

        findViewById(R.id.send_btn_image_grp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Group_ChatActivity.this);
                dialog.setContentView(R.layout.dialog_choose);
                dialog.setTitle("Custom Dialog");
                dialog.show();
                dialog.findViewById(R.id.image_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, 123);
                        dialog.dismiss();
                    }
                });
                dialog.findViewById(R.id.video_dialog).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(galleryIntent, 456);
                        dialog.dismiss();
                    }
                });

            }
        });

    }

    public void sendTextmsg_grp() throws SmackException.NotConnectedException {
        String message = msg.getText().toString();
        if (!message.equalsIgnoreCase("")) {

            MyService.xmpp.sendMessage_grp(sender, receiver, message, this, "text");


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

    /**
     * keep on chatting in group
     * @param msg1
     * @param myMessage
     * @param tag
     * @throws SmackException.NotConnectedException
     */
    public void chatting_grp(String msg1, boolean myMessage, String tag) throws SmackException.NotConnectedException {
        ChatBubble2 chatBubble2 = new ChatBubble2(msg1, myMessage, tag);
        ChatBubbles2.add(chatBubble2);
        adapter2.notifyDataSetChanged();
        msg.setText("");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         //for selecting image to send and get path of image

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

         //for selecting video to send and get path of video

        else if (requestCode == 456 && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Video.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (cursor == null)
                return;

            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String filePath = cursor.getString(columnIndex);
            cursor.close();

            File file = new File(filePath);
            sendVideo(file, filePath);

        }
    }

    /**
     * used for sending video to group with setting parameter of video
     * @param file
     * @param filepath
     */
    public void sendVideo(final File file, final String filepath) {
        AsyncTask<Void, Void, Map> connectionThread = new AsyncTask<Void, Void, Map>() {
            Map config;

            @Override
            protected Map doInBackground(Void... voids) {
                try {

                    config = new HashMap();
                    config.put("cloud_name", "dqo56rw2t");
                    config.put("api_key", "494861765915649");
                    config.put("api_secret", "s5YVxxT96b_6zyJ_nOIRJkftFGk");
                    cloudinary = new Cloudinary(config);
                    map2 = cloudinary.uploader().upload(file,
                            ObjectUtils.asMap("resource_type", "video"));


                } catch (IOException e) {
                    e.printStackTrace();
                }
                return config;
            }

            @Override
            protected void onPostExecute(Map map) {
                super.onPostExecute(map);
                Log.d("Url", map2.get("url").toString());
                myMessage = true;
                msg.setText("");
                try {
                    MyService.xmpp.sendMessage_grp(sender, receiver, map2.get("url").toString(), Group_ChatActivity.this, "video");
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        };
        connectionThread.execute();  //execute thread
    }

    /**
     * used to bind service
     */
    void doBindService() {
        bindService(new Intent(this, MyService.class),mConnection ,
                Context.BIND_AUTO_CREATE);
    }
    /**
     * when activity destroyed then close the connection and unbind service
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();


        System.out.println("Activity destroyed Groupchat" );
    }
}

