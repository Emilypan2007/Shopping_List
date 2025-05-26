package com.example.shopping_list;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * The Opening screen activity.
 * This is the first screen the user sees, providing options to either register or log in.
 */
public class Opening extends AppCompatActivity {

    private Button registerButton, loginButton;

    /**
     * Called when the activity is starting.
     * Initializes the view and sets click listeners for register and login buttons.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening);

        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginButton);

        // Navigate to the SignUp activity when register button is clicked
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(Opening.this, SignUp.class);
            startActivity(intent);
        });

        // Navigate to the Login activity when login button is clicked
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(Opening.this, Login.class);
            startActivity(intent);
        });
    }
}
