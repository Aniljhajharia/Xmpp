package com.example.user.xmppchat.Design_Fragment;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.sax.TextElementListener;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.xmppchat.ChatActivity;
import com.example.user.xmppchat.MyService;
import com.example.user.xmppchat.MyXMPP;
import com.example.user.xmppchat.R;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Chat extends BaseFragment {

    String presence, sender, receiver;
    MyXMPP xmpp = new MyXMPP();
    ListView msglist;
    EditText msg;
    Button msgbtn;
    String mmm;
    Context context;
    public static ArrayList<String> arrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("Login", 0);
        presence = pref.getString("presence", null);
        Intent intent = getActivity().getIntent();
        receiver = intent.getStringExtra("receiver");
        sender = pref.getString("sender", null);
        ((TextView)getView().findViewById(R.id.presence)).setText(presence);
        msglist = getView().findViewById(R.id.chat_list);
        msg = getView().findViewById(R.id.messgae);
        msglist.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msglist.setStackFromBottom(true);
        msgbtn = getView().findViewById(R.id.send_btn);
        msgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTextmsg();
            }
        });
    }

    public void sendTextmsg() {
        String message = msg.getText().toString();
        if (!message.equalsIgnoreCase("")) {
            msg.setText("");
            MyService.xmpp.sendMessage(sender,receiver, message, getContext(),"text");
            chatting(sender + " : " + message);

        }
    }

    public void chatting(String msg) {

        arrayList.add(msg);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, arrayList);
        msglist.setAdapter(arrayAdapter);
    }
}
