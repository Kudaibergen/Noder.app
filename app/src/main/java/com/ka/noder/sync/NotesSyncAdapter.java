package com.ka.noder.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ka.noder.account.NoderAccount;
import com.ka.noder.model.Note;
import com.ka.noder.model.StatusResponse;
import com.ka.noder.provider.Contract;
import com.ka.noder.utils.Controller;
import com.ka.noder.utils.NoteUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Response;

class NotesSyncAdapter extends AbstractThreadedSyncAdapter {
    private final AccountManager mAccountManager;

    NotesSyncAdapter(Context context) {
        super(context, true);
        mAccountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.e("TAG_Sync", "onPerformSync for account " + account.name + ", accHash: " + account.hashCode());

        String token;
        try {
            token = mAccountManager.blockingGetAuthToken(account, NoderAccount.TOKEN_TYPE_FULL_ACCESS, true);
            Log.e("TAG_Sync", "Токен получен: " + token);
        } catch (OperationCanceledException | IOException | AuthenticatorException e) {
            Log.e("TAG_Sync", "Exception " + e);
            e.printStackTrace();
        }

        int status = extras.getInt("status_sync", -1);
        Log.e("TAG_Sync", "sync status: " + status);

        String[] idsArray = getIdsArray(extras);
        boolean isFullSync = status < 0;

        if (isFullSync) {
            fullSync();
        } else {
            partSync(status, idsArray);
        }
    }

    private void fullSync(){}

    private void partSync(int sync_status, String[] ids) {
        switch (sync_status) {
            case Contract.Notes.STATUS_ADDED:
                Log.e("TAG_Sync", "Adding");
                addNote(ids);
                break;
            case Contract.Notes.STATUS_UPDATED:
                Log.e("TAG_Sync", "Updating");
                updateNote(ids);
                break;
            case Contract.Notes.STATUS_DELETED:
                Log.e("TAG_Sync", "Deleting");
                deleteNotes(ids);
                break;
            default: throw new IllegalArgumentException("Incorrect sync status code: " + sync_status);
        }
    }

    private String[] getIdsArray(Bundle extras) {
        int length = extras.getInt("ids_arrLen", -1);
        String[] array;
        if (length > 0) {
            array = new String[length];
            for (int i = 0; i < length; i++) {
                array[i] = extras.getString("arr" + i);
            }
        } else {
            array = new String[]{String.valueOf(extras.getLong("id_note", -1))};
        }

        return array;
    }

    private void addNote(String[] itemIds) {
        Cursor cursor = getCursor(itemIds);
        Note note = NoteUtil.getFromCursor(cursor);
        cursor.close();

        Log.e("TAG_Sync", "note title: " + note.getTitle());
        Log.e("TAG_Sync", "note uuid: " + note.getUuid());
        Response<StatusResponse> response;
        StatusResponse statusResponse = new StatusResponse();
        int status = Contract.Notes.STATUS_FAILED_ADD;

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String json = gson.toJson(note);
        Log.e("TAG_Sync", "json note: " + json);

        try {
            response = Controller.getApi().addNote(note).execute();
            statusResponse = response.body();
            if (statusResponse == null){
                Log.e("TAG_Sync", "Added fail, response code: " + response.code());
                statusResponse = new StatusResponse();
                statusResponse.setStatus(status);
            }
        } catch (IOException e) {
            Log.e("TAG_Sync", "Added fail, exception: " + e);
            statusResponse.setStatus(status);
        }
        Log.e("TAG_Sync", "Added Success");
        updateStatusField(itemIds, statusResponse.getStatus());
    }

    private void updateNote(String[] itemIds) {
        Cursor cursor = getCursor(itemIds);
        Note note = NoteUtil.getFromCursor(cursor);
        cursor.close();

        Response<StatusResponse> response;
        StatusResponse statusResponse = new StatusResponse();
        int status = Contract.Notes.STATUS_FAILED_UPDATE;
        try {
            response = Controller.getApi().updateNote(note).execute();
            statusResponse = response.body();
            if (statusResponse == null) {
                Log.e("TAG_Sync", "Updated fail, response code: " + response.code());
                statusResponse = new StatusResponse();
                statusResponse.setStatus(status);
            }
        } catch (IOException e) {
            Log.e("TAG_Sync", "Updated fail, exception: " + e);
            statusResponse.setStatus(status);
        }
        Log.e("TAG_Sync", "Updated Success");
        updateStatusField(itemIds, statusResponse.getStatus());
    }

    private void deleteNotes(String[] itemIds) {
        Cursor cursor = getCursor(itemIds);
        List<UUID> uuidList = getUuidList(cursor);
        cursor.close();

        Log.e("TAG_Sync", "del point. uuidList size: " + uuidList.size());
        try {
            Response response = Controller.getApi().deleteNotes(uuidList).execute();
            if (response.isSuccessful()) {
                int rows_del = getContext().getContentResolver().delete(
                        Contract.Notes.CONTENT_URI,
                        Contract.Notes.ID + "=?",
                        itemIds
                );
                Log.e("TAG_Sync", "DELETE SUCCESS!");
                Log.e("TAG_Sync", "rows deleted: " + rows_del);
            } else {
                Log.e("TAG_Sync", "Deleted fail, response code: " + response.code());
                updateStatusField(itemIds, Contract.Notes.STATUS_FAILED_DELETE);
            }
        } catch (IOException e) {
            Log.e("TAG_Sync", "Deleted fail exc: " + e);
            updateStatusField(itemIds, Contract.Notes.STATUS_FAILED_DELETE);
        }
    }

//    private void deleteNote(NoderApi api, String[] itemIds) {
//        Note note = getNote(itemIds);
//        Response response;
//        int status;
//        try {
//            UUID uuid = note.getUuid();
//            response = api.deleteNote(uuid).execute();
//            if (response.code() != 200) {
//                Log.e("TAG_Sync", "Deleted fail, response code: " + response.code());
//                status = Contract.Notes.STATUS_FAILED_DELETE;
//
//                ContentValues cv = new ContentValues();
//                cv.put(Contract.Notes.COLUMN_STATUS, status);
//                updateFieldStatus(itemIds, cv);
//            } else {
//                status = Contract.Notes.STATUS_SUCCESS;
//                int rows_deleted = getContext().getContentResolver().delete(
//                        Contract.Notes.CONTENT_URI,
//                        "_id=?",
//                        new String[]{String.valueOf(note.getId())}
//                );
//                Log.e("TAG_Sync", "DELETE SUCCESS!");
//                Log.e("TAG_Sync", "rows deleted: " + rows_deleted);
//            }
//        } catch (IOException e) {
//            Log.e("TAG_Sync", "Deleted fail, exception: " + e);
//            status = Contract.Notes.STATUS_FAILED_DELETE;
//
//            ContentValues cv = new ContentValues();
//            cv.put(Contract.Notes.COLUMN_STATUS, status);
//            updateFieldStatus(itemIds, cv);
//        }
//    }

    private void updateStatusField(String[] selectionArgsId, int sync_status_code) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.Notes.COLUMN_STATUS, sync_status_code);
        int rows_updated = getContext().getContentResolver().update(
                Contract.Notes.CONTENT_URI,
                contentValues,
                Contract.Notes.ID + "=?",
                selectionArgsId
        );
        Log.e("TAG_Sync", "STATUS field update, rows updated: " + rows_updated);
    }

    private List<UUID> getUuidList(Cursor cursor) {
        List<UUID> uuidList = new ArrayList<>();
        Note note;
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            note = NoteUtil.getFromCursor(cursor);
            uuidList.add(note.getUuid());
        }
        return uuidList;
    }

    private Cursor getCursor(String[] selectionArgsId) {
        Cursor cursor = getContext().getContentResolver().query(
                Contract.Notes.CONTENT_URI,
                Contract.Notes.DEFAULT_PROJECTION,
                Contract.Notes.ID + "=?",
                selectionArgsId,
                Contract.Notes.DEFAULT_SORT_ORDER
        );
        if (cursor == null) {
            throw new NullPointerException("Cursor null!");
        }
        if (!cursor.moveToFirst()) {
            throw new CursorIndexOutOfBoundsException("Cursor incorrect!");
        }
        return cursor;
    }
}