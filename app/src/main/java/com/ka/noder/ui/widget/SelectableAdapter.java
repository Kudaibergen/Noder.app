package com.ka.noder.ui.widget;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;

import java.util.ArrayList;
import java.util.List;

public abstract class SelectableAdapter<VH extends RecyclerView.ViewHolder> extends CursorRecyclerAdapter<VH> {
    private SparseBooleanArray selectedItems;

    public SelectableAdapter() {
        selectedItems = new SparseBooleanArray();
    }

    public void toggleSelection(int position) {
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    public void clearSelection() {
        List<Integer> selection = getSelectedItems();
        selectedItems.clear();
        for (Integer i : selection) {
            Log.e("TAG_SelectableAdapter", "clear selection: int i = " + i);
            notifyItemChanged(i);
        }
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Long> getSelectedItemsById() {
        Log.e("TAG_SAdapter", "point");

        List<Integer> itemPositions = getSelectedItems();
        List<Long> items = new ArrayList<>(itemPositions.size());
        for (Integer i : itemPositions) {
            Log.e("TAG_SAdapter", "selected position: " + i);
            items.add(getItemId(i));
        }
        return items;
    }

    protected boolean isSelected(int position) {
        return getSelectedItems().contains(position);
    }

    private List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); ++i) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }
}