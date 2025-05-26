package com.example.shopping_list;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * This activity displays the list of groups the current user is part of.
 * Users can create new groups, view existing ones, or delete a group.
 */
public class groups extends AppCompatActivity implements GroupsAdapter.OnGroupClickListener {

    private RecyclerView groupsRecyclerView;
    private GroupsAdapter adapter;
    private List<Group> groupsList = new ArrayList<>();
    private FirebaseAuth mAuth;
    private DatabaseReference groupsDatabase;

    /**
     * Initializes the activity, sets up UI elements, and loads groups for the logged-in user.
     *
     * @param savedInstanceState Saved state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_groups);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        groupsRecyclerView = findViewById(R.id.groupsRecyclerView);
        Button createGroupButton = findViewById(R.id.createGroupButton);

        mAuth = FirebaseAuth.getInstance();
        groupsDatabase = FirebaseDatabase.getInstance().getReference("Groups");

        // Set up RecyclerView with layout manager and adapter
        groupsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GroupsAdapter(groupsList, this);
        groupsRecyclerView.setAdapter(adapter);

        // Load groups for the current user
        fetchUserGroups();

        // Navigate to create group screen
        createGroupButton.setOnClickListener(v -> {
            Intent intent = new Intent(groups.this, createGroup.class);
            startActivity(intent);
        });

        // Set long-click listener for deleting a group
        adapter.setOnGroupLongClickListener(group -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Group")
                    .setMessage("Are you sure you want to delete this group?")
                    .setPositiveButton("Yes", (dialog, which) -> deleteGroup(group))
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    /**
     * Fetches the list of group IDs associated with the current user and retrieves their details.
     */
    private void fetchUserGroups() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userGroupsRef = FirebaseDatabase.getInstance().getReference("Users")
                    .child(currentUser.getUid()).child("groups");

            userGroupsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    groupsList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String groupId = snapshot.getKey();
                        getGroupDetails(groupId);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(groups.this, "Error loading groups", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Retrieves full group details from Firebase using group ID.
     *
     * @param groupId The ID of the group to retrieve.
     */
    private void getGroupDetails(String groupId) {
        groupsDatabase.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Group group = dataSnapshot.getValue(Group.class);
                if (group != null) {
                    groupsList.add(group);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(groups.this, "Error fetching group details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Handles click event when a group item is selected.
     * Opens the main shopping list screen associated with the group.
     *
     * @param group The selected group object.
     */
    @Override
    public void onGroupClick(Group group) {
        Intent intent = new Intent(groups.this, MainActivity.class);
        intent.putExtra("groupId", group.getGroupId());
        intent.putExtra("shoppingListId", group.getShoppingListId());
        startActivity(intent);
    }

    /**
     * Deletes the selected group from Firebase, removes it from the user's group list,
     * and deletes its associated shopping list.
     *
     * @param group The group to be deleted.
     */
    private void deleteGroup(Group group) {
        String groupId = group.getGroupId();
        String shoppingListId = group.getShoppingListId();

        if (groupId == null || shoppingListId == null) {
            Toast.makeText(this, "Invalid group or shopping list ID.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Remove group from "Groups" node
        groupsDatabase.child(groupId).removeValue()
                .addOnSuccessListener(aVoid -> {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        // Remove group from user's personal group list
                        DatabaseReference userGroupsRef = FirebaseDatabase.getInstance().getReference("Users")
                                .child(currentUser.getUid()).child("groups").child(groupId);

                        userGroupsRef.removeValue()
                                .addOnSuccessListener(aVoid1 -> {
                                    // Remove associated shopping list
                                    DatabaseReference shoppingListsRef = FirebaseDatabase.getInstance().getReference("ShoppingLists")
                                            .child(shoppingListId);

                                    shoppingListsRef.removeValue()
                                            .addOnSuccessListener(aVoid2 -> {
                                                groupsList.remove(group);
                                                adapter.notifyDataSetChanged();
                                                Toast.makeText(groups.this, "Group deleted successfully!", Toast.LENGTH_SHORT).show();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(groups.this, "Failed to delete shopping list: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(groups.this, "Failed to remove group from user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(groups.this, "Failed to delete group from database: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
