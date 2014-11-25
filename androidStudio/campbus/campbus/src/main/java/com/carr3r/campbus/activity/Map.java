package com.carr3r.campbus.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import android.view.WindowManager;

import com.carr3r.campbus.Definitions;
import com.carr3r.campbus.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

/**
 * Created by carrer on 10/23/14.
 * Activity principal. Simplesmente
 */
public class Map extends FragmentActivity {

    MapView mapView;
    GoogleMap map;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
            Remove a barra de título e expanse o activity em fullscreen
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.map);


        // advertising pra pagar minha cerveja ;)
        AdView adView = (AdView) this.findViewById(R.id.map_adview);
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

}
