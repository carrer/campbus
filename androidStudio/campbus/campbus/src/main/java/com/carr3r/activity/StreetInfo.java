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

    protected ListView listLinhas;
    private float x1,x2;
    static final int MIN_DISTANCE = 150;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.street_info);

        ((TextView) findViewById(R.id.street_info_street_name)).setText(getIntent().getExtras().getString("STREET"));

        LineItemAdapter adapter = new LineItemAdapter(getApplicationContext(), new ArrayList<LineItem>());
        listLinhas = (ListView) findViewById(R.id.street_info_list_lines);

        listLinhas.setAdapter(adapter);

        listLinhas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the TextView text or do whatever you need.
                //Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
                String lineNumber = ((TextView) view.findViewById(R.id.line_row_line_number)).getText().toString();
                Log.d("carr3r","sending lineNumber="+lineNumber);
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

            JSONObject jsonLinhas;
            String[] letreiros = new String[2];
            String linha;

            try {
                jsonLinhas = new JSONObject(Utils.loadJSONFromAsset(this, lineNumber + ".json"));
                linha = jsonLinhas.getString("linha");
                linha = linha.substring(linha.lastIndexOf("-")+1).trim();
                letreiros[0] = jsonLinhas.getJSONObject("ida").getString("letreiro");
                letreiros[1] = jsonLinhas.getJSONObject("volta").getString("letreiro");

            } catch (JSONException e) {
                jsonLinhas = new JSONObject();
                linha = "Unknown";
                letreiros[0] = letreiros[1] = "Unknown";
            }

            adapter.add(new LineItem(lineNumber, linha, letreiros));
        }

    }

    public void onBackPressed() {
        super.finish();
        overridePendingTransition  (R.anim.slide_stay, R.anim.right_slide_out);
    }


    class LoadStreetInfo extends AsyncTask<String, Void, List<StreetItem>> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
//            dialog = ProgressDialog.show(SearchByStreet.this, "Aguarde",
//                    "Procurando registros...");

        }

        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected List<StreetItem> doInBackground(String... params)
        {
            String criterio = params[0];

            List<StreetItem> out = new ArrayList<StreetItem>();

/*
            Iterator it = jsonParadas.keys();
            streetLines.clear();
            while(it.hasNext())
            {
                String key = (String) it.next();

                try {
                    JSONArray item = jsonParadas.getJSONArray(key);
                    streetLines.put(key, item.toString());
                    //Log.i("carr3r",item.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                boolean encontrado = false;

                for(int i=0; i<termos.length && !encontrado;i++)
                    if (termos[i].length()>=3 && key.contains(termos[i]))
                        encontrado=true;

                if (encontrado)
                    out.add(key);
            }

*/
            return out;
        }



        @Override
        protected void onPostExecute(List<StreetItem> result) {
            super.onPostExecute(result);

            StreetItemAdapter adapter = new StreetItemAdapter(getApplicationContext(), new ArrayList<StreetItem>());
            listLinhas.setAdapter(adapter);

            Iterator it = result.iterator();
            while(it.hasNext())
            {
                String streetName = (String) it.next();
                adapter.add(new StreetItem(streetName));
            }

            dialog.dismiss();
        }

    }

}
