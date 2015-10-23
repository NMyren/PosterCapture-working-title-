package com.droid.mooresoft.postercapture.debug;

import android.app.Activity;
import android.os.Bundle;

import com.droid.mooresoft.postercapture.model.AsyncTaskManager;

/**
 * Created by Ed on 10/23/15.
 */
public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AsyncTaskManager.getInstance().addOcrTask(null, this);

        // Intent intent = new Intent(Intent.ACTION_GET_CONTENT)
        //        .setType("image/*");
        // startActivityForResult(intent, 0);
    }
}
