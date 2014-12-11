package com.carr3r.campbus;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONObject;
import org.xeustechnologies.jtar.TarEntry;
import org.xeustechnologies.jtar.TarInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.GZIPInputStream;

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
        Carrega um arquivo JSON do armazenamento interno
     */
    public static String loadFile(Context context, String file) {
        try
        {
            FileInputStream inputStream = context.openFileInput(file);
            DataInputStream stream = new DataInputStream(inputStream);
            byte[] buffer = new byte[(int) inputStream.getChannel().size()];
            stream.readFully(buffer);
            stream.close();
            return new String(buffer);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /*
        Salva um arquivo JSON para o armazenamento interno
     */
    public static Boolean saveFile(Context context, String file, JSONObject json) {

        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(file, Context.MODE_PRIVATE);
            outputStream.write(json.toString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static Boolean saveFile(Context context, String file, String content) {

        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(file, Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    // apaga um arquivo do armazenamento interno
    public static Boolean deleteFile(Context context, String file)
    {
        return context.deleteFile(file);
    }

    public static String formatedDate()
    {
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
    }

    // baixa um arquivo
    public static byte[] downloadRemote(URL url)
    {
        if (url == null)
            return null;

        try
        {
            URLConnection conn = url.openConnection();
            int contentLength = conn.getContentLength();
            DataInputStream stream = new DataInputStream(url.openStream());
            byte[] buffer = new byte[contentLength];
            stream.readFully(buffer);
            stream.close();
            return buffer;
        }
        catch(Exception e)
        {
        }
        return null;
    }

    public static boolean fileExistance(Context context, String fname){
        File file = context.getFileStreamPath(fname);
        return file.exists();
    }

    // verifica se o dispositivo tem conectividade
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null ? cm.getActiveNetworkInfo().isConnected() : false;
    }

    // gunzip
    public static byte[] decompress(byte[] compressed) throws IOException {
        final int BUFFER_SIZE = 32;
        ByteArrayInputStream is = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte[] data = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = gis.read(data)) != -1) {
            outputStream.write(data, 0, bytesRead);
        }
        gis.close();
        is.close();
        return outputStream.toByteArray();
    }


    public static Boolean unTar(Context context, byte[] stream)
    {
        try
        {
            TarInputStream tis = new TarInputStream(new ByteArrayInputStream(stream));
            TarEntry entry;
            while((entry = tis.getNextEntry()) != null) {


                int count;
                byte data[] = new byte[2048];

                FileOutputStream fos = context.openFileOutput(entry.getName(), Context.MODE_PRIVATE);
                BufferedOutputStream dest = new BufferedOutputStream(fos);

                while((count = tis.read(data)) != -1) {
                    dest.write(data, 0, count);
                }

                dest.flush();
                dest.close();
            }

            tis.close();
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }


    public static void startAd(View banner)
    {
        AdView adView = (AdView) banner;
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


}
