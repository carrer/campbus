package com.carr3r.campbus.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

import com.carr3r.campbus.Definitions;
import com.carr3r.campbus.R;
import com.carr3r.campbus.StreetItem;
import com.carr3r.campbus.StreetItemAdapter;
import com.carr3r.campbus.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by carrer on 10/23/14.
 * Activity que lista as linhas de ônibus através do seu itinerário. (ou, em outras palavras,
 * lista todas as linhas que atendem uma localidade)
 */
public class SearchByStreet extends Activity {


    protected EditText txtSearch;
    protected ListView listStreets;
    protected JSONObject jsonStreets;
    protected Map<String, String> streetLines = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
            Remove a barra de título e expanse o activity em fullscreen
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        try {
            jsonStreets = new JSONObject(Utils.loadJSONFromAsset(this, "paradas.json"));
        } catch (JSONException e) {
            jsonStreets = null;
        }

        setContentView(R.layout.search_by_street);

        txtSearch = (EditText) findViewById(R.id.search_by_street_street_name);
        listStreets = (ListView) findViewById(R.id.search_by_street_street_list);

        listStreets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String streetName = ((TextView) view.findViewById(R.id.street_row_street_name)).getText().toString();
                Intent streetInfo = new Intent(SearchByStreet.this, StreetInfo.class);
                streetInfo.putExtra("STREET", streetName);
                streetInfo.putExtra("LINES", streetLines.get(streetName));
                startActivity(streetInfo);
                overridePendingTransition(R.anim.right_slide_in, R.anim.slide_stay);

            }
        });

        txtSearch.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() >= 3) {
                    new SearchParada()
                            .execute(s.toString().trim());


                }
            }
        });

        // advertising pra pagar minha cerveja ;)
        AdView adView = (AdView) this.findViewById(R.id.search_by_street_adview);
        AdRequest adRequest;
        if (Definitions.DEBUG)
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
        overridePendingTransition(R.anim.slide_stay, R.anim.right_slide_out);
    }

    class SearchParada extends AsyncTask<String, Void, List<String>> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dialog = ProgressDialog.show(SearchByStreet.this, "Aguarde",
                    "Procurando registros...");

        }

        @Override
        protected List<String> doInBackground(String... params) {
            String criteria = params[0];
            criteria = Normalizer.normalize(criteria, Normalizer.Form.NFD);
            criteria = criteria.replaceAll("[^\\p{ASCII}]", "").toUpperCase();

            String[] terms = criteria.split("\\s+");

            List<String> out = new ArrayList<String>();

            Iterator it = jsonStreets.keys();
            streetLines.clear();
            while (it.hasNext()) {
                String key = (String) it.next();

                try {
                    JSONArray item = jsonStreets.getJSONArray(key);
                    streetLines.put(key, item.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                boolean found = false;

                for (int i = 0; i < terms.length && !found; i++)
                    if (terms[i].length() >= 3 && key.contains(terms[i]))
                        found = true;

                if (found)
                    out.add(key);
            }

            return out;
        }


        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);

            StreetItemAdapter adapter = new StreetItemAdapter(getApplicationContext(), new ArrayList<StreetItem>());
            listStreets.setAdapter(adapter);

            Iterator it = result.iterator();
            while (it.hasNext()) {
                String streetName = (String) it.next();
                adapter.add(new StreetItem(streetName));
            }

            dialog.dismiss();
        }

    }

}
