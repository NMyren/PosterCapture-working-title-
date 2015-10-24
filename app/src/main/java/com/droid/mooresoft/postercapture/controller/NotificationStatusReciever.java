package com.droid.mooresoft.postercapture.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Ed on 10/24/15.
 */
public class NotificationStatusReciever extends BroadcastReceiver {

    interface NotificationStatusMethods {
        void handleNotificationCleared(int id, Context context);
    }

    static final String ACTION_NOTIFICATION_CLEARED =
            "com.droid.mooresoft.postercapture.action.NOTIFICATION_CLEARED";

    static final String EXTRA_NOTIFICATION_TYPE =
            "com.droid.mooresoft.postercapture.intent.extra.NOTIFICATION_TYPE";

    @Override
    public void onReceive(Context context, Intent intent) {
        int id = intent.getIntExtra(EXTRA_NOTIFICATION_TYPE, -1);
        if (-1 == id) {
            throw new RuntimeException();
        }
        NotificationStatusMethods notificationFactory = NotificationFactory.getInstance();
        notificationFactory.handleNotificationCleared(id, context);
    }
}
