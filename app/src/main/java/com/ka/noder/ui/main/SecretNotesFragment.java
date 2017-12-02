package com.ka.noder.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ka.noder.R;

public class SecretNotesFragment extends BasicTabFragment{

    public static BasicTabFragment newInstance(){
        BasicTabFragment fragment = new SecretNotesFragment();
        fragment.setTitle("Secret");
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_secret_notes, container, false);
        return view;
    }
}