package com.droid.mooresoft.postercapture.controller;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;

import com.droid.mooresoft.postercapture.controller.NotificationStatusReciever.NotificationStatusMethods;

import com.droid.mooresoft.postercapture.R;

/**
 * Created by Ed on 10/24/15.
 */
public class NotificationFactory implements NotificationStatusMethods {

    private static final NotificationFactory sInstance;

    static final int ID_SUCCESS_NOTI = 0;

    static final int ID_FAILED_NOTI = 1;

    static {
        sInstance = new NotificationFactory();
    }

    private NotificationFactory() {
    }

    /**
     * Gets the instance of {@link NotificationFactory}.
     *
     * @return The global instance of {@link NotificationFactory}.
     */
    public static NotificationFactory getInstance() {
        return sInstance;
    }

    /**
     * Post a new success notification or updates the existing one.
     *
     * @param context The application {@link Context}.
     */
    public void postSuccessNotification(Context context) {
        postNotification(ID_SUCCESS_NOTI, context);
    }

    /**
     * Posts a new failure notification or updates the existing one.
     *
     * @param context The application {@link Context}.
     */
    public void postFailedNotification(Context context) {
        postNotification(ID_FAILED_NOTI, context);
    }

    /**
     * Sets the active notification count for the specified notification type to zero.
     *
     * @param id      The identifier for the type of notification cleared. See {@link #ID_SUCCESS_NOTI}
     *                and {@link #ID_FAILED_NOTI}.
     * @param context The application {@link Context}.
     */
    public void handleNotificationCleared(int id, Context context) {
        updateNumActive(0, id, context);
    }

    private void postNotification(int id, Context context) {
        int numActive = getNumActive(id, context) + 1;
        String title = String.valueOf(numActive);
        if (ID_SUCCESS_NOTI == id) {
            title += numActive > 1 ? " new poster captures"
                    : " new poster capture";
        } else {
            title += numActive > 1 ? " poster captures failed"
                    : " poster capture failed";
        }
        updateNumActive(numActive, id, context);
        Intent deleteIntent = new Intent()
                .setAction(NotificationStatusReciever.ACTION_NOTIFICATION_CLEARED)
                .putExtra(NotificationStatusReciever.EXTRA_NOTIFICATION_TYPE, id);
        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context, id, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification noti = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setAutoCancel(true)
                .setDeleteIntent(pendingIntent)
                .build();
        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(id, noti);
    }

    private int getNumActive(int id, Context context) {
        String fileName = context.getResources().getString(R.string.notification_state_file_key);
        SharedPreferences prefs = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        String key = String.valueOf(id);
        return prefs.getInt(key, 0);
    }

    private void updateNumActive(int numActive, int id, Context context) {
        String fileName = context.getResources().getString(R.string.notification_state_file_key);
        SharedPreferences.Editor edit =
                context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit();
        String key = String.valueOf(id);
        edit.putInt(key, numActive)
                .commit();
    }
}
