package com.carr3r.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.carr3r.LineItem;
import com.carr3r.LineItemAdapter;
import com.carr3r.R;
import com.carr3r.StreetItem;
import com.carr3r.StreetItemAdapter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by carrer on 10/23/14.
 */
public class StreetInfo extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.street_info);
        ((TextView) findViewById(R.id.textStreetName)).setText(getIntent().getExtras().getString("STREET"));

        LineItemAdapter adapter = new LineItemAdapter(getApplicationContext(), new ArrayList<LineItem>());
        ((ListView) findViewById(R.id.listLinhas)).setAdapter(adapter);

        JSONArray lines;
        try {
            lines = new JSONArray( getIntent().getExtras().getString("LINES") );
        } catch (JSONException e) {
            lines = new JSONArray();
        }

        for(int i=0; i<lines.length();i++)
        {
            String lineNumber;
            try {
                lineNumber = lines.getString(i);
            } catch (JSONException e) {
                lineNumber = "";
            }

            Log.i("carr3r-lineNumber", lineNumber);
            adapter.add(new LineItem(lineNumber, lineNumber, new String[2]));
        }

    }

    public void onBackPressed() {
        super.finish();
        overridePendingTransition  (R.anim.slide_stay, R.anim.right_slide_out);
    }
}
