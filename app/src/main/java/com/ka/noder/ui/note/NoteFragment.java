package com.ka.noder.ui.note;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ka.noder.R;
import com.ka.noder.model.Note;
import com.ka.noder.provider.Contract;
import com.ka.noder.utils.AccountUtil;
import com.ka.noder.utils.NoteUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteFragment extends Fragment {
    TextView title;
    TextView textNote;
    TextView date;
    Button editBtn;
    AlertDialog.Builder dialog;
    long id;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        title = (TextView) view.findViewById(R.id.title_note);
        textNote = (TextView) view.findViewById(R.id.text_note);
        date = (TextView) view.findViewById(R.id.date);
        editBtn = (Button) view.findViewById(R.id.edit_note);

        id = getArguments().getLong("id_note");
        Note note = getNote(id);

        Date dateTime = new Date(note.getDate());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - kk:mm", Locale.getDefault());
        String formattedDate = sdf.format(dateTime);

        title.setText(note.getTitle());
        textNote.setText(note.getText());
        date.setText(formattedDate);

        editBtn.setOnClickListener(editNote);

        dialog = new AlertDialog.Builder(getContext());
        setupDialog();
        return view;
    }

    View.OnClickListener editNote = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle bundle = new Bundle();
            bundle.putLong("id_note", id);

            NoteEditorFragment fragment = new NoteEditorFragment();
            fragment.setArguments(bundle);

            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_note_container, fragment)
                    .commit();
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.note_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                Log.e("TAG_NoteFrg", "action delete");
                dialog.show();
                break;
        }
        return true;
    }

    private void setupDialog() {
        dialog.setTitle("Удаление...");
        dialog.setMessage("Вы уверены?");
        dialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("TAG_NoteFrg", "Удаление да");
                deleteNote(id);
                getActivity().finish();
            }
        });
        dialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("TAG_NoteFrg", "Удаление нет");
                dialog.cancel();
            }
        });
    }

    private void deleteNote(long id_note) {
        int status = 103;
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        int rows_updated = getContext().getContentResolver().update(
                Contract.Notes.CONTENT_URI,
                cv,
                "_id=?",
                new String[]{String.valueOf(id_note)}
        );
        Log.e("TAG_NoteFrg", "rows updated: " + rows_updated);

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putLong("id_note", id_note);
        bundle.putInt("status_sync", Contract.Notes.STATUS_DELETED);

        ContentResolver.requestSync(AccountUtil.getInstance(getContext()), Contract.CONTENT_AUTHORITY, bundle);
    }

    private Note getNote(long noteId) {
        Cursor cursor = getContext().getContentResolver().query(
                Contract.Notes.CONTENT_URI,
                Contract.Notes.DEFAULT_PROJECTION,
                "_id=?",
                new String[]{String.valueOf(noteId)},
                Contract.Notes.DEFAULT_SORT_ORDER);

        if (cursor == null) {
            throw new NullPointerException("Cursor null!");
        }
        if (!cursor.moveToFirst()) {
            throw new CursorIndexOutOfBoundsException("Cursor incorrect!");
        }

        Note note = NoteUtil.getFromCursor(cursor);
        cursor.close();
        return note;
    }
}