package com.ka.noder.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;

public class NoderProvider extends ContentProvider {
    private NoderDatabase mOpenHelper;
    private ProviderUriMatcher mUriMatcher;

    @Override
    public boolean onCreate() {
        mOpenHelper = new NoderDatabase(getContext());
        mUriMatcher = new ProviderUriMatcher();
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return mUriMatcher.matchUri(uri).getContentType();
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mOpenHelper.getReadableDatabase();
        String table = mUriMatcher.matchUri(uri).getPath();

        UriEnum matchingUriEnum = mUriMatcher.matchUri(uri);
        Log.e("TAG_Provider", "uri query: " + uri);
        Log.e("TAG_Provider", "selection: " + selection);
        Log.e("TAG_Provider", "selectionArgs: " + Arrays.toString(selectionArgs));
        switch (matchingUriEnum) {
            case NOTES:
                Log.e("TAG_Provider", "match NOTES");
                if (selection != null && selectionArgs != null) {
                    selection = Contract.Notes.ID + " IN (" + getArgsInString(selectionArgs) + ")";
                }
                break;
            case NOTES_ID:
                Log.e("TAG_Provider", "match NOTES_ID");
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = Contract.Notes.ID + " = " + id;
                } else {
                    selection = selection + " AND " + Contract.Notes.ID + " = " + id;
                }
                break;
            default: throw new UnsupportedOperationException("Unknown query uri: " + uri);
        }
        Log.e("TAG_Provider", "selection after matching: " + selection);

        Cursor cursor = database.query(table, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        String table = mUriMatcher.matchUri(uri).getPath();
        long id = database.insertWithOnConflict(table, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        if (id > 0){
            Log.e("TAG_Provider", "row id: " + id);
            getContext().getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, id);
        } else {
            throw new SQLException("Problem while inserting into uri: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        String table = mUriMatcher.matchUri(uri).getPath();

        UriEnum matchingUriEnum = mUriMatcher.matchUri(uri);
        Log.e("TAG_Provider", "uri query: " + uri);
        Log.e("TAG_Provider", "selection: " + selection);
        Log.e("TAG_Provider", "selectionArgs: " + Arrays.toString(selectionArgs));
        switch (matchingUriEnum) {
            case NOTES:
                Log.e("TAG_Provider", "match NOTES");

                if (selection != null && selectionArgs != null) {
                    selection = Contract.Notes.ID + " IN (" + getArgsInString(selectionArgs) + ")";
                }
                break;
            case NOTES_ID:
                Log.e("TAG_Provider", "match NOTES_ID");
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = Contract.Notes.ID + " = " + id;
                } else {
                    selection = selection + " AND " + Contract.Notes.ID + " = " + id;
                }
                break;
            default: throw new UnsupportedOperationException("Unknown delete uri: " + uri);
        }
        Log.e("TAG_Provider", "selection after matching: " + selection);

        int rowsDeleted = database.delete(table, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        UriEnum matchingUriEnum = mUriMatcher.matchUri(uri);
        String table = mUriMatcher.matchUri(uri).getPath();
        Log.e("TAG_Provider", "uri update: " + uri);
        Log.e("TAG_Provider", "selection: " + selection);
        Log.e("TAG_Provider", "selectionArgs: " + Arrays.toString(selectionArgs));
        switch (matchingUriEnum) {
            case NOTES:
                Log.e("TAG_Provider", "match NOTES");

                if (selection != null && selectionArgs != null) {
                    selection = Contract.Notes.ID + " IN (" + getArgsInString(selectionArgs) + ")";
                }
                break;
            case NOTES_ID:
                Log.e("TAG_Provider", "match NOTES_ID");
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    selection = Contract.Notes.ID + " = " + id;
                } else {
                    selection = selection + " AND " + Contract.Notes.ID + " = " + id;
                }
                break;
            default: throw new UnsupportedOperationException("Unknown update uri: " + uri);
        }
        Log.e("TAG_Provider", "selection after matching: " + selection);
        int rowsUpdated = database.update(table, contentValues, selection, selectionArgs);
        Log.e("TAG_Provider", "rows updated: " + rowsUpdated);
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private String getArgsInString(String[] selectionArgs) {
        StringBuilder inList = new StringBuilder(selectionArgs.length * 2);
        for (int i = 0; i < selectionArgs.length; i++) {
            if (i > 0) {
                inList.append(",");
            }
            inList.append("?");
        }
        return inList.toString();
    }
}