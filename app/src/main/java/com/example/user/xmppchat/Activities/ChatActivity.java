package com.example.user.xmppchat.Activities;

import android.Manifest;
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
import android.support.v4.app.ActivityCompat;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.user.xmppchat.Message_contents.ChatBubble;
import com.example.user.xmppchat.File_upload.ApiClient;
import com.example.user.xmppchat.File_upload.ApiInterface;
import com.example.user.xmppchat.File_upload.ImageResponse;
import com.example.user.xmppchat.R;
import com.example.user.xmppchat.Service_And_Connections.MyService;
import com.example.user.xmppchat.Service_And_Connections.MyXMPP;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.privacy.PrivacyListManager;
import org.jivesoftware.smackx.privacy.packet.PrivacyItem;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.jzvd.JZVideoPlayer;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatActivity extends AppCompatActivity {
    String presence, sender, receiver;
    ListView msglist;
    static EditText msg;
    Cloudinary cloudinary;
    static boolean myMessage = true;
    View view;
    boolean tag;
    Map map2 = new HashMap();
    ImageView imageView;
    EmojiconEditText emojiconEditText;
    public static ChatActivity context1;
    ApiInterface apiInterface;
    private MyService mService;
    /**
     * listener for check service connection if connected or not
     */
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
        context1 = this;
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = findViewById(R.id.tool_tool);
        setSupportActionBar(toolbar);
        doBindService();
        toolbar.setTitle(receiver);
        emojiconEditText = findViewById(R.id.messgae_act);
        imageView = findViewById(R.id.emoji_button);
        view = findViewById(R.id.root);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorbar));
        }
        /**
         * emoji library
         */
        EmojIconActions emojIcon = new EmojIconActions(this, view, emojiconEditText, imageView, "#FF7F50", "#FF7F50", "#FF7F50");

        emojIcon.ShowEmojIcon();

        emojIcon.setIconsIds(R.drawable.ic_keyboard_black_24dp, R.drawable.ic_insert_emoticon_black_24dp);
/**
 * setting action bar
 */
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        presence = pref.getString("presence", null);
        Intent intent = getIntent();
        receiver = intent.getStringExtra("receiver");
        actionbar.setTitle(receiver);
        sender = pref.getString("sender", null);
        msglist = findViewById(R.id.chat_list);
        msg = findViewById(R.id.messgae_act);
        msglist.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msglist.setStackFromBottom(true);
        msglist.setAdapter(Log_in.adapter);
        /**
         * send button to send the message using xmpp to roster
         */
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
                    Log_in.ChatBubbles.add(chatBubble);
                    Log_in.adapter.notifyDataSetChanged();
                    msg.setText("");
                    tag = true;
                    MyService.xmpp.sendMessage(sender, receiver, message, ChatActivity.this, "text");
                }
            }
        });
        /**
         * sending images to roster
         */
        findViewById(R.id.send_btn_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(ChatActivity.this);
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
                /**
                 * for sending video to user
                 */
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
        msglist.setLongClickable(true);
        msglist.setClickable(true);
        /**
         * for deleting selected item on long press
         */
        msglist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           final int position, long id) {

                final Dialog dialog = new Dialog(ChatActivity.this);
                dialog.setContentView(R.layout.dialog_delete);
                dialog.setTitle("Custom Dialog");
                dialog.show();
                dialog.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log_in.ChatBubbles.remove(position);

                        Log_in.adapter.notifyDataSetChanged();

                        Toast.makeText(ChatActivity.this, "Item Deleted", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });

                return true;
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

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_2, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorite3) {

            XMPPAddNewPrivacyList(MyXMPP.connection, receiver);

        }
        return super.onOptionsItemSelected(item);
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
        Log_in.ChatBubbles.add(chatBubble);
        Log_in.adapter.notifyDataSetChanged();
    }


    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        ActivityCompat.requestPermissions(ChatActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
        super.onActivityResult(requestCode, resultCode, data);
        /**
         * used to select image and get path of image
         */
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
            /**
             * using retrofit to get response from server
             */
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
                    Log_in.ChatBubbles.add(chatBubble);
                    Log_in.adapter.notifyDataSetChanged();
                    msg.setText("");
                    MyService.xmpp.sendMessage(sender, receiver, message, ChatActivity.this, "image");
                }

                @Override
                public void onFailure(Call<ImageResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
        /**
         * selecting video for sending and getting path
         */
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

    @Override
    protected void onResume() {
        super.onResume();
        MyXMPP.connection.addConnectionListener(new MyXMPP.XMPPConnectionListener());
    }

    /**
     * used to set video parameters and sending video to roster using cloudinary
     *
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
                ChatBubble chatBubble = new ChatBubble(map2.get("url").toString(), myMessage, "video");
                Log_in.ChatBubbles.add(chatBubble);
                Log_in.adapter.notifyDataSetChanged();
                msg.setText("");
                MyService.xmpp.sendMessage(sender, receiver, map2.get("url").toString(), ChatActivity.this, "video");
            }
        };
        connectionThread.execute();
    }

    /**
     * used to add privacy list of user
     *
     * @param connection
     * @param userName
     */
    public void XMPPAddNewPrivacyList(XMPPConnection connection, String userName) {

        PrivacyListManager privacyManager = PrivacyListManager.getInstanceFor(MyXMPP.connection);

        String listName = "friends";
        ArrayList privacyItems = new ArrayList();

        PrivacyItem item = new PrivacyItem(PrivacyItem.Type.jid, userName + "/Smack", false, 1);
        privacyItems.add(item);
        item = new PrivacyItem(PrivacyItem.Type.subscription, userName + "/Smack", false, 1);
        privacyItems.add(item);
        item = new PrivacyItem(PrivacyItem.Type.group, userName + "/Smack", false, 1);
        privacyItems.add(item);

        try {
            privacyManager.createPrivacyList(listName, privacyItems);
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        } catch (XMPPException.XMPPErrorException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * used for binding service with actvity
     */
    void doBindService() {
        bindService(new Intent(this, MyService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    /**
     * when activity destroyed then close the connection and unbind service
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("Activity destroyed chat" );
    }
    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }
    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }
}
