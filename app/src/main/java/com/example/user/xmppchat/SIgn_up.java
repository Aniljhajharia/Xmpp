package com.example.user.xmppchat;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.io.IOException;
import java.util.Map;

public class SIgn_up extends AppCompatActivity {
EditText editText1,editText2;
XMPPTCPConnection connection;
    String u,p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        editText1=findViewById(R.id.user);
        editText2=findViewById(R.id.password_sign);
        u=editText1.getText().toString();
         p=editText2.getText().toString();
        findViewById(R.id.sign).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              register();

            }
        });
        findViewById(R.id.log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SIgn_up.this,Log_in.class);
                startActivity(intent);
            }
        });
    }
    public void register() {
        AsyncTask<Void, Void, Map> connectionThread = new AsyncTask<Void, Void, Map>() {
            Map config;
            @Override
            protected Map doInBackground(Void... voids) {
                XMPPTCPConnectionConfiguration.Builder confg = XMPPTCPConnectionConfiguration
                        .builder();
                confg.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                confg.setServiceName("192.168.1.114");
                confg.setHost("192.168.1.114");
                confg.setPort(5222);
                confg.setDebuggerEnabled(true);
                XMPPTCPConnection.setUseStreamManagementDefault(true);
                connection = new XMPPTCPConnection(confg.build());

                try {
                    connection.connect(); // Here we create the connection
                } catch (SmackException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
                AccountManager accountManager = AccountManager.getInstance(connection);
                try {
                    if (accountManager.supportsAccountCreation()) {
                        accountManager.sensitiveOperationOverInsecureConnection(true);
                        accountManager.createAccount(u, p);

                    }
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
                return config;
            }

            @Override
            protected void onPostExecute(Map map) {
                super.onPostExecute(map);
                Toast.makeText(SIgn_up.this,"registered",Toast.LENGTH_LONG).show();

            }
        };
        connectionThread.execute();
    }
}
