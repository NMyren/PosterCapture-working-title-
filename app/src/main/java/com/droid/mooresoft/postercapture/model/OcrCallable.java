package com.droid.mooresoft.postercapture.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.InputStream;
import java.util.concurrent.Callable;

/**
 * Created by Ed on 10/23/15.
 */
class OcrCallable implements Callable<OcrResult> {

    private final Uri mImgUri;

    private final String mLang;

    private final Context mContext;

    OcrCallable(Uri imgUri, String lang, Context context) {
        mImgUri = imgUri;
        mLang = lang;
        mContext = context;
    }

    /**
     * Loads a {@link Bitmap} from the supplied {@link Uri} and uses Tesseract Tools to recognize
     * UTF8 text within the image.
     *
     * @return An {@link OcrResult} containing the result of the image analysis.
     * @throws Exception When the supplied {@link Uri} does not represent a valid path to an image
     *                   or the task is cancelled.
     */
    @Override
    public OcrResult call() throws Exception {
        InputStream is = mContext.getContentResolver().openInputStream(mImgUri);
        Bitmap bmp = BitmapFactory.decodeStream(is);
        if (null == bmp) {
            throw new Exception();
        }
        TessBaseAPI tess = new TessBaseAPI();
        tess.init(mContext.getFilesDir().getPath(), mLang);
        tess.setImage(bmp);
        String result = tess.getUTF8Text();
        tess.end();
        return new OcrResult(mImgUri, mLang, mContext, result);
    }
}
