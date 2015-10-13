package com.droid.mooresoft.postercapture;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

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
        startActivityForResult(intent, IMAGE_CAPTURE_REQUEST_CODE);
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
            onImageCaptured(resultCode, data);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
