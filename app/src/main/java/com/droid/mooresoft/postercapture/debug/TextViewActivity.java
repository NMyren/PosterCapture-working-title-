package com.droid.mooresoft.postercapture.debug;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.droid.mooresoft.postercapture.R;

/**
 * Created by Ed on 10/23/15.
 */
public class TextViewActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_view_activity);
        String result = getIntent().getStringExtra("result");
        TextView tv = (TextView) findViewById(R.id.result_text);
        tv.setText(result);
    }
}
