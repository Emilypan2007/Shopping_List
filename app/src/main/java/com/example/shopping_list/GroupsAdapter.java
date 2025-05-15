package com.example.shopping_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GroupViewHolder> {

    private List<Group> groups;
    private OnGroupClickListener listener;
    private OnGroupLongClickListener longClickListener;

    public interface OnGroupClickListener {
        void onGroupClick(Group group);
    }

    public interface OnGroupLongClickListener {
        void onGroupLongClick(Group group);
    }

    public GroupsAdapter(List<Group> groups, OnGroupClickListener listener) {
        this.groups = groups;
        this.listener = listener;
    }

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

    static class GroupViewHolder extends RecyclerView.ViewHolder {
        TextView groupNameTextView,descriptionTextView;

        public GroupViewHolder(@NonNull View itemView) {
            super(itemView);
            groupNameTextView = itemView.findViewById(R.id.groupNameText);
            descriptionTextView=itemView.findViewById(R.id.descriptionTextView);
        }
    }
}

