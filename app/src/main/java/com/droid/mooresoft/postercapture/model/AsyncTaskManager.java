package com.droid.mooresoft.postercapture.model;

import android.content.Context;
import android.net.Uri;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by Ed on 10/22/15.
 */
public class AsyncTaskManager {

    private static final AsyncTaskManager sInstance;

    private final ExecutorService mOcrExecutorService;

    static {
        sInstance = new AsyncTaskManager();
    }

    private AsyncTaskManager() {
        mOcrExecutorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Gets the instance of {@link AsyncTaskManager}.
     *
     * @return The global instance of {@link AsyncTaskManager}.
     */
    public static AsyncTaskManager getInstance() {
        return sInstance;
    }

    /**
     * Adds an OCR task to the queue to be executed.
     *
     * @param imgUri  An image {@link Uri}.
     * @param lang    A Tesseract Tools recognizable language abbreviation.
     * @param context The application {@link Context}.
     * @return A {@link Future} containing the result of the OCR.
     */
    public Future<OcrResult> addOcrTask(Uri imgUri, String lang, Context context) {
        return mOcrExecutorService.submit(new OcrCallable(imgUri, lang, context));
    }
}
