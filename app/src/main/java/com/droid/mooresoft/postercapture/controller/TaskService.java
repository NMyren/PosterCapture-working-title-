package com.droid.mooresoft.postercapture.controller;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.droid.mooresoft.postercapture.model.AsyncPostOcrTask;
import com.droid.mooresoft.postercapture.model.TaskManager;
import com.droid.mooresoft.postercapture.model.OcrResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Ed on 10/23/15.
 */
public class TaskService extends Service {

    static TaskManager sTaskManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sTaskManager = TaskManager.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Uri imgUri = Uri.parse(intent.getStringExtra("uri"));
        String lang = intent.getStringExtra("lang");
        Future<OcrResult> future = sTaskManager.addOcrTask(imgUri, lang, this);
        AsyncPostOcrTask postOcrTask = sTaskManager.startPostOcrTask(future);
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
