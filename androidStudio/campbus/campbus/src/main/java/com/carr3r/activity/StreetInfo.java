package com.carr3r.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.carr3r.LineItem;
import com.carr3r.LineItemAdapter;

import com.carr3r.R;
import com.carr3r.StreetItem;
import com.carr3r.StreetItemAdapter;
import com.carr3r.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by carrer on 10/23/14.
 */
public class StreetInfo extends Activity  {

    protected ListView listLines;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.street_info);

        ((TextView) findViewById(R.id.street_info_street_name)).setText(getIntent().getExtras().getString("STREET"));

        LineItemAdapter adapter = new LineItemAdapter(getApplicationContext(), new ArrayList<LineItem>());
        listLines = (ListView) findViewById(R.id.street_info_list_lines);

        listLines.setAdapter(adapter);

        listLines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the TextView text or do whatever you need.
                //Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
                String lineNumber = ((TextView) view.findViewById(R.id.line_row_line_number)).getText().toString();
                Intent lineInfo = new Intent(StreetInfo.this, LineInfo.class);
                lineInfo.putExtra("NUMBER",lineNumber);
                startActivity(lineInfo);
                overridePendingTransition(R.anim.right_slide_in, R.anim.slide_stay);

            }
        });

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

            JSONObject obj;
            String[] boards = new String[2];
            String line;

            try {
                obj= new JSONObject(Utils.loadJSONFromAsset(this, lineNumber + ".json"));
                line = obj.getString("linha");
                line = line.substring(line.lastIndexOf("-")+1).trim();
                boards[0] = obj.getJSONObject("ida").getString("letreiro");
                boards[1] = obj.getJSONObject("volta").getString("letreiro");

            } catch (JSONException e) {
                line = "Unknown";
                boards[0] = boards[1] = "Unknown";
            }

            adapter.add(new LineItem(lineNumber, line, boards));
        }

    }

    public void onBackPressed() {
        super.finish();
        overridePendingTransition  (R.anim.slide_stay, R.anim.right_slide_out);
    }


}
