package com.carr3r.campbus.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.carr3r.campbus.Definitions;
import com.carr3r.campbus.R;
import com.carr3r.campbus.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carrer on 10/23/14.
 * Activity principal. Simplesmente
 */
public class Main extends Activity {

    ProgressDialog mProgressDialog;
    public static final String HOMEPAGE = "http://www.carr3r.com/campbus/";
    public static final String CHECKSUM_INDEX = HOMEPAGE+"checksum.json";
    public static final String INDEX = HOMEPAGE+"index.json";
    public static final String GZ_DIR = HOMEPAGE+"gz/";

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
        if (Definitions.DEBUG)
            adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)// This is for emulators
                    .addTestDevice("2EAB96D84FE62876379A9C030AA6A0AC") // Nexus 5
                    .build();
        else
            adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

// instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(Main.this);
        mProgressDialog.setMessage("Verificando atualizações...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);

// execute this when the downloader must be fired
        VerifySynchronizationTask vsyncTask = new VerifySynchronizationTask(Main.this);
        vsyncTask.execute();

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

    public void go2Disclaimer(View v) {
        Intent disclaimer = new Intent(Main.this, Disclaimer.class);
        startActivity(disclaimer);
        overridePendingTransition(R.anim.right_slide_in, R.anim.slide_stay);
    }

    private class VerifySynchronizationTask extends AsyncTask<Void, Integer, Boolean> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public VerifySynchronizationTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            JSONObject local;
            try
            {
                local = new JSONObject(Utils.loadFile(getApplicationContext(), "checksum.json"));
            } catch(Exception e)
            {
                e.printStackTrace();
                local = null;
            }

            URL url;
            try
            {
                url = new URL(Main.CHECKSUM_INDEX);
            } catch(Exception e)
            {
                e.printStackTrace();
                url = null;
            }

            String fileContents = new String(Utils.downloadRemote(url));
            if (fileContents != null)
            {
                if (local == null)
                    return true;

                try
                {
                    JSONObject remote = new JSONObject(fileContents);
                    if (!remote.getString("c").equals(local.getString("c")))
                        return true;
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    return false;
                }
            }
            else
                Log.d("carr3r", "problem loading remote checksum file");

            return false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(Boolean result) {

            mProgressDialog.dismiss();

            if (result)
            {
                mProgressDialog = new ProgressDialog(Main.this);
                mProgressDialog.setMessage("Baixando atualizações");
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);

                final SynchronizeTask syncTask = new SynchronizeTask(Main.this);
                syncTask.execute();

            }
            else
                mWakeLock.release();

        }
    }

    private class SynchronizeTask extends AsyncTask<Void, Integer, Boolean> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public SynchronizeTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            JSONObject localContent;

            try
            {
                localContent = new JSONObject(Utils.loadFile(getApplicationContext(), "checksum.json"));
            } catch(Exception e)
            {
                localContent = null;
                Log.d("carr3r", "local file index doesn't exist");
            }

            URL url;
            try
            {
                url = new URL(Main.INDEX);
            } catch(Exception e)
            {
                url = null;
            }

            String fileContents = new String(Utils.downloadRemote(url));
            if (fileContents != null)
            {
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(false);

                try
                {
                    JSONObject remote = new JSONObject(fileContents);
                    JSONArray files = remote.getJSONArray("f");
                    JSONArray localFiles;

                    List<String> deleteList = new ArrayList<String>();
                    if (localContent!=null)
                    {
                        try {
                            localFiles = localContent.getJSONArray("f");
                            for(int i=0;i<localFiles.length();i++)
                                deleteList.add((String) localFiles.getJSONObject(i).keys().next());
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                            localFiles = null;
                        }
                    }
                    else
                        localFiles = null;

                    List<String> downloadList = new ArrayList<String>();
                    for(int i=0;i<files.length();i++)
                    {
                        boolean shouldDownload=true;
                        JSONObject file = files.getJSONObject(i);
                        String fileName = (String) file.keys().next();
                        String crc = file.getString(fileName);

                        if (localFiles != null)
                        {
                            for(int j=0;j<localFiles.length();j++)
                            {
                                JSONObject local = localFiles.getJSONObject(j);
                                String localFilename = (String) local.keys().next();
                                if (localFilename.equals(fileName))
                                {
                                    if (crc.equals(local.getString(localFilename)))
                                    {
                                        shouldDownload = false;
                                        deleteList.remove(fileName);
                                    }

                                    break;
                                }
                            }
                        }
                        if (shouldDownload)
                            downloadList.add(fileName);
                    }

                    mProgressDialog.setIndeterminate(false);
                    mProgressDialog.setMax(deleteList.size()+downloadList.size());
                    mProgressDialog.setProgress(0);

                    int p=0;
                    publishProgress(0);

                    for (int i=0;i<deleteList.size();i++) {
                        Log.d("carr3r", "deleting -> " + deleteList.get(i));
                        Utils.deleteFile(getApplicationContext(), deleteList.get(i)+".json");
                        publishProgress(++p);
                    }

                    for (int i=0;i<downloadList.size();i++) {

                        Log.d("carr3r", "downlading -> " + downloadList.get(i));

                        String remoteContent = Utils.decompress(Utils.downloadRemote(new URL(GZ_DIR + downloadList.get(i) + ".json.gz")));
                        if (remoteContent!=null)
                            Utils.saveFile(getApplicationContext(), downloadList.get(i)+".json", remoteContent);

                        publishProgress(++p);
                    }

                    Utils.saveFile(getApplicationContext(), "checksum.json", fileContents);
                    return true;
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    return false;
                }
            }
            else
                Log.d("carr3r", "problem loading remote checksum file");

            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            mProgressDialog.setProgress(progress[0]);
            // if we get here, length is known, now set indeterminate to false
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (mWakeLock != null)
                mWakeLock.release();

            mProgressDialog.dismiss();
            if (!result)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
        }
    }

}
