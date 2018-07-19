package com.example.user.xmppchat.Design_Fragment;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.user.xmppchat.ChatBubble2;
import com.example.user.xmppchat.Friends;
import com.example.user.xmppchat.Group_ChatActivity;
import com.example.user.xmppchat.Group_activity;
import com.example.user.xmppchat.MainActivity;
import com.example.user.xmppchat.MyXMPP;
import com.example.user.xmppchat.R;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.Subscription;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Frag_Groups extends BaseFragment {

    ArrayList<String> arrayList = new ArrayList<>();
    ArrayList<String> arrayList2 = new ArrayList<>();
    ArrayList<Subscription> arrayList3 = new ArrayList<>();
    MultiUserChatManager Mmanager;
    ArrayAdapter arrayAdapter;
    ListView listView;
    private static boolean myMessage;

    public Frag_Groups() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_frag__groups, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = (ListView) view.findViewById(R.id.frag_groups);
        try {
            Mmanager = MultiUserChatManager.getInstanceFor(MyXMPP.connection);
            List<String> joinedRooms = Mmanager.getJoinedRooms(MyXMPP.connection.getUser());
            arrayList.clear();
            for (int i = 0; i < joinedRooms.size(); i++) {
                arrayList.add(joinedRooms.get(i));
            }
            arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, arrayList);
            listView.setAdapter(arrayAdapter);
            /*arrayList2.clear();
            PubSubManager pubSubManager = new PubSubManager(MyXMPP.connection);
            List<Subscription> subscriptions = pubSubManager.getSubscriptions();
            for (int k = 0; k < subscriptions.size(); k++) {
                //s  arrayList.add(joinedRooms.get(i));
                //arrayList3.add(subscriptions.get(k));
                arrayList2.add(subscriptions.get(k).getNode());
            }*/
            //arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, arrayList);
        } catch (Exception e) {

        }
        //Intent intent=getIntent();
        // arrayList=intent.getStringArrayListExtra("key");

        try {
            // addlist();
            //listView.setAdapter(arrayAdapter);

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
                                    Toast.makeText(getContext(), message.getBody(), Toast.LENGTH_LONG).show();
                                    Log.d("message", message.getBody());
                                    if (message.getBody() != null) {
                                        try {
                                           if((Group_ChatActivity.receiver+"/"+Group_ChatActivity.sender).equals(message.getFrom()))
                                           {
                                               myMessage=true;
                                           }
                                           else
                                               myMessage=false;
                                            Group_ChatActivity.context.chatting_grp(message.getBody(), myMessage, message.getSubject());
                                        } catch (SmackException.NotConnectedException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }
                            });


                        }
                    });
                    Intent intent = new Intent(getContext(), Group_ChatActivity.class);
                    String s = arrayList.get(position);
                    intent.putExtra("receiver", s);
                    Log.i("user", s);
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), "No Groups to show", Toast.LENGTH_LONG).show();
        }
        view.findViewById(R.id.add_group_farg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
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
                            muc.create(MyXMPP.connection.getUser());
                            PubSubManager mgr = new PubSubManager(MyXMPP.connection);
                            LeafNode leaf = mgr.createNode(group_name);
                            ConfigureForm form = new ConfigureForm(DataForm.Type.submit);
                            form.setAccessModel(AccessModel.open);
                            form.setDeliverPayloads(false);
                            form.setNotifyRetract(true);
                            form.setPersistentItems(true);
                            form.setPublishModel(PublishModel.open);
                            leaf.sendConfigurationForm(form);
                            leaf.subscribe(MyXMPP.connection.getUser());
                            leaf.send();
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
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();

                        try {
                            List<String> joinedRooms = Mmanager.getJoinedRooms(MyXMPP.connection.getUser());
                            PubSubManager pubSubManager = new PubSubManager(MyXMPP.connection);
                            List<Subscription> subscriptions = pubSubManager.getSubscriptions();
                            arrayList.clear();
                            for (int j = 0; j < joinedRooms.size(); j++) {
                                arrayList.add(joinedRooms.get(j));

                            }
                           /* arrayList2.clear();
                            for (int j = 0; j < subscriptions.size(); j++) {
                                //s  arrayList.add(joinedRooms.get(i));
                                arrayList2.add(subscriptions.get(j).getNode());
                            }*/
                            addlist();

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

    public void addlist() {
        arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(arrayAdapter);
    }

    public static boolean ismyMessage(boolean mymessage) {
        myMessage = mymessage;
        return mymessage;
    }
}
