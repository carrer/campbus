package com.carr3r;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class campbus extends Activity {


    protected EditText textProcura;
    protected LinearLayout listParadas;
    protected JSONObject jsonParadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            jsonParadas =  new JSONObject(loadJSONFromAsset());
        } catch (JSONException e) {
            jsonParadas = null;
        }

        setContentView(R.layout.activity_initial);

        textProcura = (EditText) findViewById(R.id.textProcura);
        listParadas = (LinearLayout) findViewById(R.id.listParadas);

        textProcura.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){

                if (s.toString().trim().length()>=3)
                {
                    new SearchParada()
                            .execute(s.toString().trim());
                }
            }
        });
    }

    class SearchParada extends AsyncTask<String, Void, List<String>> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dialog = ProgressDialog.show(campbus.this, "Aguarde",
                    "Baixando JSON, Por Favor Aguarde...");
            listParadas.removeAllViews();
        }

        @Override
        protected List<String> doInBackground(String... params)
        {
            String criterio = params[0];
            criterio = Normalizer.normalize(criterio, Normalizer.Form.NFD);
            criterio = criterio.replaceAll("[^\\p{ASCII}]", "").toUpperCase();

            Log.i("carr3r", criterio);
            String[] termos = criterio.split("\\s+");

            List<String> out = new ArrayList<String>();

            Iterator it = jsonParadas.keys();
            while(it.hasNext())
            {
                String key = (String) it.next();
/*
                try {
                    JSONArray item = jsonParadas.getJSONArray(key);
                    Log.i("carr3r",item.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
*/
                boolean encontrado = false;

                for(int i=0; i<termos.length && !encontrado;i++)
                    if (termos[i].length()>=3 && key.contains(termos[i]))
                        encontrado=true;

                if (encontrado)
                    out.add(key);
            }

            return out;
        }


        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);

            Iterator it = result.iterator();
            while(it.hasNext())
            {
                String name = (String) it.next();
                TextView item = new TextView(getApplicationContext());
                item.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
                item.setTextColor(Color.BLACK);
                item.setPadding(5,5,5,5);
                item.setText(name);
                listParadas.addView(item);
            }

            dialog.dismiss();
        }

    }

    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getAssets().open("db/paradas.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
}
