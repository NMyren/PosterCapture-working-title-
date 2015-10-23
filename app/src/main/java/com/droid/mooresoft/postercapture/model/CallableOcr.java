package com.droid.mooresoft.postercapture.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.InputStream;
import java.util.concurrent.Callable;

/**
 * Created by Ed on 10/22/15.
 */
public class CallableOcr implements Callable<String> {

    Uri mImgUri;

    Context mContext;

    CallableOcr(Uri imgUri, Context context) {
        mImgUri = imgUri;
        mContext = context;
    }

    @Override
    public String call() throws Exception {
        InputStream is = mContext.getContentResolver().openInputStream(mImgUri);
        Bitmap bmp = BitmapFactory.decodeStream(is);
        TessBaseAPI tess = new TessBaseAPI();
        tess.init(mContext.getFilesDir().getPath(), "eng");
        tess.setImage(bmp);
        String result = tess.getUTF8Text();
        tess.end();
        return result;
    }
}
