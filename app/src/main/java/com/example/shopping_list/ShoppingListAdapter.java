package com.example.shopping_list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter for displaying shopping list items in a RecyclerView.
 * Each item shows its name, quantity, and optional remark.
 */
public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    private final List<Item> itemList;

    /**
     * Listener interface for handling item clicks.
     */
    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    private final OnItemClickListener listener;

    /**
     * Constructor for the adapter.
     *
     * @param itemList List of Item objects to be displayed.
     * @param listener Callback for item click events.
     */
    public ShoppingListAdapter(List<Item> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    /**
     * Inflates the item layout and creates a ViewHolder.
     *
     * @param parent   The parent ViewGroup.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View for each item.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopping_list_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds data to the ViewHolder at the given position.
     *
     * @param holder   The ViewHolder to bind data to.
     * @param position The position in the list.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.itemName.setText(item.getName());
        holder.itemQuantity.setText(String.valueOf(item.getQuantity()));

        // Show remark if present
        if (item.getRemark() != null && !item.getRemark().trim().isEmpty()) {
            holder.itemRemark.setText("Remark: " + item.getRemark());
            holder.itemRemark.setVisibility(View.VISIBLE);
        } else {
            holder.itemRemark.setText("");
            holder.itemRemark.setVisibility(View.GONE);
        }

        // Set item click listener
        holder.bind(item, listener);
    }

    /**
     * Returns the total number of items in the list.
     *
     * @return Item count.
     */
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /**
     * ViewHolder class that holds and manages item views.
     */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemName, itemQuantity, itemRemark;

        /**
         * Constructor for ViewHolder.
         *
         * @param itemView The item layout view.
         */
        ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.itemNameTextView);
            itemQuantity = itemView.findViewById(R.id.itemQuantityTextView);
            itemRemark = itemView.findViewById(R.id.itemRemarkTextView);
        }

        /**
         * Binds an Item to the view and sets the click listener.
         *
         * @param item     The Item object to display.
         * @param listener The click listener.
         */
        void bind(Item item, OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(item));
        }
    }
}
