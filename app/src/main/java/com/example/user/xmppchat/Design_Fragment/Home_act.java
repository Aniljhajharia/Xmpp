package com.example.user.xmppchat.Design_Fragment;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.user.xmppchat.BaseActivity;
import com.example.user.xmppchat.MyService;
import com.example.user.xmppchat.MyXMPP;
import com.example.user.xmppchat.R;

import java.util.ArrayList;
import java.util.List;


public class Home_act extends BaseActivity {
    private MyService mService;
    ViewPager viewPager;
    TabLayout tabLayout;
    TextView textView;
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
        setContentView(R.layout.activity_home_act);
        Toolbar toolbar = findViewById(R.id.tool_home);
        textView = findViewById(R.id.logged);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        String name = MyXMPP.connection.getUser();
        doBindService();
        // actionbar.setTitle(MyXMPP.connection.getUser().substring(0,name.length()-11));
        textView.setText(MyXMPP.connection.getUser().substring(0, name.length() - 11));
        viewPager = (ViewPager) findViewById(R.id.container);
        addTabs(viewPager);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorbar));
        }
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setText("Friends");
        tabLayout.getTabAt(1).setText("Groups");
    }

    private void addTabs(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new Frag_Friend(), "friends");
        adapter.addFrag(new Frag_Groups(), "groups");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyXMPP.connection.addConnectionListener(new MyXMPP.XMPPConnectionListener());
    }

    void doBindService() {
        bindService(new Intent(this, MyService.class),mConnection ,
                Context.BIND_AUTO_CREATE);
    }

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
