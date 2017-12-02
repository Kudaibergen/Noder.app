package com.ka.noder.ui.note;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ka.noder.R;

public class NoteActivity extends AppCompatActivity {
    Fragment contentFragment;
    Fragment editorFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        Intent intent = getIntent();
        boolean isEditorMod = intent.getBooleanExtra("isEditorMod", false);
        long id_note = intent.getLongExtra("id_note", -1);

        Log.e("NoteActy", "id: " + id_note);

        Bundle bundle = new Bundle();
        bundle.putLong("id_note", id_note);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (isEditorMod) {
            editorFragment = new NoteEditorFragment();
            editorFragment.setArguments(bundle);
            transaction.replace(R.id.fragment_note_container, editorFragment);
        } else {
            contentFragment = new NoteFragment();
            contentFragment.setArguments(bundle);
            transaction.replace(R.id.fragment_note_container, contentFragment);
        }
        transaction.commit();
    }
}