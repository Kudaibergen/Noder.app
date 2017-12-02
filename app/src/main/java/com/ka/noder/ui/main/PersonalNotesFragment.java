package com.ka.noder.ui.main;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ka.noder.R;
import com.ka.noder.provider.Contract;
import com.ka.noder.ui.note.NoteActivity;
import com.ka.noder.ui.widget.DividerItemDecoration;
import com.ka.noder.ui.widget.ItemClickSupport;

public class PersonalNotesFragment extends BasicTabFragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int CURSOR_LOADER_ID = R.id.cursor_loader_id;

    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private ActionMode actionMode;
    private ToolbarActionModeCallbackImpl modeCallback;

    public static BasicTabFragment newInstance(){
        BasicTabFragment fragment = new PersonalNotesFragment();
        fragment.setTitle("Personal");
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLoaderManager().initLoader(CURSOR_LOADER_ID, Bundle.EMPTY, this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_notes, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(fabListener);

        adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);
        modeCallback = new ToolbarActionModeCallbackImpl(getContext(), this, adapter);

        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(itemClickListener);
        ItemClickSupport.addTo(recyclerView).setOnItemLongClickListener(itemLongClickListener);

        return view;
    }

    View.OnClickListener fabListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.e("TAG_PersonalList", "fab click");
            Intent intent = new Intent(getContext(), NoteActivity.class);
            intent.putExtra("isEditorMod", true);
            startActivity(intent);
        }
    };

    private ItemClickSupport.OnItemClickListener itemClickListener = new ItemClickSupport.OnItemClickListener() {
        @Override
        public void onItemClicked(RecyclerView recyclerView, int position, View v) {
            long id = adapter.getItemId(position);
            Log.e("TAG_Recycle", "Click pos:" + position + ", id: " + id);

            if (actionMode == null) {
                Intent intent = new Intent(getContext(), NoteActivity.class);
                intent.putExtra("id_note", id);
                intent.putExtra("isEditorMod", false);
                startActivity(intent);
            } else {
                onListItemSelect(position);
            }
        }
    };

    private ItemClickSupport.OnItemLongClickListener itemLongClickListener = new ItemClickSupport.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClicked(RecyclerView recyclerView, int position, View v) {
            long id = adapter.getItemId(position);
            Log.e("TAG_Recycler", "Long click pos: " + position + ", id: " + id);
            onListItemSelect(position);
            return true;
        }
    };

    private void onListItemSelect(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();
        boolean hasChecked = count > 0;

        if (hasChecked && actionMode == null) {
            actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(modeCallback);
        } else if (!hasChecked && actionMode != null){
            actionMode.finish();
        }
        if (actionMode != null) {
            actionMode.setTitle(String.valueOf(count));
        }
    }

    public void setNullToActionMode() {
        if (actionMode != null) {
            actionMode = null;
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.e("TAG_Loader", "onCreateLoader");
        switch (id) {
            case CURSOR_LOADER_ID:
                return new CursorLoader(
                        getContext(),
                        Contract.Notes.CONTENT_URI,
                        Contract.Notes.DEFAULT_PROJECTION,
                        null,
                        null,
                        Contract.Notes.DEFAULT_SORT_ORDER);
            default:
                throw new IllegalArgumentException("Unknown id loader: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        Log.e("TAG_Loader", "onLoadFinished");
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader loader) {
        Log.e("TAG_Loader", "onLoaderReset");
        adapter.swapCursor(null);
    }
}