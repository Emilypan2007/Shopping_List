package com.example.shopping_list;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Activity for creating a new group.
 * Allows the user to set a group name, description, and add members by user ID.
 * Saves the group to Firebase Realtime Database and updates each user's group list.
 */
public class createGroup extends AppCompatActivity {
    private TextInputEditText groupNameEditText, groupDescriptionEditText;
    private Button addMembersButton, createGroupButton;

    private FirebaseAuth mAuth;
    private DatabaseReference groupsDatabase;
    private DatabaseReference groupsRef;

    private ArrayList<String> membersList = new ArrayList<>();
    private String creatorId;

    /**
     * Called when the activity is first created.
     * Initializes Firebase, binds UI views, and sets click listeners.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_group);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase references
        mAuth = FirebaseAuth.getInstance();
        groupsDatabase = FirebaseDatabase.getInstance().getReference("Groups");
        groupsRef = FirebaseDatabase.getInstance().getReference("Groups");

        // Get current user and add them as the first member
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            creatorId = currentUser.getUid();
            membersList.add(creatorId);
        }

        // Bind views
        groupNameEditText = findViewById(R.id.groupNameEditText);
        groupDescriptionEditText = findViewById(R.id.groupDescriptionEditText);
        addMembersButton = findViewById(R.id.addMembersButton);
        createGroupButton = findViewById(R.id.createGroupButton);

        // Set up button click actions
        addMembersButton.setOnClickListener(v -> addMembers());
        createGroupButton.setOnClickListener(v -> createGroup());
    }

    /**
     * Updates the list of groups for a specific user in Firebase.
     *
     * @param userId  The user ID to update.
     * @param groupId The group ID to add.
     */
    private void updateUserGroups(String userId, String groupId) {
        DatabaseReference userGroupsRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("groups");

        userGroupsRef.child(groupId).setValue(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Group added to user's group list.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update user's groups.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Opens a dialog to enter and add a member to the group using their user ID.
     * Validates the ID and checks Firebase to ensure the user exists.
     */
    private void addMembers() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Member");
        builder.setMessage("Enter the unique ID of the user you want to add:");

        final EditText input = new EditText(this);
        input.setHint("User ID");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String userId = input.getText().toString().trim();

            if (TextUtils.isEmpty(userId)) {
                Toast.makeText(this, "User ID cannot be empty!", Toast.LENGTH_SHORT).show();
            } else if (membersList.contains(userId)) {
                Toast.makeText(this, "User is already in the group!", Toast.LENGTH_SHORT).show();
            } else {
                DatabaseReference usersDatabase = FirebaseDatabase.getInstance().getReference("Users");
                usersDatabase.child(userId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        membersList.add(userId);
                        updateUserGroups(userId, creatorId);  // Update user's group list
                        Toast.makeText(this, "User added to the group!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "User ID not found!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    /**
     * Creates a new group in Firebase with the given name, description, and member list.
     * Adds the group ID to each member's "groups" list.
     */
    private void createGroup() {
        String groupName = groupNameEditText.getText().toString().trim();
        String groupDescription = groupDescriptionEditText.getText().toString().trim();

        if (TextUtils.isEmpty(groupName)) {
            groupNameEditText.setError("Group name is required");
            groupNameEditText.requestFocus();
            return;
        }

        String shoppingListId = UUID.randomUUID().toString();
        String groupId = groupsRef.push().getKey();

        Group newGroup = new Group(groupId, groupName, groupDescription, membersList, creatorId, shoppingListId);

        groupsRef.child(groupId).setValue(newGroup).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                updateUserGroups(creatorId, groupId);
                for (String memberId : membersList) {
                    updateUserGroups(memberId, groupId);
                }

                Toast.makeText(createGroup.this, "Group created successfully!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(createGroup.this, groups.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(createGroup.this, "Failed to create group.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
