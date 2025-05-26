package com.example.shopping_list;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * BroadcastReceiver that handles alarm triggers and displays a notification reminder.
 * Typically used with AlarmManager to notify the user at a specific time.
 */
public class ReminderReceiver extends BroadcastReceiver {

    /**
     * Called when the BroadcastReceiver receives an Intent broadcast.
     * Triggers a notification to remind the user about the shopping list.
     *
     * @param context The Context in which the receiver is running.
     * @param intent  The Intent being received.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context);
    }

    /**
     * Builds and shows a notification to remind the user to check the shopping list.
     *
     * @param context The context used to build and show the notification.
     */
    private void showNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "reminderChannel")
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Shopping Reminder")
                .setContentText("Don't forget to go buy the shopping list!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1001, builder.build());
    }
}
