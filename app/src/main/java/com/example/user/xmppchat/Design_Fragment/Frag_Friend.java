package com.example.user.xmppchat.Design_Fragment;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.xmppchat.Active_users;
import com.example.user.xmppchat.ChatActivity;
import com.example.user.xmppchat.MyXMPP;
import com.example.user.xmppchat.R;

import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_Friend extends BaseFragment {
    TextView tv1;
    ListView listView, listView2;
    MyXMPP xmpp;
    ImageView imageView;
    String user, pre;
    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> arrayList2 = new ArrayList<>();
    ArrayList<String> arrayList3 = new ArrayList<>();

    public Frag_Friend() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_frag__friend, container, false);
        listView = v.findViewById(R.id.friends_list_frag);
        imageView=v.findViewById(R.id.active_frag);
        //listView2 = v.findViewById(R.id.active_usres);
        SharedPreferences pref = getActivity().getApplicationContext().getSharedPreferences("Login", 0);
        user = pref.getString("sender", null);
        xmpp = new MyXMPP();
        new GetUser().execute();

        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(imageView, "scaleX", 0.9f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(imageView, "scaleY", 0.9f);
        scaleDownX.setDuration(1500);
        scaleDownY.setDuration(1500);

        ObjectAnimator moveUpY = ObjectAnimator.ofFloat(imageView, "translationY", -50);

        moveUpY.setDuration(1500);
        moveUpY.setRepeatCount(ValueAnimator.INFINITE);

        AnimatorSet scaleDown = new AnimatorSet();
        AnimatorSet moveUp = new AnimatorSet();

        scaleDown.play(scaleDownX).with(scaleDownY);
        moveUp.play(moveUpY);

        scaleDown.start();
        moveUp.start();
        v.findViewById(R.id.active_frag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Active_users.class);
                startActivity(intent);
            }
        });
        return v;
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

            ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, arrayList2);
            listView.setAdapter(arrayAdapter);
        }
    }

    public void getRegisteredUser() throws SmackException.NotLoggedInException, XMPPException.XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException {
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
        arrayList2.clear();
        for (RosterEntry entry : entries) {
            System.out.println(entry.getName());
            arrayList2.add(entry.getUser());
            roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.manual);
            roster.createEntry(entry.getUser(), MyXMPP.connection.getUser(), null);
            Presence pres = new Presence(Presence.Type.subscribe);
            pres.setFrom(MyXMPP.connection.getUser());
            MyXMPP.connection.sendStanza(pres);
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
                ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(MyXMPP.connection);
                reconnectionManager.enableAutomaticReconnection();
                reconnectionManager.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.FIXED_DELAY);
                reconnectionManager.setFixedDelay(100);
                System.out.println("Presence changed: " + presence.getFrom() + " " + presence);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getContext(), ChatActivity.class);
                String s = arrayList2.get(position);
                intent.putExtra("receiver", s);
                Log.i("user", s);
                startActivity(intent);
            }
        });
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getContext(), ChatActivity.class);
                String s = arrayList3.get(position);
                intent.putExtra("receiver", s);
                Log.i("user", s);
                startActivity(intent);
            }
        });
    }

}
