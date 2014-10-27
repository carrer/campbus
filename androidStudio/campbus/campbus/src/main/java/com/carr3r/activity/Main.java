package com.carr3r.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.carr3r.R;

/**
 * Created by carrer on 10/23/14.
 */
public class Main extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);
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
