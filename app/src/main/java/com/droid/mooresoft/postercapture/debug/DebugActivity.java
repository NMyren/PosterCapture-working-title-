package com.droid.mooresoft.postercapture.debug;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.droid.mooresoft.postercapture.R;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

/**
 * Created by Ed on 10/20/15.
 */
public class DebugActivity extends Activity {

    public static final int REQUEST_CHOOSE_IMAGE = 0,
            REQUEST_TAKE_PICTURE = 1;

    private Menu mMenu;
    private Uri mCurrImgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_activity);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            DebugOcrFrag frag = null;
            switch (requestCode) {
                case REQUEST_CHOOSE_IMAGE:
                    frag = DebugOcrFrag.newInstance(data.getData());
                    break;
                case REQUEST_TAKE_PICTURE:
                    frag = DebugOcrFrag.newInstance(mCurrImgUri);
                    break;
            }
            getFragmentManager().beginTransaction().replace(R.id.frag_container, frag).commit();
            mMenu.findItem(R.id.action_settings).setVisible(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.debug_menu, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.action_choose_image:
                intent.setAction(Intent.ACTION_GET_CONTENT)
                        .setType("image/*");
                startActivityForResult(intent, REQUEST_CHOOSE_IMAGE);
                return true;
            case R.id.action_take_picture:
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                String timestamp = new SimpleDateFormat("hhmmssMMddyy").format(System.currentTimeMillis());
                try {
                    mCurrImgUri = Uri.fromFile(File.createTempFile(timestamp, ".png", getExternalFilesDir(null)));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrImgUri);
                    startActivityForResult(intent, REQUEST_TAKE_PICTURE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;
            case R.id.action_settings:
                getFragmentManager().beginTransaction().replace(R.id.frag_container, DebugPrefsFrag.newInstance()).commit();
                mMenu.findItem(R.id.action_settings).setVisible(false);
                return true;
        }
        return false;
    }


    public static class DebugOcrFrag extends Fragment implements View.OnClickListener {

        private Uri mUri;
        private ProgressBar mProgBar;

        private final Handler mHandler = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mProgBar.setVisibility(View.VISIBLE);
                        mProgBar.setMax(100);
                        mProgBar.setProgress(msg.what);
                    }
                });
            }
        };

        public static DebugOcrFrag newInstance(Uri uri) {
            DebugOcrFrag frag = new DebugOcrFrag();
            Bundle args = new Bundle();
            args.putString("uri", uri.toString());
            frag.setArguments(args);
            return frag;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mUri = Uri.parse(getArguments().getString("uri"));
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.debug_ocr_frag, container, false);
            ImageView iv = (ImageView) view.findViewById(R.id.debug_thumbnail);
            iv.setImageURI(mUri);
            iv.setOnClickListener(this);
            mProgBar = (ProgressBar) view.findViewById(R.id.debug_ocr_progress);
            return view;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.debug_thumbnail) {
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setDataAndType(mUri, "image/*");
                startActivity(intent);
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            try {
                final Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mUri);
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        TessBaseAPI tessBaseAPI = new TessBaseAPI(new TessBaseAPI.ProgressNotifier() {
                            @Override
                            public void onProgressValues(TessBaseAPI.ProgressValues progressValues) {
                                Message msg = Message.obtain(mHandler, progressValues.getPercent());
                                msg.sendToTarget();
                            }
                        });
                        String lang = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("lang", "eng");
                        tessBaseAPI.init(getActivity().getFilesDir().toString(), lang);
                        tessBaseAPI.setImage(bmp);
                        String result = tessBaseAPI.getUTF8Text();
                        tessBaseAPI.end();
                        return result;
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        TextView tv = (TextView) getView().findViewById(R.id.debug_ocr_text);
                        tv.setText(result);
                        mProgBar.setVisibility(View.GONE);
                    }
                }.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static class DebugPrefsFrag extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        public static final HashMap<String, Integer> LANG_ASSET_MAP;

        static {
            LANG_ASSET_MAP = new HashMap<>();
            LANG_ASSET_MAP.put("eng", R.raw.eng_traineddata_gz);
        }

        public static DebugPrefsFrag newInstance() {
            return new DebugPrefsFrag();
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.debug_prefs);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {
            if (key.equals("lang")) {
                String newLang = sharedPrefs.getString(key, "eng");
                File dir = new File(getActivity().getFilesDir(), "tessdata");
                if (!dir.isDirectory()) {
                    if (!dir.mkdir()) return;
                }
                File langData = new File(dir, newLang + ".traineddata");
                if (!langData.exists()) {
                    try {
                        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(langData));
                        GZIPInputStream gzin = new GZIPInputStream(new BufferedInputStream(getResources().openRawResource(LANG_ASSET_MAP.get(newLang))));
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
            }
        }

        @Override
        public void onStart() {
            super.onStart();
            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }
    }
}
