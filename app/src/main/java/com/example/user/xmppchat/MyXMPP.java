package com.example.user.xmppchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.user.xmppchat.Design_Fragment.ChatBubble;
import com.example.user.xmppchat.Design_Fragment.MessageAdapter;
import com.google.gson.Gson;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smackx.filetransfer.FileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.CollectionNode;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.ConfigureNodeFields;
import org.jivesoftware.smackx.pubsub.FormNode;
import org.jivesoftware.smackx.pubsub.FormNodeType;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.Node;
import org.jivesoftware.smackx.pubsub.NodeExtension;
import org.jivesoftware.smackx.pubsub.NodeType;
import org.jivesoftware.smackx.pubsub.PubSubElementType;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.jivesoftware.smackx.pubsub.packet.PubSub;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.sasl.SASLErrorException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.muc.InvitationListener;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MyXMPP implements PingFailedListener {
    private static Context context_main2;
    private String serverAddress;
    private static byte[] dataReceived;
    public static boolean connected = false;
    public boolean loggedin = false;
    public static boolean isconnecting = false;
    public static boolean isToasted = true;
    private boolean chat_created = false;
    public static String loginUser;
    public static String passwordUser;
    Context context_chat, context_grp;
    public static XMPPTCPConnection connection;
    XMPPConnectionListener connectionListener;
    MyService context;
    String sender;
    String receiver_xmpp, receiver;
    boolean auth;
    Gson gson;
    boolean tag;
    ChatActivity chatActivity = new ChatActivity();
    public static MyXMPP instance = null;
    public static boolean instanceCreated = false;

    public MyXMPP(MyService context, String serverAdress, String logiUser,
                  String passwordser) {
        this.serverAddress = serverAdress;
        this.loginUser = logiUser;
        this.passwordUser = passwordser;
        this.context = context;
        init();
    }

    public MyXMPP() {

    }

    public XMPPTCPConnection getConnection() {
        return connection;
    }

    public static MyXMPP getInstance(MyService context, String server,
                                     String user, String pass, Context context11) {
        // if (instance == null) {
        instance = new MyXMPP(context, server, user, pass);
        instanceCreated = true;
        // }
        return instance;
    }

    public org.jivesoftware.smack.chat.Chat Mychat;
    ChatManagerListenerImpl mChatManagerListener;

    MMessageListener mMessageListener;

    private FileTransferManager manager;

    public void init() {
        gson = new Gson();
        initialiseConnection();


    }

    private void initialiseConnection() {

        XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration
                .builder();
        config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        config.setServiceName("192.168.1.114");
        config.setHost("192.168.1.114");
        config.setPort(5222);
        config.setDebuggerEnabled(true);
        XMPPTCPConnection.setUseStreamManagementDefault(true);
        connection = new XMPPTCPConnection(config.build());
        connectionListener = new XMPPConnectionListener();
        connection.addConnectionListener(connectionListener);
        PingManager pingManager = PingManager.getInstanceFor(connection);
        pingManager.registerPingFailedListener(this);
        pingManager.setPingInterval(100);

        mMessageListener = new MMessageListener();
        mChatManagerListener = new ChatManagerListenerImpl();
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        chatManager.addChatListener(mChatManagerListener);

        manager = FileTransferManager.getInstanceFor(connection);
        manager.addFileTransferListener(new FileTransferIMPL());

        ReconnectionManager reconnectionManager = ReconnectionManager.getInstanceFor(connection);
        reconnectionManager.enableAutomaticReconnection();
        reconnectionManager.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.FIXED_DELAY);
        reconnectionManager.setFixedDelay(100);
        connection.addConnectionListener(new XMPPConnectionListener());
        StanzaListener stanzaListener = new StanzaListener() {
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
                Log.d("groupmessage", packet + "");
            }
        };
    }

    private class ChatManagerListenerImpl implements ChatManagerListener {
        @Override
        public void chatCreated(final org.jivesoftware.smack.chat.Chat chat,
                                final boolean createdLocally) {
            chat.addMessageListener(mMessageListener);
            String groupName = String.valueOf(chat.getParticipant());
            String name=MyXMPP.connection.getUser();
            setInvitationListener(MyXMPP.connection.getUser().substring(0,name.length()-11));

        }

    }

    public void setInvitationListener(final String groupName) {
        MultiUserChatManager mManager;
        mManager = MultiUserChatManager.getInstanceFor(MyXMPP.connection);
        mManager.addInvitationListener(new InvitationListener() {
            @Override
            public void invitationReceived(XMPPConnection conn, MultiUserChat room, String inviter, String reason, String password, Message message) {
                try {
                    room.join(groupName);
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

    private class MMessageListener implements ChatMessageListener {


        @Override
        public void processMessage(final org.jivesoftware.smack.chat.Chat chat, final org.jivesoftware.smack.packet.Message message) {
            Log.d("Message", "received" + message.getBody());
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (message.getType() == Message.Type.chat
                            && message.getBody() != null) {
                        Toast.makeText(context, "message -" + "" + message.getBody(), Toast.LENGTH_LONG).show();

                        if (connection.getUser() != null)
                            chatActivity.chatting(message.getBody(), message.getSubject());

                    }

                }
            });

        }
    }

    public void connect(final Context context_main) {
        context_main2 = context_main;
        AsyncTask<Void, Void, Boolean> connectionThread = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... arg0) {
                if (connection.isConnected())
                    return false;
                isconnecting = true;
                if (isToasted)
                    new Handler(Looper.getMainLooper()).post(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(context, "=>connecting....", Toast.LENGTH_LONG).show();
                        }
                    });
                try {

                    connection.connect();
                    login();
                    connected = true;
                    DeliveryReceiptManager dm = DeliveryReceiptManager
                            .getInstanceFor(connection);
                    dm.setAutoReceiptMode(DeliveryReceiptManager.AutoReceiptMode.always);
                    dm.addReceiptReceivedListener(new ReceiptReceivedListener() {

                        @Override
                        public void onReceiptReceived(final String fromid, final String toid, final String msgid, final Stanza packet) {
                            Toast.makeText(context, "received", Toast.LENGTH_LONG).show();
                        }
                    });
                    connected = true;

                } catch (IOException e) {
                    System.out.println(e);
                } catch (SmackException e) {
                    System.out.println(e);
                } catch (XMPPException e) {
                    System.out.println(e);
                }

                return connected;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                ((MainActivity) context_main).startActivity();

            }
        };
        connectionThread.execute();

    }

    public void login() {
        try {
            connection.login(loginUser, passwordUser);
        } catch (SASLErrorException e) {
            connection.disconnect();
        } catch (XMPPException | SmackException | IOException e) {

        } catch (Exception e) {

        }
    }

    public void sendMessage(String sender, String receiver, String message, Context context, String tag) {
        context_chat = context;
        receiver_xmpp = receiver;
        this.sender = sender;

        Mychat = ChatManager.getInstanceFor(connection).createChat(
                receiver
                , mMessageListener);
        chat_created = true;


        Message massage = new Message();
        massage.setBody(message);
        massage.setSubject(tag);
        massage.setType(Message.Type.normal);

        try {
            if (connection.isAuthenticated()) {

                Mychat.sendMessage(massage);
            } else {

                login();
            }
        } catch (SmackException.NotConnectedException e) {
            Log.e("xmpp.SendMessage()", "msg Not sent!-Not Connected!");

        } catch (Exception e) {
            Log.e("xmpp.-Exception", "msg Not sent!" + e.getMessage());
        }
    }

    public void sendMessage_grp(String sender, String receiver, String message, Context context, String tag) throws SmackException.NotConnectedException {
        context_grp = context;
        this.receiver = receiver;
        Message msg = new Message(receiver, Message.Type.groupchat);
        msg.setBody(message);
        msg.setSubject(tag);
        connection.sendStanza(msg);


    }

    public void disconnect2() {
        connection.disconnect();
    }

    @Override
    public void pingFailed() {

    }


    public class XMPPConnectionListener implements ConnectionListener {
        @Override
        public void connected(XMPPConnection connection) {

        }

        @Override
        public void authenticated(XMPPConnection connection, boolean resumed) {

        }

        @Override
        public void connectionClosed() {
            System.out.println("closed");

        }


        @Override
        public void connectionClosedOnError(Exception e) {

        }

        @Override
        public void reconnectionSuccessful() {
            Log.d("reconnection", "successful");
        }

        @Override
        public void reconnectingIn(int seconds) {
            Log.d("reconnection", "reconnecting");
        }

        @Override
        public void reconnectionFailed(Exception e) {
            Log.d("reconnection", "failed");
        }

    }

    public void fileTransfer(String user, Bitmap bitmap, String filename) throws XMPPException {
        Roster roster = Roster.getInstanceFor(connection);
        String destination = roster.getPresence(user).getFrom();

        FileTransferManager manager = FileTransferManager.getInstanceFor(connection);

        final OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(destination);
        transfer.sendStream(new ByteArrayInputStream(convertFileToByte(bitmap)), filename, convertFileToByte(bitmap).length, "Sent");

        System.out.println("Status :: " + transfer.getStatus() + " Error :: " + transfer.getError() + " Exception :: " + transfer.getException());
        System.out.println("Is it done? " + transfer.isDone());
        if (transfer.getStatus().equals(FileTransfer.Status.refused))
            System.out.println("refused  " + transfer.getError());
        else if (transfer.getStatus().equals(FileTransfer.Status.error))
            System.out.println(" error " + transfer.getError());
        else if (transfer.getStatus().equals(FileTransfer.Status.cancelled))
            System.out.println(" cancelled  " + transfer.getError());
        else
            System.out.println("Success");
    }

    public byte[] convertFileToByte(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


    /*  public class FileTransferIMPL implements FileTransferListener {

          @Override
          public void fileTransferRequest(final FileTransferRequest request) {
              final IncomingFileTransfer transfer = request.accept();
              try {
                  InputStream is = transfer.recieveFile();
                  ByteArrayOutputStream os = new ByteArrayOutputStream();
                  int nRead;
                  byte[] buf = new byte[1024];
                  try {
                      while ((nRead = is.read(buf, 0, buf.length)) != -1) {
                          os.write(buf, 0, nRead);
                      }
                      os.flush();
                  } catch (IOException e) {
                      e.printStackTrace();
                  }
                  dataReceived = os.toByteArray();
                  createDirectoryAndSaveFile(dataReceived, request.getFileName());
                  Log.i("File Received", transfer.getFileName());
                  processMessage(request);
              } catch (XMPPException ex) {
                  Logger.getLogger(MyXMPP.class.getName()).log(Level.SEVERE, null, ex);
              } catch (SmackException e) {
                  e.printStackTrace();
              }
          }
      }*/
    public class FileTransferIMPL implements FileTransferListener {

        @Override
        public void fileTransferRequest(FileTransferRequest request) {
            final IncomingFileTransfer transfer = request.accept();

        }
    }

    private void createDirectoryAndSaveFile(byte[] imageToSave, String fileName) {
        File direct = new File(Environment.getExternalStorageDirectory() + "/LocShopie/Received/");
        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/LocShopie/Received/");
            wallpaperDirectory.mkdirs();
        }
        File file = new File(new File("/sdcard/LocShopie/Received/"), fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            out.write(imageToSave);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}


