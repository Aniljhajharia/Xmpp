package com.example.user.xmppchat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.FontRequest;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static com.example.user.xmppchat.MyXMPP.connection;

public class Friends extends AppCompatActivity {
    ListView listView;
    MyXMPP xmpp;
    String user, pre;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> arrayList2 = new ArrayList<>();
    ArrayList<String> arrayList3 = new ArrayList<>();
    Button groups, create_groups, join_groups;
    private MultiUserChatManager Mmanager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        listView = findViewById(R.id.friends_list);
        create_groups = findViewById(R.id.create_group);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        user = pref.getString("sender", null);
        xmpp = new MyXMPP();
        new GetUser().execute();
        Mmanager = MultiUserChatManager.getInstanceFor(xmpp.getConnection());
    }

    class GetUser extends AsyncTask<Void, Void, String> {
        private ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                getRegisteredUser();
            } catch (Exception e) {

            }


            return null;
        }

        @Override
        public void onPostExecute(String re) {
            ArrayAdapter arrayAdapter = new ArrayAdapter(Friends.this, android.R.layout.simple_list_item_1, arrayList2);
            listView.setAdapter(arrayAdapter);
            // setInvitationListener();
        }
    }

    public void getRegisteredUser() {
        UserSearchManager manager = new UserSearchManager(xmpp.getConnection());
        try {
            String searchFormString = "search." + xmpp.getConnection().getServiceName();
            Log.d("***", "SearchForm: " + searchFormString);
            Form searchForm = manager.getSearchForm(searchFormString);
            Form answerForm = searchForm.createAnswerForm();

            UserSearch userSearch = new UserSearch();
            answerForm.setAnswer("Username", true);
            answerForm.setAnswer("search", user);

            ReportedData results = userSearch.sendSearchForm(xmpp.getConnection(), answerForm, searchFormString);
            if (results != null) {
                List<ReportedData.Row> rows = results.getRows();
                for (ReportedData.Row row : rows) {
                    if (user.equals(row.getValues("Username").toString()))
                        System.out.print(row.getValues("Username").toString());
                    String sbs = row.getValues("Username").toString();
                    sbs = sbs.replaceAll("\\p{P}", "");

                    arrayList.add(sbs);
                    ArrayList<String> arrayListContact = new ArrayList<>();
                    ArrayList<String> openfire = new ArrayList<>();
                    for (String str1 : arrayListContact) {
                        for (int i = 0; i < openfire.size(); i++) {
                            String opencontact = openfire.get(i);
                            if (str1 == opencontact) {

                            }
                            {

                            }
                        }

                    }
                    Log.i("MILA", sbs);
                }
            } else {
                Log.d("***", "No result found");
            }
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (SmackException.NoResponseException e) {
            e.printStackTrace();
        }
        final Roster roster = Roster.getInstanceFor(xmpp.getConnection());
        Collection<RosterEntry> entries = roster.getEntries();
        for (RosterEntry entry : entries) {
            System.out.println(entry.getName());
            arrayList2.add(entry.getUser());

        }
        roster.addRosterListener(new RosterListener() {
            public void entriesDeleted(Collection<String> addresses) {
            }

            @Override
            public void entriesAdded(Collection<String> addresses) {

            }

            public void entriesUpdated(Collection<String> addresses) {
            }

            public void presenceChanged(Presence presence) {
                pre = presence.getStatus();
                System.out.println("Presence changed: " + presence.getFrom() + " " + presence);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Friends.this, ChatActivity.class);
                String s = arrayList2.get(position);
                intent.putExtra("receiver", s);
                Log.i("user", s);
                startActivity(intent);
            }
        });
        findViewById(R.id.all_groups).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    List<String> joinedRooms = Mmanager.getJoinedRooms(MyXMPP.connection.getUser());
                    arrayList3.clear();
                    for (int i = 0; i < joinedRooms.size(); i++) {
                        arrayList3.add(joinedRooms.get(i));
                    }
                    Intent intent = new Intent(Friends.this, Group_activity.class);
                    intent.putStringArrayListExtra("key", arrayList3);
                    startActivity(intent);

                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        });
        create_groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Friends.this);
                dialog.setContentView(R.layout.dialog);
                dialog.setTitle("Custom Dialog");
                dialog.show();

                Button create = (Button) dialog.findViewById(R.id.create);
                create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText grp_name = (EditText) dialog.findViewById(R.id.dialog);
                        final String group_name = grp_name.getText().toString();

                        MultiUserChat muc = Mmanager.getMultiUserChat(group_name + "@conference.test");
                        try {
                            muc.create("anil");
                        } catch (XMPPException.XMPPErrorException e) {
                            e.printStackTrace();
                        } catch (SmackException e) {
                            e.printStackTrace();
                        }

                        try {
                            Form form = muc.getConfigurationForm();
                            Form answerForm = form.createAnswerForm();
                            for (FormField field : form.getFields()) {
                                if (!FormField.Type.hidden.name().equals(field.getType()) && field.getVariable() != null) {
                                    answerForm.setDefaultAnswer(field.getVariable());
                                }

                            }

                            List maxusers = new ArrayList();
                            maxusers.add("100");

                            List cast_values = new ArrayList();
                            cast_values.add("moderator");
                            cast_values.add("participant");
                            answerForm.setAnswer("muc#roomconfig_presencebroadcast", cast_values);

                            answerForm.setAnswer("muc#roomconfig_publicroom", true);

                            answerForm.setAnswer("muc#roomconfig_persistentroom", true);

                            answerForm.setAnswer("x-muc#roomconfig_canchangenick", true);

                            answerForm.setAnswer("x-muc#roomconfig_registration", true);

                            try {
                                muc.sendConfigurationForm(answerForm);
                                muc.join("anil");
                            } catch (SmackException.NoResponseException e) {
                                e.printStackTrace();
                            } catch (XMPPException.XMPPErrorException e) {
                                e.printStackTrace();
                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        dialog.dismiss();

                        try {
                            List<String> joinedRooms = Mmanager.getJoinedRooms(xmpp.getConnection().getUser());
                            arrayList3.clear();
                            for (int i = 0; i < joinedRooms.size(); i++) {
                                arrayList3.add(joinedRooms.get(i));
                            }
                            Intent intent = new Intent(Friends.this, Group_activity.class);
                            intent.putStringArrayListExtra("key", arrayList3);
                            startActivity(intent);

                        } catch (SmackException.NoResponseException e) {
                            e.printStackTrace();
                        } catch (XMPPException.XMPPErrorException e) {
                            e.printStackTrace();
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });


    }

    /**
     * Set the InvitationListeners
     */

}
