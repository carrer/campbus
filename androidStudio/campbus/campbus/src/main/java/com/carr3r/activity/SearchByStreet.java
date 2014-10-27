package com.carr3r.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.carr3r.StreetItem;
import com.carr3r.R;
import com.carr3r.StreetItemAdapter;
import com.carr3r.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SearchByStreet extends Activity {


    protected EditText txtSearch;
    protected ListView listStreets;
    protected JSONObject jsonStreets;
    protected Map<String, String> streetLines = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        try {
            jsonStreets =  new JSONObject(Utils.loadJSONFromAsset(this, "paradas.json"));
        } catch (JSONException e) {
            jsonStreets = null;
        }

        setContentView(R.layout.search_by_street);

        txtSearch = (EditText) findViewById(R.id.search_by_street_street_name);
        listStreets = (ListView) findViewById(R.id.search_by_street_street_list);

        listStreets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the TextView text or do whatever you need.
                //Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
                String streetName = ((TextView) view.findViewById(R.id.street_row_street_name)).getText().toString();
                Intent streetInfo = new Intent(SearchByStreet.this, StreetInfo.class);
                streetInfo.putExtra("STREET", streetName);
                streetInfo.putExtra("LINES", streetLines.get(streetName));
                startActivity(streetInfo);
                Log.d("carr3r", streetName);
                Log.d("carr3r", streetLines.get(streetName));
                overridePendingTransition(R.anim.right_slide_in, R.anim.slide_stay);

            }
        });

        txtSearch.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            public void onTextChanged(CharSequence s, int start, int before, int count){

                if (s.toString().trim().length()>=3)
                {
                    new SearchParada()
                            .execute(s.toString().trim());


                }
            }
        });
    }

    public void onBackPressed() {
        super.finish();
        overridePendingTransition  (R.anim.slide_stay, R.anim.right_slide_out);
    }

    class SearchParada extends AsyncTask<String, Void, List<String>> {

        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dialog = ProgressDialog.show(SearchByStreet.this, "Aguarde",
                    "Procurando registros...");

        }

        @TargetApi(Build.VERSION_CODES.GINGERBREAD)
        @Override
        protected List<String> doInBackground(String... params)
        {
            String criteria = params[0];
            criteria = Normalizer.normalize(criteria, Normalizer.Form.NFD);
            criteria = criteria.replaceAll("[^\\p{ASCII}]", "").toUpperCase();

            Log.i("carr3r", criteria);
            String[] terms = criteria.split("\\s+");

            List<String> out = new ArrayList<String>();

            Iterator it = jsonStreets.keys();
            streetLines.clear();
            while(it.hasNext())
            {
                String key = (String) it.next();

                try {
                    JSONArray item = jsonStreets.getJSONArray(key);
                    streetLines.put(key, item.toString());
                    //Log.i("carr3r",item.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                boolean found= false;

                for(int i=0; i<terms.length && !found;i++)
                    if (terms[i].length()>=3 && key.contains(terms[i]))
                        found=true;

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
            while(it.hasNext())
            {
                String streetName = (String) it.next();
                adapter.add(new StreetItem(streetName));
            }

            dialog.dismiss();
        }

    }

}
