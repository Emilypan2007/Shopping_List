package com.example.shopping_list;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class ProfileFragment extends Fragment {


    private TextInputEditText nameEditText, emailEditText;
    private Button  saveButton;


    private FirebaseAuth auth;
    private DatabaseReference userDatabaseRef;
   

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Views
        nameEditText = view.findViewById(R.id.nameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        saveButton = view.findViewById(R.id.saveButton);

        // Firebase Setup
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        userDatabaseRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        // Load User Data
        loadUserData();


        // Set Click Listeners
        saveButton.setOnClickListener(v -> saveUserData());

        return view;
    }

    private void loadUserData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // Load email
            emailEditText.setText(currentUser.getEmail());

            // Load other user data (like fullName) from Firebase
            userDatabaseRef.child("fullName").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    String fullName = task.getResult().getValue(String.class);
                    if (fullName != null) {
                        nameEditText.setText(fullName);
                    } else {
                        nameEditText.setText(""); // If no name is saved, clear the field
                    }
                }
            });
        }
    }




    private void saveUserData() {
        String fullName = nameEditText.getText().toString().trim();

        if (!fullName.isEmpty()) {
            // Save fullName to Firebase
            userDatabaseRef.child("fullName").setValue(fullName).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Show a temporary success message
                    Toast.makeText(getActivity(), "Data saved successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Failed to save data", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

}