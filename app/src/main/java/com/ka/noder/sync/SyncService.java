package com.ka.noder.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class SyncService extends Service {
    private static final Object syncAdapterLock = new Object();
    private static NotesSyncAdapter syncAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        synchronized (syncAdapterLock) {
            if (syncAdapter == null) {
                Log.e("TAG_SyncService", "onCreate NotesSyncAdapter create");
                syncAdapter = new NotesSyncAdapter(getApplicationContext());
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("TAG_SyncService", "onBind");
        return syncAdapter.getSyncAdapterBinder();
    }
}