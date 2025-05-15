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

public class createGroup extends AppCompatActivity {
    private TextInputEditText groupNameEditText, groupDescriptionEditText;
    private Button addMembersButton, createGroupButton;

    private FirebaseAuth mAuth;
    private DatabaseReference groupsDatabase;
    private DatabaseReference groupsRef;

    private ArrayList<String> membersList = new ArrayList<>();
    private String creatorId;

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
        groupsRef = FirebaseDatabase.getInstance().getReference("Groups");  // הוספת הגדרה ל-groupsRef

        // Get current user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            creatorId = currentUser.getUid();
            membersList.add(creatorId); // Add creator to the members list
        }

        // Bind views
        groupNameEditText = findViewById(R.id.groupNameEditText);
        groupDescriptionEditText = findViewById(R.id.groupDescriptionEditText);
        addMembersButton = findViewById(R.id.addMembersButton);
        createGroupButton = findViewById(R.id.createGroupButton);

        // Set up buttons
        addMembersButton.setOnClickListener(v -> addMembers());
        createGroupButton.setOnClickListener(v -> createGroup());
    }

    private void updateUserGroups(String userId, String groupId) {
        DatabaseReference userGroupsRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("groups");

        // הוספת מזהה הקבוצה לרשימה
        userGroupsRef.child(groupId).setValue(true).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Group added to user's group list.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to update user's groups.", Toast.LENGTH_SHORT).show();
            }
        });
    }

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

                        // עדכון רשימת הקבוצות של המשתמש
                        updateUserGroups(userId, creatorId);  // עדכון קבוצת המשתמש

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

    private void createGroup() {
        String groupName = groupNameEditText.getText().toString().trim();
        String groupDescription = groupDescriptionEditText.getText().toString().trim();

        if (TextUtils.isEmpty(groupName)) {
            groupNameEditText.setError("Group name is required");
            groupNameEditText.requestFocus();
            return;
        }

        // Create a unique shopping list ID
        String shoppingListId = UUID.randomUUID().toString();


        String groupId = groupsRef.push().getKey(); // יצירת מזהה ייחודי לקבוצה
        // מזהה המשתמש היוצר כבר הוגדר כ-creatorId

        // יצירת אובייקט הקבוצה
        Group newGroup = new Group(groupId, groupName, groupDescription, membersList, creatorId,shoppingListId);


        // שמירת הקבוצה בבסיס הנתונים
        groupsRef.child(groupId).setValue(newGroup).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // הוספת מזהה הקבוצה למשתמש היוצר
                updateUserGroups(creatorId, groupId);

                // הוספת מזהה הקבוצה לכל משתמש ברשימת החברים
                for (String memberId : membersList) {
                    updateUserGroups(memberId, groupId);
                }

                Toast.makeText(createGroup.this, "Group created successfully!", Toast.LENGTH_SHORT).show();

                // מעבר למסך הקבוצות
                Intent intent = new Intent(createGroup.this, groups.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(createGroup.this, "Failed to create group.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
