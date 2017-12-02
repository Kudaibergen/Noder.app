package com.ka.noder.utils;

import android.database.Cursor;
import android.util.Log;

import com.ka.noder.model.Note;
import com.ka.noder.provider.Contract;

import java.util.UUID;

public class NoteUtil {

    public static Note getFromCursor(Cursor cursor) {
        long unixTime = cursor.getLong(cursor.getColumnIndex(Contract.Notes.COLUMN_DATE));
        Log.e("NoteUtil", "unixTime in sec: " + unixTime + ", in ms: " + (unixTime * 1000L));

        Note note = new Note();
        note.setId(cursor.getInt(cursor.getColumnIndex(Contract.Notes.ID)));
        note.setUuid(UUID.nameUUIDFromBytes(cursor.getBlob(cursor.getColumnIndex(Contract.Notes.UUID))));
        note.setTitle(cursor.getString(cursor.getColumnIndex(Contract.Notes.COLUMN_TITLE)));
        note.setText(cursor.getString(cursor.getColumnIndex(Contract.Notes.COLUMN_TEXT)));
        note.setDate(unixTime);
        note.setStatus(cursor.getInt(cursor.getColumnIndex(Contract.Notes.COLUMN_STATUS)));

        return note;
    }
}