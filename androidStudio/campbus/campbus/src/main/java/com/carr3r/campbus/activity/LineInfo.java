package com.carr3r.campbus.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.carr3r.campbus.Definitions;
import com.carr3r.campbus.R;
import com.carr3r.campbus.TimeItem;
import com.carr3r.campbus.TimeItemAdapter;
import com.carr3r.campbus.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by carrer on 10/23/14.
 * Activity que exibe as informações da linha como letreiros, itinerários, quantidade de ônibus circulando,
 * horários, etc. Recebe a numeração por parâmetro NUMBER
 */
public class LineInfo extends Activity {

    private JSONObject geo;

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
        if (lineNumber.length() == 3)
            lineNumber += "-0";

        int way = Integer.valueOf(getIntent().getExtras().getString("WAY"));
        try {
            jsonLine = new JSONObject(Utils.loadJSONFromAsset(this, lineNumber + ".json"));

            geo = jsonLine.getJSONObject("geo");

            JSONObject jsonWay;
            // IDA
            if (way == 1) // ida
                jsonWay = jsonLine.getJSONObject("ida");
            else
                jsonWay = jsonLine.getJSONObject("volta");

            JSONArray jsonTimetable = jsonWay.getJSONArray("horarios");
            JSONArray jsonVehicles = jsonWay.getJSONArray("veiculos");

            ((TextView) findViewById(R.id.line_info_line_name)).setText(jsonLine.getString("linha").substring(jsonLine.getString("linha").lastIndexOf("-") + 1).trim());
            ((TextView) findViewById(R.id.line_info_line_number)).setText(jsonLine.getString("numero"));
            ((TextView) findViewById(R.id.line_info_board)).setText(jsonWay.getString("letreiro"));

            Display display = getWindowManager().getDefaultDisplay();
            int oneRowHeight = 40;


            // dias uteis
            String[] timetable = jsonTimetable.getString(0).split(", ");
            int vehicles = jsonVehicles.getInt(0);

            if (vehicles <= 0 || timetable.length <= 0) // remove labels
            {
                ((TextView) findViewById(R.id.line_info_vehicles_weekday)).setVisibility(View.GONE);
                ((GridView) findViewById(R.id.line_info_weekday)).setVisibility(View.GONE);
                ((View) findViewById(R.id.line_info_weekday_divisor)).setVisibility(View.GONE);
            } else {


                String formatedVehicles = String.valueOf(vehicles) + " ";
                formatedVehicles += vehicles > 1 ? "veículos" : "veículo";

                ((TextView) findViewById(R.id.line_info_vehicles_weekday)).setText("Dias úteis: " + formatedVehicles);

                GridView grid = (GridView) findViewById(R.id.line_info_weekday);//).setText("Dias úteis (" + formatedVehicles + "): " + timeList);

                TimeItemAdapter adapter = new TimeItemAdapter(getApplicationContext(), new ArrayList<TimeItem>());
                adapter.setDayOfWeek(TimeItemAdapter.WEEKDAY);
                grid.setAdapter(adapter);
                TimeItemAdapter.resetCounter();

                for (int i = 0; i < timetable.length; i++) {
                    adapter.add(new TimeItem(timetable[i]));
                }

                ViewGroup.LayoutParams params = grid.getLayoutParams();

                int columns = (int) Math.floor(display.getWidth() / 100);
                int rows = (int) Math.ceil((double) ((double) adapter.getCount() / (double) columns));
                params.height = oneRowHeight * rows;
                grid.setLayoutParams(params);

            }


            // sábados

            timetable = jsonTimetable.getString(1).split(", ");

            vehicles = jsonVehicles.getInt(1);
            if (vehicles <= 0 || timetable.length <= 0) // remove labels
            {
                ((TextView) findViewById(R.id.line_info_vehicles_saturday)).setVisibility(View.GONE);
                ((GridView) findViewById(R.id.line_info_saturday)).setVisibility(View.GONE);
                ((View) findViewById(R.id.line_info_saturday_divisor)).setVisibility(View.GONE);

            } else {
                String formatedVehicles = String.valueOf(vehicles) + " ";
                formatedVehicles += vehicles > 1 ? "veículos" : "veículo";

                ((TextView) findViewById(R.id.line_info_vehicles_saturday)).setText("Sábados: " + formatedVehicles);

                GridView grid = (GridView) findViewById(R.id.line_info_saturday);

                TimeItemAdapter adapter = new TimeItemAdapter(getApplicationContext(), new ArrayList<TimeItem>());
                adapter.setDayOfWeek(TimeItemAdapter.SATURDAY);
                grid.setAdapter(adapter);
                TimeItemAdapter.resetCounter();

                for (int i = 0; i < timetable.length; i++) {
                    adapter.add(new TimeItem(timetable[i]));
                }

                ViewGroup.LayoutParams params = grid.getLayoutParams();

                int columns = (int) Math.floor(display.getWidth() / 100);
                int rows = (int) Math.ceil((double) ((double) adapter.getCount() / (double) columns));
                params.height = oneRowHeight * rows;
                grid.setLayoutParams(params);

            }


            // domingos

            timetable = jsonTimetable.getString(2).split(", ");

            vehicles = jsonVehicles.getInt(2);
            if (vehicles <= 0 || timetable.length <= 0) // remove labels
            {
                ((TextView) findViewById(R.id.line_info_vehicles_sunday)).setVisibility(View.GONE);
                ((GridView) findViewById(R.id.line_info_sunday)).setVisibility(View.GONE);
                ((View) findViewById(R.id.line_info_sunday_divisor)).setVisibility(View.GONE);
            } else {
                String formatedVehicles = String.valueOf(vehicles) + " ";
                formatedVehicles += vehicles > 1 ? "veículos" : "veículo";

                ((TextView) findViewById(R.id.line_info_vehicles_sunday)).setText("Domingos e feriados: " + formatedVehicles);

                GridView grid = (GridView) findViewById(R.id.line_info_sunday);

                TimeItemAdapter adapter = new TimeItemAdapter(getApplicationContext(), new ArrayList<TimeItem>());
                adapter.setDayOfWeek(TimeItemAdapter.SUNDAY);
                grid.setAdapter(adapter);
                TimeItemAdapter.resetCounter();

                for (int i = 0; i < timetable.length; i++) {
                    adapter.add(new TimeItem(timetable[i]));
                }

                ViewGroup.LayoutParams params = grid.getLayoutParams();

                int columns = (int) Math.floor(display.getWidth() / 100);
                int rows = (int) Math.ceil((double) ((double) adapter.getCount() / (double) columns));
                params.height = oneRowHeight * rows;
                grid.setLayoutParams(params);
            }

            // itinerario
            JSONArray jsonItinerario = jsonWay.getJSONArray("itinerario");

            LinearLayout list = (LinearLayout) findViewById(R.id.line_info_way);
            for (int i = 0; i < jsonItinerario.length(); i++) {
                LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View v = inflater.inflate(R.layout.bus_stop_row, null);
                TextView nameView = (TextView) v.findViewById(R.id.bus_stop_row_name);
                nameView.setText(jsonItinerario.getString(i));
                if (i % 2 == 0)
                    v.setBackgroundColor(Color.rgb(230, 230, 230));
                list.addView(v);
            }


        } catch (JSONException e) {
        }


        findViewById(R.id.line_info_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent map = new Intent(LineInfo.this, Map.class);
                map.putExtra("NUMBER", ((TextView) findViewById(R.id.line_info_line_number)).getText());
                map.putExtra("NAME", ((TextView) findViewById(R.id.line_info_line_name)).getText());
                map.putExtra("GEO", geo.toString());
                startActivity(map);
                overridePendingTransition(R.anim.right_slide_in, R.anim.slide_stay);
            }
        });

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
        overridePendingTransition(R.anim.slide_stay, R.anim.right_slide_out);
    }

}
