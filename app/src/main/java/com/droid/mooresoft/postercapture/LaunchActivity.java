package com.droid.mooresoft.postercapture;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * Created by Ed on 10/20/15.
 */
public class LaunchActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean ok = true;
        File dir = new File(getExternalFilesDir(null), "tessdata");
        if (dir.isDirectory()) {
            // done
        } else {
            if (dir.mkdir()) {
                File langData = new File(dir, "eng.traineddata");
                try {
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(langData));
                    GZIPInputStream gzin = new GZIPInputStream(new BufferedInputStream(getResources().openRawResource(R.raw.eng_traineddata_gz)));
                    byte[] buf = new byte[1024];
                    int read;
                    while ((read = gzin.read(buf, 0, buf.length)) != -1) out.write(buf, 0, read);
                    out.close();
                    gzin.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    ok = false; // failed
                }
            } else {
                ok = false; // failed
            }
        }
        if (ok) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else finish();
    }


}
