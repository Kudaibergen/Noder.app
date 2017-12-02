package com.ka.noder.provider;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class Contract {
    public static final String CONTENT_AUTHORITY = "com.ka.noder.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private Contract(){}

    public static final class Notes {
        public static final String TABLE_NAME = "notes";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, TABLE_NAME);
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/notes";
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/note";

        public static final String ID = BaseColumns._ID;
        public static final String UUID = "uuid";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_TEXT = "text";
        public static final String COLUMN_PASSWORD = "password";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_STATUS = "status";

        public static final int STATUS_SUCCESS = 100;
        public static final int STATUS_ADDED = 101;
        public static final int STATUS_UPDATED = 102;
        public static final int STATUS_DELETED = 103;
        public static final int STATUS_FAILED_ADD = 201;
        public static final int STATUS_FAILED_UPDATE = 202;
        public static final int STATUS_FAILED_DELETE = 203;

        public static final String DEFAULT_SORT_ORDER = ID + " ASC";
        public static final String[] DEFAULT_PROJECTION = new String[]{
                Notes.ID,
                Notes.UUID,
                Notes.COLUMN_TITLE,
                Notes.COLUMN_TEXT,
                Notes.COLUMN_PASSWORD,
                Notes.COLUMN_DATE,
                Notes.COLUMN_STATUS
        };
        static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + Notes.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Notes.UUID + " BLOB DEFAULT(randomblob(16)),"
                + Notes.COLUMN_TITLE + " TEXT NOT NULL,"
                + Notes.COLUMN_TEXT + " TEXT NOT NULL,"
                + Notes.COLUMN_PASSWORD + " TEXT,"
                + Notes.COLUMN_DATE + " LONG NOT NULL,"
                + Notes.COLUMN_STATUS + " INTEGER NOT NULL"
                + ");";

        private Notes(){}
    }
}