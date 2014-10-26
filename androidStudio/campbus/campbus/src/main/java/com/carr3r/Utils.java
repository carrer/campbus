package com.carr3r;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by carrer on 10/24/14.
 */
public class Utils {

    public static String loadJSONFromAsset(Context context, String file) {
        String json = null;
        try {

            InputStream is = context.getAssets().open("db/"+file);

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
