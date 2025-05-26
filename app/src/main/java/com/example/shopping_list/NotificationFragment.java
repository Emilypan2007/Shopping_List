package com.example.shopping_list;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

/**
 * Fragment for setting a reminder using AlarmManager.
 * The reminder time is saved to Firebase and used to trigger a local notification.
 */
public class NotificationFragment extends Fragment {
    private Button setReminderButton;
    private FirebaseUser currentUser;
    private DatabaseReference usersRef;

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views.
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous state.
     * @return the view for the fragment UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_notification_fragment, container, false);

        setReminderButton = view.findViewById(R.id.setReminderButton);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        setReminderButton.setOnClickListener(v -> openTimePicker());
        return view;
    }

    /**
     * Opens a time picker dialog allowing the user to select a time for the reminder.
     * The selected time is saved to Firebase and an alarm is scheduled.
     */
    private void openTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        new TimePickerDialog(getContext(), (view, hourOfDay, minute1) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute1);
            calendar.set(Calendar.SECOND, 0);

            long timeInMillis = calendar.getTimeInMillis();

            saveReminderToFirebase(timeInMillis);
            setAlarm(timeInMillis);
        }, hour, minute, true).show();
    }

    /**
     * Saves the selected reminder time (in milliseconds) to Firebase under the current user's node.
     *
     * @param timeInMillis The reminder time in milliseconds since epoch.
     */
    private void saveReminderToFirebase(long timeInMillis) {
        usersRef.child(currentUser.getUid()).child("reminderTime").setValue(timeInMillis);
    }

    /**
     * Sets an alarm using AlarmManager that will trigger a broadcast at the given time.
     * The broadcast will be received by the ReminderReceiver to show a notification.
     *
     * @param timeInMillis The time at which the alarm should go off.
     */
    private void setAlarm(long timeInMillis) {
        Intent intent = new Intent(getContext(), ReminderReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getContext(),
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) requireContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);

        Toast.makeText(getContext(), "Reminder set!", Toast.LENGTH_SHORT).show();
    }
}
