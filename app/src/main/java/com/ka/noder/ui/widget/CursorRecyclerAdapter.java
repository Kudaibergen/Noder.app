package com.ka.noder.ui.widget;

import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.v7.widget.RecyclerView;

public abstract class CursorRecyclerAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private Cursor mCursor;
    private boolean mDataValid;
    private int mRowIdColumn;

    public CursorRecyclerAdapter() {
        this(null);
    }

    public CursorRecyclerAdapter(Cursor cursor) {
        setHasStableIds(true);
        swapCursor(cursor);
    }

    public abstract void onBindViewHolder(VH holder, Cursor cursor);

    @Override
    public void onBindViewHolder(VH holder, int position) {
        if (!mDataValid){
            throw new IllegalStateException("Cannot bind viewholder when cursor is in invalid state.");
        }
        if (!mCursor.moveToPosition(position)){
            throw new IllegalStateException("Could not move cursor to position " + position + " when trying to bind viewholder");
        }
        onBindViewHolder(holder, mCursor);
    }

    @Override
    public int getItemCount() {
        if (mDataValid){
            return mCursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        if (!mDataValid){
            throw new IllegalStateException("Cannot lookup item id when cursor is in invalid state.");
        }
        if (!mCursor.moveToPosition(position)){
            throw new IllegalStateException("Could not move cursor to position " + position + " when trying to get an item id.");
        }
        return mCursor.getLong(mRowIdColumn);
    }

    public void swapCursor(Cursor newCursor) {
        if (newCursor == mCursor){
            return;
        }
        if (newCursor != null){
            mCursor = newCursor;
            mRowIdColumn = mCursor.getColumnIndexOrThrow(BaseColumns._ID);
            mDataValid = true;
            notifyDataSetChanged();
        } else {
            notifyItemRangeRemoved(0, getItemCount());
            mCursor = null;
            mRowIdColumn = -1;
            mDataValid = false;
        }
    }
}