package com.example.shopping_list;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Fragment that displays and manages a shared shopping list for a given group.
 * Allows users to add, display, and delete items in the shopping list stored in Firebase Realtime Database.
 * The shopping list is tied to a specific group identified by groupId.
 *
 * Items include name, quantity, and an optional remark.
 *
 * Navigation includes a back button to exit this fragment and return to the Group activity.
 *
 * @author Emily Panfilov
 * @version 1.0
 * @since 2025-01-25
 */
public class ShoppingListFragment extends Fragment {

    private EditText itemNameEditText, itemQuantityEditText, itemRemarkEditText;
    private Button addItemButton;
    private RecyclerView shoppingListRecyclerView;
    private ImageButton backButton;

    private ArrayList<Item> items;
    private ShoppingListAdapter adapter;

    private String groupId;
    private DatabaseReference shoppingListsDatabase;
    private DatabaseReference groupsDatabase;

    /**
     * Constructor for the fragment that takes the groupId.
     *
     * @param groupId The unique identifier of the group whose shopping list will be managed.
     */
    public ShoppingListFragment(String groupId) {
        this.groupId = groupId;
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * Initializes UI elements, sets up RecyclerView and adapters, Firebase references,
     * listeners for adding items and back button, and loads existing shopping list from Firebase.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        itemNameEditText = view.findViewById(R.id.itemNameEditText);
        itemQuantityEditText = view.findViewById(R.id.itemQuantityEditText);
        itemRemarkEditText = view.findViewById(R.id.itemRemarkEditText);
        addItemButton = view.findViewById(R.id.addItemButton);
        shoppingListRecyclerView = view.findViewById(R.id.shoppingListRecyclerView);
        backButton = view.findViewById(R.id.backButton);

        items = new ArrayList<>();
        adapter = new ShoppingListAdapter(items, item -> deleteItemFromDatabase(item));
        shoppingListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        shoppingListRecyclerView.setAdapter(adapter);

        // Initialize Firebase references
        shoppingListsDatabase = FirebaseDatabase.getInstance().getReference("ShoppingLists");
        groupsDatabase = FirebaseDatabase.getInstance().getReference("Groups");

        addItemButton.setOnClickListener(v -> addItemToShoppingList());

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), Group.class);
            startActivity(intent);
            getActivity().finish();
        });

        // Load existing shopping list from Firebase
        loadShoppingListFromDatabase();

        return view;
    }

    /**
     * Adds a new item to the shopping list.
     * Validates input fields for name and quantity.
     * Adds the item to the local list and notifies adapter.
     * Saves the item to Firebase under the group's shopping list.
     * Clears the input fields after adding.
     */
    private void addItemToShoppingList() {
        String itemName = itemNameEditText.getText().toString().trim();
        String itemQuantityStr = itemQuantityEditText.getText().toString().trim();
        String itemRemark = itemRemarkEditText.getText().toString().trim(); // Get remark text

        if (TextUtils.isEmpty(itemName) || TextUtils.isEmpty(itemQuantityStr)) {
            Toast.makeText(getContext(), "Please enter both item name and quantity.", Toast.LENGTH_SHORT).show();
            return;
        }

        int itemQuantity = Integer.parseInt(itemQuantityStr);
        Item item = new Item(itemName, itemQuantity, itemRemark); // Pass the remark

        // Add item to UI
        items.add(item);
        adapter.notifyDataSetChanged();

        // Save item to Firebase
        saveItemToDatabase(item);

        // Clear input fields
        itemNameEditText.setText("");
        itemQuantityEditText.setText("");
        itemRemarkEditText.setText("");
    }

    /**
     * Saves a given item to the Firebase Realtime Database.
     * Retrieves or creates the shopping list ID for the group,
     * then saves the item under that shopping list.
     *
     * @param item The item to save in the database.
     */
    private void saveItemToDatabase(Item item) {
        groupsDatabase.child(groupId).child("shoppingListId").get().addOnSuccessListener(snapshot -> {
            String shoppingListId;
            if (snapshot.exists()) {
                shoppingListId = snapshot.getValue(String.class);
            } else {
                shoppingListId = UUID.randomUUID().toString();
                groupsDatabase.child(groupId).child("shoppingListId").setValue(shoppingListId);
            }

            // Save the item under the shopping list
            shoppingListsDatabase.child(shoppingListId).child("items").push().setValue(item)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Item added successfully!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to add item.", Toast.LENGTH_SHORT).show());

        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to retrieve shopping list ID.", Toast.LENGTH_SHORT).show());
    }

    /**
     * Loads the shopping list items from Firebase and updates the UI.
     * Listens for changes on the shopping list node and updates the local list accordingly.
     */
    private void loadShoppingListFromDatabase() {
        groupsDatabase.child(groupId).child("shoppingListId").get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String shoppingListId = snapshot.getValue(String.class);

                // Read the items from the shopping list
                shoppingListsDatabase.child(shoppingListId).child("items").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        items.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Item item = snapshot.getValue(Item.class);
                            if (item != null) {
                                item.setKey(snapshot.getKey());
                                items.add(item);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getContext(), "Failed to load shopping list.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to retrieve shopping list ID.", Toast.LENGTH_SHORT).show());
    }

    /**
     * Deletes an item from the Firebase database based on its key.
     *
     * @param item The item to be deleted.
     */
    private void deleteItemFromDatabase(Item item) {
        groupsDatabase.child(groupId).child("shoppingListId").get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                String shoppingListId = snapshot.getValue(String.class);
                if (item.getKey() != null) {
                    shoppingListsDatabase.child(shoppingListId).child("items").child(item.getKey())
                            .removeValue()
                            .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Item deleted", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Failed to delete item", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
}
