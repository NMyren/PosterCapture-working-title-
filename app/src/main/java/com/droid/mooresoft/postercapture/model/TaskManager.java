package com.droid.mooresoft.postercapture.model;

import android.content.Context;
import android.net.Uri;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Ed on 10/22/15.
 */
public class TaskManager {

    static TaskManager sInstance;

    ExecutorService mExecutorService;

    static {
        sInstance = new TaskManager();
    }

    private TaskManager() {
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    public static TaskManager getInstance() {
        return sInstance;
    }

    public Future<String> addOcrTask(Uri imgUri, Context context) {
        return mExecutorService.submit(new CallableOcr(imgUri, context));
    }
}
