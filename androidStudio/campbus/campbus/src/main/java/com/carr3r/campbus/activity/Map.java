package com.carr3r.campbus.activity;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.carr3r.campbus.Definitions;
import com.carr3r.campbus.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by carrer on 10/23/14.
 * Activity principal. Simplesmente
 */
public class Map extends android.support.v4.app.FragmentActivity {

    private GoogleMap mMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
            Remove a barra de título e expanse o activity em fullscreen
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.map);

        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        ((TextView) findViewById(R.id.map_line_name)).setText(getIntent().getExtras().getString("NAME"));
        ((TextView) findViewById(R.id.map_line_number)).setText(getIntent().getExtras().getString("NUMBER"));

        JSONObject geo;
        try
        {
            geo = new JSONObject(getIntent().getExtras().getString("GEO"));

            String center[] = geo.get("center").toString().split(", ");
            double lat = Double.valueOf(center[0].trim());
            double lng = Double.valueOf(center[1].trim());
            int zoom = Integer.valueOf(geo.get("zoom").toString().trim());
            CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(lat, lng)).zoom(zoom).build();

            ArrayList<Location> locations = new ArrayList<Location>();

            JSONArray l2 = geo.getJSONArray("l2");
            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            for(int i=0; i<l2.length(); i++)
            {
                String c[] = l2.getString(i).split(",");
                double lt = Double.valueOf(c[0]);
                double ln = Double.valueOf(c[1]);
                options.add(new LatLng(lt, ln));
            }
            mMap.addPolyline(options);

            JSONArray pp = geo.getJSONArray("pp");
            for(int i=0; i<pp.length();i++)
            {
                String c[] = pp.getString(i).split(",");
                double lt = Double.valueOf(c[0]);
                double ln = Double.valueOf(c[1]);

                MarkerOptions marker = new MarkerOptions().position(new LatLng(lt, ln)).title("Parada");

                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.busstopmarker));

                mMap.addMarker(marker);

            }



            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        } catch (Exception e)
        {
            geo = null;
            e.printStackTrace();
        }

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
