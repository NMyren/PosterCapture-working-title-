package com.droid.mooresoft.postercapture.debug;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.droid.mooresoft.postercapture.R;
import com.droid.mooresoft.postercapture.controller.TaskService;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * Created by Ed on 10/23/15.
 */
public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File dir = new File(getFilesDir(), "tessdata");
        if (!dir.isDirectory()) {
            if (!dir.mkdir()) {
                throw new RuntimeException();
            }
        }
        File langData = new File(dir, "eng.traineddata");
        if (!langData.exists()) {
            try {
                BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(langData));
                GZIPInputStream gzin = new GZIPInputStream(
                        new BufferedInputStream(getResources().openRawResource(R.raw.eng_traineddata_gz)));
                byte[] buf = new byte[1024];
                int read;
                while ((read = gzin.read(buf, 0, buf.length)) != -1)
                    out.write(buf, 0, read);
                out.close();
                gzin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT)
                .setType("image/*");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            Uri imgUri = data.getData();
            String lang = "eng";
            Intent intent = new Intent(this, TaskService.class)
                    .putExtra("uri", imgUri.toString())
                    .putExtra("lang", lang);
            startService(intent);
            finish();
        }
    }
}
