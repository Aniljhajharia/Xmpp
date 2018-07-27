package com.example.user.xmppchat.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.xmppchat.Message_contents.ChatBubble;
import com.example.user.xmppchat.Design_Fragment.Home_act;
import com.example.user.xmppchat.Adapters.MessageAdapter;
import com.example.user.xmppchat.R;
import com.example.user.xmppchat.Service_And_Connections.MyService;
import com.example.user.xmppchat.Service_And_Connections.MyXMPP;

import java.util.ArrayList;
import java.util.List;

public class Log_in extends BaseActivity implements View.OnClickListener {
    private MyService mService;
    EditText username;
    EditText password;
    public static  String user,pass;
    public static ArrayAdapter<ChatBubble> adapter;
    public static List<ChatBubble> ChatBubbles = new ArrayList<>();
    MyXMPP xmpp = new MyXMPP();
    /**
     * service listener to check if service connected or not
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
        setContentView(R.layout.activity_main);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adapter = new MessageAdapter(this, R.layout.left_chat_bubble, ChatBubbles);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_black_24dp);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorbar));
        }

         // used to register new user

        findViewById(R.id.login).setOnClickListener(this);
        doBindService();
        findViewById(R.id.new_account).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Log_in.this, SIgn_up.class);
                startActivity(intent);
            }
        });
    }

    /**
     * saving username and password in shared preference and login user with function systest.
     * @param v
     */
    @Override
    public void onClick(View v) {
         user = username.getText().toString();
         pass = password.getText().toString();
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("sender", user);
        editor.putString("pass", pass);
        editor.commit();

        if (mService != null) {
            mService.sysTest(user, pass, this);
        } else {
            Toast.makeText(this, "Please wait for the service to get connected", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This method is used to bind the service.
     */
    void doBindService() {
        bindService(new Intent(this, MyService.class), mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mService == null) {
            doBindService();
        }
    }

    /**
     * used to check authentication..if authenticated then session started and sent to home page
     */
    public void startActivity() {
        if (xmpp.getConnection().isAuthenticated()) {
            Intent intent = new Intent(this, Home_act.class);
            startActivity(intent);
        } else {
            displayToast("Please enter correct username and password");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * if activity destroyed then disconnect the connection of user and unbind service
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            MyXMPP.connection.disconnect();
            unbindService(mConnection);
        } catch (Exception e) {

        }

        System.out.println("Activity destroyed");
    }
}
