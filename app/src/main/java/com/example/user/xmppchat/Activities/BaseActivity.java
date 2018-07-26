package com.example.user.xmppchat.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.user.xmppchat.Design_Fragment.BaseFragment;
import com.example.user.xmppchat.R;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.Collection;

public class BaseActivity extends AppCompatActivity {

    public void displayToast(String string){
        Toast.makeText(this, string,Toast.LENGTH_LONG).show();
    }
    public void intentfire() {
       // Intent intent=new Intent(this,)
    }
    public void changefragment(BaseFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void bundle(BaseFragment fragment, String tag) {
        Bundle bundle = new Bundle();
        bundle.putString("key", tag);
        fragment.setArguments(bundle);
    }


}
