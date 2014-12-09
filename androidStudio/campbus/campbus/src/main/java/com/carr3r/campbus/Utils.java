package com.carr3r.campbus;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
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
        Carrega um arquivo JSON da pasta de assets
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
            e.printStackTrace();
            return null;
        }
    }

    /*
        Carrega um arquivo JSON da pasta de assets
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

    public static Boolean deleteFile(Context context, String file)
    {
        return context.deleteFile(file);
    }

    public static String formatedDate()
    {
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date());
    }

    // CRC inspired on http://www.s-parker.uk/2014/08/crc8-arduino-and-php-implementation/
    public static byte crc8(String txt)
    {
        byte content[] = txt.getBytes();
        int crc = 0;
        for(int i=0;i<content.length;i++)
        {
            byte extract = content[i];
            for(int j=0;j<8;j++)
            {
                int sum = (crc ^ extract) & 0x01;
                crc >>= 1;
                if (sum>0)
                    crc ^= 0x8C;
                extract >>= 1;
            }
        }
        return (byte) crc;
    }

    public static byte[] downloadRemote(URL url)
    {
        if (url == null)
            return null;


        Log.d("carr3r","URL="+url.toString());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

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
            e.printStackTrace();
        }
        return null;
    }

    public static String decompress(byte[] compressed) throws IOException {
        final int BUFFER_SIZE = 32;
        ByteArrayInputStream is = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
        StringBuilder string = new StringBuilder();
        byte[] data = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = gis.read(data)) != -1) {
            string.append(new String(data, 0, bytesRead));
        }
        gis.close();
        is.close();
        return string.toString();
    }

}
