package com.droid.mooresoft.postercapture.model;

import android.os.AsyncTask;

import com.droid.mooresoft.postercapture.controller.NotificationFactory;

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by Ed on 10/24/15.
 */
public class AsyncPostOcrTask extends AsyncTask<Future<OcrResult>, Void, OcrResult> {

    @Override
    protected OcrResult doInBackground(Future<OcrResult>... arr) {
        Future<OcrResult> future = arr[0];
        OcrResult ocrResult = null;
        while (null == ocrResult) {
            try {
                ocrResult = future.get(5, TimeUnit.MINUTES);
            } catch (InterruptedException e1) {

            } catch (ExecutionException e2) {
                return null;
            } catch (TimeoutException e3) {
                return null;
            } catch (CancellationException e4) {
                return null;
            }
        }
        return ocrResult;
    }

    @Override
    protected void onPostExecute(OcrResult ocrResult) {
        NotificationFactory notificationFactory = NotificationFactory.getInstance();
        if (null == ocrResult) {
            notificationFactory.postFailedNotification(ocrResult.getContext());
        } else {
            notificationFactory.postSuccessNotification(ocrResult.getContext());
        }
    }
}
