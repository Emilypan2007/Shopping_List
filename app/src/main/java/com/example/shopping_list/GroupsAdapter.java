package com.example.shopping_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * RecyclerView Adapter to display a list of groups.
 * Supports click and long-click listeners for interaction with each group item.
 */
public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupViewHolder> {

    private List<Group> groups;
    private OnGroupClickListener listener;
    private OnGroupLongClickListener longClickListener;

    /**
     * Interface for handling click events on group items.
     */
    public interface OnGroupClickListener {
        /**
         * Called when a group item is clicked.
         *
         * @param group The clicked group.
         */
        void onGroupClick(Group group);
    }

    /**
     * Interface for handling long click events on group items.
     */
    public interface OnGroupLongClickListener {
        /**
         * Called when a group item is long-clicked.
         *
         * @param group The long-clicked group.
         */
        void onGroupLongClick(Group group);
    }

    /**
     * Constructor for GroupsAdapter.
     *
     * @param groups   List of groups to be displayed.
     * @param listener Click listener for group items.
     */
    public GroupsAdapter(List<Group> groups, OnGroupClickListener listener) {
        this.groups = groups;
        this.listener = listener;
    }

    /**
     * Sets a long-click listener for group items.
     *
     * @param longClickListener Listener to handle long-clicks.
     */
    public void setOnGroupLongClickListener(OnGroupLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groups.get(position);
        holder.groupNameTextView.setText(group.getName());
        holder.descriptionTextView.setText(group.getDescription());

        holder.itemView.setOnClickListener(v -> listener.onGroupClick(group));

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onGroupLongClick(group);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    /**
     * ViewHolder class for group items.
     * Holds references to the views displaying group name and description.
     */
    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupNameTextView, descriptionTextView;

        /**
         * Constructor for GroupViewHolder.
         *
         * @param itemView The group item view.
         */
        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameTextView = itemView.findViewById(R.id.groupNameText);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }
    }
}
