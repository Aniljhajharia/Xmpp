package com.example.user.xmppchat.Design_Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.user.xmppchat.BaseActivity;
import com.example.user.xmppchat.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Previous_Chats extends BaseFragment {


    public Previous_Chats() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_previous__chats, container, false);
    }

}
