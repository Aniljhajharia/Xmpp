package com.example.user.xmppchat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.util.ArrayList;

public class Group_activity extends AppCompatActivity {
ArrayList<String> arrayList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_activity);
        ListView listView=(ListView)findViewById(R.id.groups_list);
        Intent intent=getIntent();
        arrayList=intent.getStringArrayListExtra("key");
        ArrayAdapter arrayAdapter = new ArrayAdapter(Group_activity.this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(MyXMPP.connection);
                MultiUserChat muc = manager.getMultiUserChat(arrayList.get(position));
                muc.addMessageListener(new MessageListener() {
                    @Override
                    public void processMessage(final Message message) {
                        Log.d("groupmessage", message.getBody());
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                boolean myMessage=false;
                                Toast.makeText(Group_activity.this,message.getBody(),Toast.LENGTH_LONG).show();
                                Log.d("message",message.getBody());
                                if(message.getBody()!=null) {
                                    try {
                                        Group_ChatActivity.context.chatting_grp(message.getBody(),true);
                                    } catch (SmackException.NotConnectedException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });


                    }
                });
                Intent intent = new Intent(Group_activity.this, Group_ChatActivity.class);
                String s = arrayList.get(position);
                intent.putExtra("receiver", s);
                Log.i("user", s);
                startActivity(intent);
            }
        });

    }
}
