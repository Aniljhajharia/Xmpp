package com.example.user.xmppchat.Activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.user.xmppchat.R;
import com.example.user.xmppchat.Service_And_Connections.MyXMPP;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.ArrayList;
import java.util.Collection;

public class Active_users extends AppCompatActivity {
    ArrayList<String> arrayList = new ArrayList<>();
    public static int c;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_users);
        Toolbar toolbar = findViewById(R.id.tool_tool_grp);
        setSupportActionBar(toolbar);
        //toolbar.setTitle(receiver);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle("Active Friends");
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        listView=findViewById(R.id.active);
        getActiveuser();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Active_users.this, ChatActivity.class);
                String s = arrayList.get(position);
                intent.putExtra("receiver", s);
                Log.i("user", s);
                startActivity(intent);
            }
        });
    }
    public void getActiveuser() {
        final Roster roster = Roster.getInstanceFor(MyXMPP.connection);
        Collection<RosterEntry> entries = roster.getEntries();
        arrayList.clear();
        for (RosterEntry entry : entries) {
            if ((roster.getPresence(entry.getUser()).getStatus()) != null) {
                if ((roster.getPresence(entry.getUser()).getStatus()).equals("Online"))
                    arrayList.add(entry.getUser());
            }

        }
        if(arrayList.isEmpty())
        {
            Toast.makeText(this,"No Active Friends to show",Toast.LENGTH_LONG).show();
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
