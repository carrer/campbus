package com.carr3r.campbus;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by carrer on 10/24/14.
 * Classe com funções auxiliares;
 */
public class Utils {

    public static int dayOfWeek()
    {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
    }

    /*
        Carrega um arquivo JSON da pasta de assets
     */
    public static String loadJSONFromAsset(Context context, String file) {
        String json;
        try {

            InputStream is = context.getAssets().open("db/"+file);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            json="";
        }
        return json;

    }
}
