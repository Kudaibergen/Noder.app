package com.ka.noder.ui.note;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ka.noder.R;
import com.ka.noder.model.Note;
import com.ka.noder.provider.Contract;
import com.ka.noder.utils.AccountUtil;
import com.ka.noder.utils.NoteUtil;

public class NoteEditorFragment extends Fragment {
    EditText titleEdit;
    EditText noteEdit;
    Button saveBtn;
    int status_code;
    long noteId;
    boolean isUpdateMod;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_editor, container, false);

        titleEdit = (EditText) view.findViewById(R.id.title_note);
        noteEdit = (EditText) view.findViewById(R.id.text_note);
        saveBtn = (Button) view.findViewById(R.id.save_note);

        noteId = getArguments().getLong("id_note");
        isUpdateMod = noteId > 0;

        if (isUpdateMod) {
            noteId = getArguments().getLong("id_note");
            Note note = getNote(noteId);
            titleEdit.setText(note.getTitle());
            noteEdit.setText(note.getText());
            status_code = Contract.Notes.STATUS_UPDATED;
        } else {
            status_code = Contract.Notes.STATUS_ADDED;
        }

        saveBtn.setOnClickListener(saveNote);
        return view;
    }

    View.OnClickListener saveNote = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ContentValues cv = new ContentValues();
            cv.put(Contract.Notes.COLUMN_TITLE, titleEdit.getText().toString());
            cv.put(Contract.Notes.COLUMN_TEXT, noteEdit.getText().toString());
            cv.put(Contract.Notes.COLUMN_STATUS, status_code);
            cv.put(Contract.Notes.COLUMN_DATE, System.currentTimeMillis());

            if (isUpdateMod) {
                int rows = getContext().getContentResolver().update(Contract.Notes.CONTENT_URI,
                        cv,
                        "_id=?",
                        new String[]{String.valueOf(noteId)});
                Log.e("TAG_NoteActy", "rows updated: : " + rows);
            } else {
                Uri uri = getContext().getContentResolver().insert(Contract.Notes.CONTENT_URI, cv);
                if (uri == null) {
                    Log.e("TAG_NoteActy", "uri null!");
                    return;
                }
                noteId = Long.parseLong(uri.getLastPathSegment());
                Log.e("TAG_NoteActy", "uri inserting: " + uri);
            }

            Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            bundle.putLong("id_note", noteId);
            bundle.putInt("status_sync", status_code);

            ContentResolver.requestSync(AccountUtil.getInstance(getContext()), Contract.CONTENT_AUTHORITY, bundle);

            bundle = new Bundle();
            bundle.putLong("id_note", noteId);
            NoteFragment fragment = new NoteFragment();
            fragment.setArguments(bundle);
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment_note_container, fragment)
                    .commit();
        }
    };

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
