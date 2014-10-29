package com.carr3r.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
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

/**
 * Created by carrer on 10/23/14.
 * Activity que exibe todas as linhas que passam por uma localidade.
 * Esta activity deve receber como parâmetro o nome da localidade via "extras", mapeada com a string "STREET",
 * assim como as LINHAS que atendem a localidade em "LINES"
 */
public class StreetInfo extends Activity  {

    protected ListView listLines;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
            Remove a barra de título e expanse o activity em fullscreen
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.street_info);

        // Nome da rua
        ((TextView) findViewById(R.id.street_info_street_name)).setText(getIntent().getExtras().getString("STREET"));

        LineItemAdapter adapter = new LineItemAdapter(getApplicationContext(), new ArrayList<LineItem>());
        listLines = (ListView) findViewById(R.id.street_info_list_lines);

        listLines.setAdapter(adapter);

        /*
            Define o evento de onClick do ListView onde são listadas as linhas que atendem a localidade
         */
        listLines.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String lineNumber = ((TextView) view.findViewById(R.id.line_row_line_number)).getText().toString();
                Intent lineInfo = new Intent(StreetInfo.this, LineInfo.class);
                // passagem da numeração da linha por parâmetro
                lineInfo.putExtra("NUMBER",lineNumber);
                startActivity(lineInfo);
                // chama efeito de transição sliding
                overridePendingTransition(R.anim.right_slide_in, R.anim.slide_stay);

            }
        });

        /*
            Recebe a lista de linhas que atendem a localidade por parâmetro (uma vez que estas informações
            já foram carregadas na memória pela Activity anterior)
         */
        JSONArray lines;
        try {
            lines = new JSONArray( getIntent().getExtras().getString("LINES") );
        } catch (JSONException e) {
            lines = new JSONArray();
        }

        for(int i=0; i<lines.length();i++)
        {
            String lineNumber;
            JSONObject obj;
            String[] boards = new String[2];
            String line;

            try {
                lineNumber = lines.getString(i);
                obj= new JSONObject(Utils.loadJSONFromAsset(this, lineNumber + ".json"));
                line = obj.getString("linha");
                line = line.substring(line.lastIndexOf("-")+1).trim();
                boards[0] = obj.getJSONObject("ida").getString("letreiro");
                boards[1] = obj.getJSONObject("volta").getString("letreiro");
            } catch (JSONException e) {
                // Melhorar isso com Locale
                lineNumber = "???";
                line = "Desconhecido";
                boards[0] = boards[1] = "Desconhecido";
            }

            adapter.add(new LineItem(lineNumber, line, boards));
        }


        // advertising pra pagar minha cerveja ;)
        AdView adView = (AdView) this.findViewById(R.id.street_info_adview);
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


}
