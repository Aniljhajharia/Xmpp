package com.example.user.xmppchat.Service_And_Connections;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.IBinder;

import java.lang.ref.WeakReference;


public class MyService extends Service {
    public static ConnectivityManager cm;
    public static MyXMPP xmpp;
    private static final String DOMAIN = "192.168.1.114";
    String user;
    String pass;
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new LocalBinder<MyService>(this);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags,
                              final int startId) {


        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }


    public class LocalBinder<S> extends Binder {
        private final WeakReference<S> mService;

        public LocalBinder(final S service) {
            mService = new WeakReference<S>(service);
        }

        public S getService() {
            return mService.get();
        }
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        return super.onUnbind(intent);
    }

    public void destroy() {
        Intent i = new Intent(getApplicationContext(), MyService.class);
        stopService(i);
    }


    public void sysTest(String username,String password,Context context){
        System.out.println("Am being called");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        user = pref.getString("sender", null);
        pass = pref.getString("pass", null);

        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        xmpp = MyXMPP.getInstance(MyService.this, DOMAIN, user, pass,context);
        xmpp.connect(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try
        {
            MyXMPP.connection.disconnect();
        }
        catch (Exception e)
        {

        }
        System.out.print("Am destroyed anil killed me");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        MyXMPP.connection.disconnect();
        System.out.print("Am in onTaskRemoved and I would be killed");

    }
}
