package com.example.shopping_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    private final List<Item> itemList;
    public interface OnItemClickListener {
        void onItemClick(Item item);
    }
    private final OnItemClickListener listener;
    public ShoppingListAdapter(List<Item> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.itemName.setText(item.getName());
        holder.itemQuantity.setText(String.valueOf(item.getQuantity()));
        // Set remark text (check if it's empty)
        if (item.getRemark() != null && !item.getRemark().trim().isEmpty()) {
            holder.itemRemark.setText("Remark: " + item.getRemark());
            holder.itemRemark.setVisibility(View.VISIBLE); // Ensure it's visible
        } else {
            holder.itemRemark.setText("");  // Clear text
            holder.itemRemark.setVisibility(View.GONE); // Hide if empty
        }
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity,itemRemark;

        ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemNameTextView);
            itemQuantity = itemView.findViewById(R.id.itemQuantityTextView);
            itemRemark = itemView.findViewById(R.id.itemRemarkTextView);
        }
        void bind(Item item, OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}

