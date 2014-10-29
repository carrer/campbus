package com.carr3r.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.carr3r.Definitions;
import com.carr3r.LineItem;
import com.carr3r.LineItemAdapter;
import com.carr3r.R;
import com.carr3r.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by carrer on 10/23/14.
 * Activity que lista as linhas de ônibus pela numberação;
 */
public class SearchByLine extends Activity {


    protected EditText txtSearch;
    protected ListView listLines;
    protected JSONArray jsonLines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
            Remove a barra de título e expanse o activity em fullscreen
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        try {
            jsonLines =  new JSONArray(Utils.loadJSONFromAsset(this, "linhas.json"));
        } catch (JSONException e) {
            jsonLines = null;
        }

        setContentView(R.layout.search_by_line);

        txtSearch = (EditText) findViewById(R.id.search_by_line_line);
        txtSearch.setRawInputType(Configuration.KEYBOARD_12KEY);

        listLines = (ListView) findViewById(R.id.search_by_line_list);

        listLines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String lineNumber = ((TextView) view.findViewById(R.id.line_row_line_number)).getText().toString();
                Intent lineInfo = new Intent(SearchByLine.this, LineInfo.class);
                lineInfo.putExtra("NUMBER", lineNumber);
                startActivity(lineInfo);
                overridePendingTransition(R.anim.right_slide_in, R.anim.slide_stay);

            }
        });

        txtSearch.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}

            public void onTextChanged(CharSequence s, int start, int before, int count){

                if (s.toString().trim().length()>=2)
                {
                    new Search()
                            .execute(s.toString().trim());
                }
            }
        });


        // advertising pra pagar minha cerveja ;)
        AdView adView = (AdView) this.findViewById(R.id.search_by_line_adview);
        AdRequest adRequest;
        if ( Definitions.DEBUG )
            adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)// This is for emulators
                    .addTestDevice("2EAB96D84FE62876379A9C030AA6A0AC") // Nexus 5
                    .build();
        else
            adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    public void onBackPressed() {
        super.finish();
        overridePendingTransition  (R.anim.slide_stay, R.anim.right_slide_out);
    }

    class Search extends AsyncTask<String, Void, List<JSONObject>> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dialog = ProgressDialog.show(SearchByLine.this, "Aguarde",
                    "Procurando registros...");

        }

        @Override
        protected List<JSONObject> doInBackground(String... params)
        {
            String criteria = params[0];
            List<JSONObject> out = new ArrayList<JSONObject>();

            for(int i=0;i<jsonLines.length();i++)
            {
                JSONObject obj = null;
                try {
                    obj = (JSONObject) jsonLines.getJSONObject(i);
                    if (obj.getString("numero").contains(criteria)) {
                        out.add(obj);
                    }
                } catch (JSONException e) {
                }
            }

            return out;
        }


        @Override
        protected void onPostExecute(List<JSONObject> result) {
            super.onPostExecute(result);

            LineItemAdapter adapter = new LineItemAdapter(getApplicationContext(), new ArrayList<LineItem>());
            listLines.setAdapter(adapter);

            Iterator it = result.iterator();
            while(it.hasNext())
            {
                JSONObject obj = (JSONObject) it.next();

                String lineNumber;
                JSONArray jsonBoards;
                String[] boards = new String[2];
                String line;

                try {
                    lineNumber = obj.getString("numero");
                    line = obj.getString("linha");
                    line = line.substring(line.lastIndexOf("-")+1).trim();

                    jsonBoards = obj.getJSONArray("letreiros");
                    boards[0] = jsonBoards.get(0).toString();
                    boards[1] = jsonBoards.get(1).toString();

                } catch (JSONException e) {
                    lineNumber = "?";
                    line = "Desconhecido";
                    boards[0] = boards[1] = "Desconhecido";
                }

                adapter.add(new LineItem(lineNumber, line, boards));
            }


            dialog.dismiss();

        }

    }

}
