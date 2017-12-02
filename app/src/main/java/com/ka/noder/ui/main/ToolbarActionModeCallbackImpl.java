package com.ka.noder.ui.main;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ka.noder.R;
import com.ka.noder.provider.Contract;
import com.ka.noder.utils.AccountUtil;

import java.util.List;

class ToolbarActionModeCallbackImpl implements ActionMode.Callback{
    private Context context;
    private PersonalNotesFragment fragment;
    private NoteAdapter adapter;
    private AlertDialog.Builder dialog;

    ToolbarActionModeCallbackImpl(Context context, PersonalNotesFragment fragment, NoteAdapter adapter) {
        this.context = context;
        this.fragment = fragment;
        this.adapter = adapter;
        dialog = new AlertDialog.Builder(context);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        Log.e("TAG_ActionMode", "action mode onCreate");
        mode.getMenuInflater().inflate(R.menu.selected_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                Log.e("TAG_ActionMode", "action mod click delete");
                setupDialog(mode, adapter);
                break;
            default: return false;
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        adapter.clearSelection();
        fragment.setNullToActionMode();
    }

    private void setupDialog(final ActionMode mode, final NoteAdapter adapter) {
        dialog.setTitle("Удаление...");
        dialog.setMessage("Вы уверены?");
        dialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("TAG_ActionMode", "Удаление да");
                List<Long> items = adapter.getSelectedItemsById();

                deleteNotes(items);
                Log.e("TAG_ActionMode", "item ids size: " + items.size());

                mode.finish();
            }
        });
        dialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("TAG_ActionMode", "Удаление нет");
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void deleteNotes(List<Long> items) {
        String[] arr = new String[items.size()];
        for (int i = 0; i < items.size(); i++) {
            arr[i] = String.valueOf(items.get(i));
        }

        int status = 103;
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        int rows_updated = context.getContentResolver().update(
                Contract.Notes.CONTENT_URI,
                cv,
                "_id=?",
                arr
        );
        Log.e("TAG_ActionMode", "rows updated: " + rows_updated);

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putInt("status_sync", status);
        bundle.putInt("ids_arrLen", arr.length);

        for (int i = 0; i < arr.length; i++) {
            bundle.putString("arr" + i, arr[i]);
        }

        Log.e("TAG_ActionMode", "to4ka 1");

        ContentResolver.requestSync(AccountUtil.getInstance(context), Contract.CONTENT_AUTHORITY, bundle);
        Log.e("TAG_ActionMode", "to4ka 2");

    }
}