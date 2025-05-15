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

public class ShoppingListFragment extends Fragment {

    private EditText itemNameEditText, itemQuantityEditText,itemRemarkEditText;
    private Button addItemButton;
    private RecyclerView shoppingListRecyclerView;
    private ImageButton backButton;

    private ArrayList<Item> items;
    private ShoppingListAdapter adapter;

    private String groupId;
    private DatabaseReference shoppingListsDatabase;
    private DatabaseReference groupsDatabase;


    public ShoppingListFragment(String groupId) {
        this.groupId = groupId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        itemNameEditText = view.findViewById(R.id.itemNameEditText);
        itemQuantityEditText = view.findViewById(R.id.itemQuantityEditText);
        addItemButton = view.findViewById(R.id.addItemButton);
        shoppingListRecyclerView = view.findViewById(R.id.shoppingListRecyclerView);
        backButton = view.findViewById(R.id.backButton);
        itemRemarkEditText = view.findViewById(R.id.itemRemarkEditText);

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
