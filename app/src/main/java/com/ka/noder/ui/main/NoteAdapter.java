package com.ka.noder.ui.main;

import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ka.noder.R;
import com.ka.noder.model.Note;
import com.ka.noder.ui.widget.SelectableAdapter;
import com.ka.noder.utils.NoteUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

class NoteAdapter extends SelectableAdapter<NoteAdapter.ViewHolder> {

    @Override
    public void onBindViewHolder(ViewHolder holder, Cursor cursor) {
        holder.bindItem(NoteUtil.getFromCursor(cursor));
        holder.itemView.setBackgroundColor(isSelected(cursor.getPosition()) ? 0x9934B5E4 : Color.TRANSPARENT);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personal_note, parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView noteId;
        private TextView uuid;
        private TextView title;
        private TextView text;
        private TextView date;
        private TextView status;

        ViewHolder(View itemView) {
            super(itemView);

            noteId = (TextView) itemView.findViewById(R.id.note_id);
            uuid = (TextView) itemView.findViewById(R.id.uuid);
            title = (TextView) itemView.findViewById(R.id.note_title);
            text = (TextView) itemView.findViewById(R.id.note_text);
            date = (TextView) itemView.findViewById(R.id.date);
            status = (TextView) itemView.findViewById(R.id.status);
        }

        private void bindItem(Note note){
            Date dateTime = new Date(note.getDate());
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - kk:mm", Locale.getDefault());
            String formattedDate = sdf.format(dateTime);

            noteId.setText(String.valueOf(note.getId()));
            uuid.setText(String.valueOf(note.getUuid()));
            title.setText(note.getTitle());
            text.setText(note.getText());
            date.setText(formattedDate);
            status.setText(String.valueOf(note.getStatus()));
        }
    }
}
