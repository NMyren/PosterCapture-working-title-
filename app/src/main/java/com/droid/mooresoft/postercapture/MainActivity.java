package com.droid.mooresoft.postercapture;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * Created by Ed on 10/12/15.
 */
public class MainActivity extends Activity {

    public static final int IMAGE_CAPTURE_REQUEST_CODE = 1;

    private Uri mImageUri;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }

    public void imageCapture(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            File outFile = getOutputFile();
            mImageUri = Uri.fromFile(outFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent chooser = Intent.createChooser(intent, "Take a photo or choose an image");
        Intent pickImage = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickImage});
        startActivityForResult(chooser, IMAGE_CAPTURE_REQUEST_CODE);
    }

    private File getOutputFile() throws IOException {
        String fileName = getResources().getString(R.string.app_name) + "_" +
                new SimpleDateFormat("hhmmssMMddyy").format(System.currentTimeMillis()) + ".png";
        File imageFile = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), fileName);
        return imageFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMAGE_CAPTURE_REQUEST_CODE) {
            if (data != null) {
                try {
                    ImageView iv = (ImageView) findViewById(R.id.image);
                    Bitmap bmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                    iv.setImageBitmap(bmp);
                    doMagic(bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else onImageCaptured(resultCode, data);
        }
    }

    private void onImageCaptured(int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Image Captured Successfully!", Toast.LENGTH_SHORT).show();
            if (mImageUri != null) showImage();
        } else {
            Toast.makeText(this, "FAILURE :(", Toast.LENGTH_SHORT).show();
        }
    }

    private void showImage() {
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageUri);
            ImageView imgView = (ImageView) findViewById(R.id.image);
            imgView.setImageBitmap(bmp);
            doMagic(bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doMagic(final Bitmap bmp) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                Log.d(getClass().toString(), "beginning OCR...");
                TessBaseAPI baseApi = new TessBaseAPI();
                baseApi.init(getExternalFilesDir(null).toString(), "eng");
                baseApi.setImage(bmp);
                String result = baseApi.getUTF8Text();
                baseApi.end();
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                Log.d(getClass().toString(), "captured text: " + result);
                TextView tv = (TextView) findViewById(R.id.magic_text);
                tv.setText(result);
            }
        }.execute();
    }
}
