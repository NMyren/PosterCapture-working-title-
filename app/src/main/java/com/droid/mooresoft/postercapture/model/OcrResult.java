package com.droid.mooresoft.postercapture.model;

import android.content.Context;
import android.net.Uri;

/**
 * Created by Ed on 10/23/15.
 */
public class OcrResult {

    private final Uri mImgUri;

    private final String mLang;

    private final Context mContext;

    private final String mResult;

    OcrResult(Uri imgUri, String lang, Context context, String result) {
        mImgUri = imgUri;
        mLang = lang;
        mContext = context;
        mResult = result;
    }

    /**
     * Gets the image {@link Uri} for this result.
     *
     * @return The {@link Uri} for the image that was OCR'd.
     */
    public Uri getImageuri() {
        return mImgUri;
    }

    /**
     * Gets the language used for this {@link OcrResult}.
     *
     * @return A Tesseract Tools recognizable language abbreviation.
     */
    public String getLanguage() {
        return mLang;
    }

    /**
     * Gets the application {@link Context} supplied for this OCR task.
     *
     * @return A {@link Context}.
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * Gets the OCR'd result.
     *
     * @return A {@link String} representing the UTF8 text recognized in the supplied image.
     */
    public String getResult() {
        return mResult;
    }
}
