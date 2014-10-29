package com.carr3r.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.carr3r.Definitions;
import com.carr3r.R;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdRequest;

/**
 * Created by carrer on 10/23/14.
 * Activity principal. Simplesmente
 */
public class Main extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
            Remove a barra de título e expanse o activity em fullscreen
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);

        // advertising pra pagar minha cerveja ;)
        AdView adView = (AdView) this.findViewById(R.id.main_adview);
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


    public void go2SearchByStreet(View v) {
        Intent searchByStreet = new Intent(Main.this, SearchByStreet.class);
        startActivity(searchByStreet);
        overridePendingTransition(R.anim.right_slide_in, R.anim.slide_stay);
    }

    public void go2SearchByLine(View v) {
        Intent searchByLine = new Intent(Main.this, SearchByLine.class);
        startActivity(searchByLine);
        overridePendingTransition(R.anim.right_slide_in, R.anim.slide_stay);
    }

}
