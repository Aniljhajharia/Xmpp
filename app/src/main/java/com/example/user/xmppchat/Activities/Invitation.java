package com.example.user.xmppchat.Activities;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.user.xmppchat.R;
import com.example.user.xmppchat.Service_And_Connections.MyXMPP;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smackx.muc.InvitationRejectionListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;

import java.util.ArrayList;
import java.util.Collection;

public class Invitation extends AppCompatActivity {

    ArrayList<String> arrayList2 = new ArrayList<>();
    String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);

        Toolbar toolbar = findViewById(R.id.tool_tool_grp);
        setSupportActionBar(toolbar);
        //toolbar.setTitle(receiver);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle("Add Member");
        getRoaster();
        initList();
        groupName = getIntent().getStringExtra("GroupName");

    }

    public void initList() {
        ListView listView = findViewById(R.id.rosters);
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList2);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                sendInvitation(groupName, arrayList2.get(position));
                onBackPressed();
            }
        });
    }


    public void getRoaster() {
        final Roster roster = Roster.getInstanceFor(MyXMPP.connection);
        Collection<RosterEntry> entries = roster.getEntries();
        for (RosterEntry entry : entries) {
            System.out.println(entry.getName());
            arrayList2.add(entry.getUser());

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Send the inivitation to the group
     *
     * @param groupName
     * @param usename
     */
    public void sendInvitation(String groupName, String usename) {
        MultiUserChatManager manager = MultiUserChatManager.getInstanceFor(MyXMPP.connection);
        MultiUserChat muc = manager.getMultiUserChat(groupName);
        muc.getOccupants();
        try {
            muc.invite(usename, "Join the group");
            muc.addInvitationRejectionListener(new InvitationRejectionListener() {
                @Override
                public void invitationDeclined(String invitee, String reason) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Reject Request.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            //muc.sendMessage(usename + " has joined the room ");


        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }
}



