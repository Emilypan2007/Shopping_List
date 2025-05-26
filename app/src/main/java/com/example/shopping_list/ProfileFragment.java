package com.example.shopping_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Fragment that displays and allows editing of the user's profile information,
 * such as full name and email.
 */
public class ProfileFragment extends Fragment {

    private TextInputEditText nameEditText, emailEditText;
    private Button saveButton;

    private FirebaseAuth auth;
    private DatabaseReference userDatabaseRef;

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous state.
     * @return The View for the fragment's UI.
     */
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

        // Load user data from Firebase and populate fields
        loadUserData();

        // Save button listener
        saveButton.setOnClickListener(v -> saveUserData());

        return view;
    }

    /**
     * Loads the user's profile data (email and full name) from Firebase
     * and populates the corresponding fields in the UI.
     */
    private void loadUserData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            // Load email from Firebase Auth
            emailEditText.setText(currentUser.getEmail());

            // Load full name from Realtime Database
            userDatabaseRef.child("fullName").get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult() != null) {
                    String fullName = task.getResult().getValue(String.class);
                    if (fullName != null) {
                        nameEditText.setText(fullName);
                    } else {
                        nameEditText.setText(""); // Clear if no name found
                    }
                }
            });
        }
    }

    /**
     * Saves the user's full name to Firebase Realtime Database.
     * Displays a success or failure Toast message based on the result.
     */
    private void saveUserData() {
        String fullName = nameEditText.getText().toString().trim();

        if (!fullName.isEmpty()) {
            userDatabaseRef.child("fullName").setValue(fullName).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
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
