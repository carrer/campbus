package com.carr3r.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carr3r.Definitions;
import com.carr3r.R;
import com.carr3r.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by carrer on 10/23/14.
 * Activity que exibe as informações da linha como letreiros, itinerários, quantidade de ônibus circulando,
 * horários, etc. Recebe a numeração por parâmetro NUMBER
 */
public class LineInfo extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
            Remove a barra de título e expanse o activity em fullscreen
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.line_info);

        JSONObject jsonLine;

        String lineNumber = getIntent().getExtras().getString("NUMBER");
        try {
            jsonLine = new JSONObject(Utils.loadJSONFromAsset(this, lineNumber + ".json"));


            // IDA

            JSONObject jsonWay = jsonLine.getJSONObject("ida");
            JSONObject jsonTimetable = jsonWay.getJSONObject("horarios");

            ((TextView) findViewById(R.id.line_info_line_name)).setText(jsonLine.getString("linha").substring(jsonLine.getString("linha").lastIndexOf("-")+1).trim());
            ((TextView) findViewById(R.id.line_info_line_number)).setText(jsonLine.getString("numero"));
            ((TextView) findViewById(R.id.line_info_board1)).setText(jsonWay.getString("letreiro"));

            // dias uteis
            JSONArray timetable = jsonTimetable.getJSONArray("util");
            String timeList = "";
            int vehicles = jsonWay.getJSONObject("veiculos").getInt("util");
            String formatedVehicles = String.valueOf(vehicles)+" ";
            formatedVehicles += vehicles > 1 ? "veículos" : "veículo";

            for(int i=0;i<timetable.length();i++)
                timeList += timeList.length() > 0 ? ", "+timetable.getString(i) : timetable.getString(i);

            ((TextView) findViewById(R.id.line_info_weekday1)).setText("Dias úteis ("+formatedVehicles+"): "+timeList);

            // sábados
            timetable = jsonTimetable.getJSONArray("sabado");
            timeList = "";
            vehicles = jsonWay.getJSONObject("veiculos").getInt("sabado");
            formatedVehicles = String.valueOf(vehicles)+" ";
            formatedVehicles += vehicles > 1 ? "veículos" : "veículo";

            for(int i=0;i<timetable.length();i++)
                timeList += timeList.length() > 0 ? ", "+timetable.getString(i) : timetable.getString(i);

            ((TextView) findViewById(R.id.line_info_saturday1)).setText("Sábados ("+formatedVehicles+"): "+timeList);


            // domingos
            timetable = jsonTimetable.getJSONArray("domingo");
            timeList = "";
            vehicles = jsonWay.getJSONObject("veiculos").getInt("domingo");
            formatedVehicles = String.valueOf(vehicles)+" ";
            formatedVehicles += vehicles > 1 ? "veículos" : "veículo";

            for(int i=0;i<timetable.length();i++)
                timeList += timeList.length() > 0 ? ", "+timetable.getString(i) : timetable.getString(i);

            ((TextView) findViewById(R.id.line_info_sunday1)).setText("Domingos e feriados ("+formatedVehicles+"): "+timeList);

            JSONArray stops = jsonWay.getJSONArray("itinerario");

            LinearLayout stopsList = (LinearLayout) findViewById(R.id.line_info_way1);

            for(int i=0; i<stops.length();i++)
            {
                LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v =  inflater.inflate(R.layout.bus_stop_row, null);
                TextView nameView = (TextView) v.findViewById(R.id.bus_stop_row_name);
                nameView.setText(stops.getString(i));
                if (i%2==0)
                    v.setBackgroundColor(Color.rgb(230,230,230));
                stopsList.addView(v);
            }



            // VOLTA
            jsonWay = jsonLine.getJSONObject("volta");
            jsonTimetable = jsonWay.getJSONObject("horarios");

            ((TextView) findViewById(R.id.line_row_board2)).setText(jsonWay.getString("letreiro"));

            // dias uteis
            timetable = jsonTimetable.getJSONArray("util");
            timeList = "";
            vehicles = jsonWay.getJSONObject("veiculos").getInt("util");
            formatedVehicles = String.valueOf(vehicles)+" ";
            formatedVehicles += vehicles > 1 ? "veículos" : "veículo";

            for(int i=0;i<timetable.length();i++)
                timeList += timeList.length() > 0 ? ", "+timetable.getString(i) : timetable.getString(i);

            ((TextView) findViewById(R.id.line_info_weekday2)).setText("Dias úteis ("+formatedVehicles+"): "+timeList);

            // sábados
            timetable = jsonTimetable.getJSONArray("sabado");
            timeList = "";
            vehicles = jsonWay.getJSONObject("veiculos").getInt("sabado");
            formatedVehicles = String.valueOf(vehicles)+" ";
            formatedVehicles += vehicles > 1 ? "veículos" : "veículo";

            for(int i=0;i<timetable.length();i++)
                timeList += timeList.length() > 0 ? ", "+timetable.getString(i) : timetable.getString(i);

            ((TextView) findViewById(R.id.line_info_saturday2)).setText("Sábados ("+formatedVehicles+"): "+timeList);


            // domingos
            timetable = jsonTimetable.getJSONArray("domingo");
            timeList = "";
            vehicles = jsonWay.getJSONObject("veiculos").getInt("domingo");
            formatedVehicles = String.valueOf(vehicles)+" ";
            formatedVehicles += vehicles > 1 ? "veículos" : "veículo";

            for(int i=0;i<timetable.length();i++)
                timeList += timeList.length() > 0 ? ", "+timetable.getString(i) : timetable.getString(i);

            ((TextView) findViewById(R.id.line_info_sunday2)).setText("Domingos e feriados ("+formatedVehicles+"): "+timeList);

            stops = jsonWay.getJSONArray("itinerario");

            stopsList = (LinearLayout) findViewById(R.id.line_info_way2);

            for(int i=0; i<stops.length();i++)
            {
                LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v =  inflater.inflate(R.layout.bus_stop_row, null);
                TextView nameView = (TextView) v.findViewById(R.id.bus_stop_row_name);
                nameView.setText(stops.getString(i));
                if (i%2==0)
                    v.setBackgroundColor(Color.rgb(230,230,230));
                stopsList.addView(v);
            }

        } catch (JSONException e) {
        }

        // advertising pra pagar minha cerveja ;)
        AdView adView = (AdView) this.findViewById(R.id.line_info_adview);
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
