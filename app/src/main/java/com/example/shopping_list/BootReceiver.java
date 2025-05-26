package com.example.shopping_list;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * BroadcastReceiver that listens for device boot completion.
 * After reboot, it reads users' reminder times from Firebase and
 * reschedules the alarms for those reminders.
 */
public class BootReceiver extends BroadcastReceiver {

    /**
     * Called when the receiver receives a broadcast.
     * Specifically listens for {@link Intent#ACTION_BOOT_COMPLETED} to restore alarms after reboot.
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            FirebaseDatabase.getInstance().getReference("Users")
                    .addListenerForSingleValueEvent(new ValueEventListener() {

                        /**
                         * Called when data is successfully read from Firebase.
                         * Iterates through all users and reschedules their alarms if needed.
                         *
                         * @param snapshot The DataSnapshot from the "Users" node.
                         */
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot userSnap : snapshot.getChildren()) {
                                Long reminderTime = userSnap.child("reminderTime").getValue(Long.class);

                                if (reminderTime != null && reminderTime > System.currentTimeMillis()) {
                                    // Reschedule the alarm
                                    Intent alarmIntent = new Intent(context, ReminderReceiver.class);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                            context, 0, alarmIntent, PendingIntent.FLAG_IMMUTABLE);

                                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                                    alarmManager.setExactAndAllowWhileIdle(
                                            AlarmManager.RTC_WAKEUP,
                                            reminderTime,
                                            pendingIntent
                                    );
                                }
                            }
                        }

                        /**
                         * Called when Firebase database read is cancelled or fails.
                         *
                         * @param error The error from Firebase.
                         */
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle possible errors here (currently ignored)
                        }
                    });
        }
    }
}
