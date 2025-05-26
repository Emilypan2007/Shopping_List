package com.example.shopping_list;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * The main activity that handles bottom navigation between app fragments.
 * Displays the shopping list, profile, and price comparison (notifications).
 * Requires a groupId passed from a previous activity.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Called when the activity is first created.
     * Sets up the layout, retrieves group ID, initializes fragments, and sets up notification channel.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Retrieve groupId passed from the previous activity
        String groupId = getIntent().getStringExtra("groupId");
        if (groupId == null) {
            throw new IllegalArgumentException("Group ID must be provided to MainActivity");
        }

        // Load the default fragment: Shopping List
        loadFragment(new ShoppingListFragment(groupId));

        // Handle navigation item selection from BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_shopping_list) {
                selectedFragment = new ShoppingListFragment(groupId);
            } else if (itemId == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.nav_prices) {
                selectedFragment = new NotificationFragment();
            }

            return loadFragment(selectedFragment);
        });

        // Create the notification channel required for showing notifications on Android 8.0+
        createNotificationChannel();
    }

    /**
     * Replaces the current fragment in the fragment container.
     *
     * @param fragment The fragment to display.
     * @return true if the fragment was loaded successfully, false otherwise.
     */
    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    /**
     * Creates a notification channel for reminders if running on Android Oreo or above.
     * Required for showing notifications on Android 8.0+.
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "reminderChannel",
                    "Reminders",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}
