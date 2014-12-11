package com.carr3r.campbus.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.carr3r.campbus.R;
import com.carr3r.campbus.Utils;
import com.carr3r.campbus.WebIndex;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carrer on 10/23/14.
 * Activity principal. Ao inicializar o app, este activity verifica a consistência do banco de dados.
 */
public class Main extends Activity {

    // ProgressDialog para exibição das animações de download
    ProgressDialog mProgressDialog;

    // Garante a ininterrupção da thread mesmo que o app entre em segundo plano
    PowerManager.WakeLock mWakeLock;

    // Workaround para exibir Toasts por tempos maiores que 5s
    Toast toast;


    boolean hasDB = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
            Remove a barra de título e expanse o activity em fullscreen
         */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main);

        // advertising pra cerveja ;)
        Utils.startAd(findViewById(R.id.main_adview));

        if (!Utils.fileExistance(this, "checksum.json"))
        {
            hasDB = false;
            downloadDB();
        }
        else
            updateDB();

    }


    public void downloadDB()
    {
        if (!Utils.isOnline(this))
        {
            if (toast == null) {
                toast = Toast.makeText(this, getString(R.string.noDB), Toast.LENGTH_LONG);
                fireLongToast();
            }
        }
        else
        {
            mProgressDialog = new ProgressDialog(Main.this);
            mProgressDialog.setMessage(getString(R.string.downloading_first_time));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.setCancelable(false);

            final DownloadTask downTask = new DownloadTask(Main.this);
            downTask.execute(WebIndex.GZ_DIR+"db.tgz");
        }
    }

    public void updateDB()
    {
        boolean verify = true;
        try {
            JSONObject local = new JSONObject(Utils.loadFile(this, "checksum.json"));
            // já realizamos esta verificação hoje? Se sim, pula a verificação
            if (local.getString("u").substring(0,10).equals(Utils.formatedDate().substring(0, 10)))
                verify = false;
        } catch (JSONException e) {}

        if (verify && Utils.isOnline(this))
        {
            mProgressDialog = new ProgressDialog(Main.this);
            mProgressDialog.setMessage(getString(R.string.downloading_updates));
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);

            final VerifySynchronizationTask vsyncTask = new VerifySynchronizationTask(Main.this);
            vsyncTask.execute();
        }
    }


    public void go2SearchByStreet(View v) {
        if (!hasDB) {
            downloadDB();
            return;
        }

        Intent searchByStreet = new Intent(Main.this, SearchByStreet.class);
        startActivity(searchByStreet);
        overridePendingTransition(R.anim.right_slide_in, R.anim.slide_stay);
    }

    public void go2SearchByLine(View v) {

        if (!hasDB) {
            downloadDB();
            return;
        }

        Intent searchByLine = new Intent(Main.this, SearchByLine.class);
        startActivity(searchByLine);
        overridePendingTransition(R.anim.right_slide_in, R.anim.slide_stay);
    }

    public void go2Disclaimer(View v) {

        // remove o Toast da frente
        if (toast != null) {
            toast.cancel();
            toast = null;
        }

        Intent disclaimer = new Intent(Main.this, Disclaimer.class);
        startActivity(disclaimer);
        overridePendingTransition(R.anim.right_slide_in, R.anim.slide_stay);
    }

    // workaround para exibir toasts por mais tempo
    private void fireLongToast() {
        Thread t;
        if (toast != null) {
            t = new Thread() {
                public void run() {
                    int count = 0;
                    try {
                        while (count < 3 && toast != null) {
                            toast.show();
                            sleep(1850);
                            count++;
                        }
                        toast = null;
                    } catch (Exception e) {
                    }
                }
            };
            t.start();
        }
    }

    // verifica se foi lançado alguma atualização online
    private class VerifySynchronizationTask extends AsyncTask<Void, Integer, Boolean> {

        private Context context;

        public VerifySynchronizationTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            JSONObject local;
            try
            {
                local = new JSONObject(Utils.loadFile(context, "checksum.json"));
                // atualiza a data de última verificação
                local.put("u", Utils.formatedDate());
                Utils.saveFile(context, "checksum.json", local);
            } catch(JSONException e)
            {
                local = null;
            }

            URL url;
            try
            {
                url = new URL(WebIndex.CHECKSUM_INDEX);
            } catch(MalformedURLException e)
            {
                url = null;
            }

            String fileContents = new String(Utils.downloadRemote(url));
            if (fileContents != null)
            {
                try
                {
                    JSONObject remote = new JSONObject(fileContents);

                    // verificamos o checksum do arquivo com catálogo de todos os checksums dos arquivos
                    if (!remote.getString("c").equals(local.getString("c")))
                        return true;
                }
                catch(JSONException e)
                {
                    return false;
                }
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // impede que o processador entre em standby quando o usuário pressiona o botão de desligar

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
                if (Utils.isOnline(context))
                {
                    mProgressDialog = new ProgressDialog(Main.this);
                    mProgressDialog.setIndeterminate(true);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    mProgressDialog.setCancelable(false);

                    mProgressDialog.setMessage(getString(R.string.downloading_updates));
                    final SynchronizeTask syncTask = new SynchronizeTask(Main.this);
                    syncTask.execute();
                }
            }
            else
                mWakeLock.release();

        }
    }

    private class SynchronizeTask extends AsyncTask<Void, Integer, Boolean> {

        private Context context;

        public SynchronizeTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            JSONObject localContent;

            try
            {
                localContent = new JSONObject(Utils.loadFile(context, "checksum.json"));
            } catch(JSONException e)
            {
                localContent = null;
            }

            URL url;
            try
            {
                url = new URL(WebIndex.INDEX);
            } catch(MalformedURLException e)
            {
                url = null;
            }

            // baixa o catálogo de arquivos
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

                    // adiciona todos os arquivos locais numa lista que, ao final deste processo,
                    // conterá todos os arquivos fora da intersecção das duas listas (e que, portanto,
                    // podem ser apagados)
                    List<String> deleteList = new ArrayList<String>();
                    if (localContent!=null)
                    {
                        localFiles = localContent.getJSONArray("f");
                        for(int i=0;i<localFiles.length();i++)
                            deleteList.add((String) localFiles.getJSONObject(i).keys().next());
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
                                    // verifica o CRC de ambos os arquivos (remoto e local)
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
                    // a barra de progresso será mensuara através de tarefas (apagar os arquivos desnecessários,
                    // e descarregar aqueles que foram atualizados ou novos)
                    mProgressDialog.setMax(deleteList.size()+downloadList.size());
                    mProgressDialog.setProgress(0);

                    int p=0;
                    publishProgress(0);


                    for (int i=0;i<deleteList.size();i++) {
                        Utils.deleteFile(context, deleteList.get(i)+".json");
                        publishProgress(++p);
                    }

                    for (int i=0;i<downloadList.size();i++) {
                        String remoteContent = new String(Utils.decompress(Utils.downloadRemote(new URL(WebIndex.GZ_DIR + downloadList.get(i) + ".json.gz"))));
                        if (remoteContent!=null) {
                            Utils.saveFile(context, downloadList.get(i) + ".json", remoteContent);
                        }
                        publishProgress(++p);
                    }

                    Utils.saveFile(context, "checksum.json", fileContents);
                    return true;
                }
                catch(Exception e)
                {
                    return false;
                }
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // impede que o processador entre em standby quando o usuário pressiona o botão de desligar
            // se já existir um lock, não precisamos criar um
            if (mWakeLock==null)
            {
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        getClass().getName());
                mWakeLock.acquire();
            }

            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (mWakeLock != null)
                mWakeLock.release();

            mProgressDialog.dismiss();

            if (!result)
            {
                if (toast == null)
                {
                    toast = Toast.makeText(context, getString(R.string.error_during_update), Toast.LENGTH_LONG);
                    fireLongToast();
                }
            }
            else {
                Toast.makeText(context, getString(R.string.database_updated), Toast.LENGTH_SHORT).show();
            }

        }
    }


    private class DownloadTask extends AsyncTask<String, Integer, Boolean> {

        private Context context;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected Boolean doInBackground(String... sUrl) {
            final int BUFFER_SIZE = 32;

            try
            {
                // baixa apenas um arquivo de uma só vez
                URL url = new URL(sUrl[0]);

                URLConnection conn = url.openConnection();
                int contentLength = conn.getContentLength();
                int contentRead = 0;

                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(contentLength/1024);

                DataInputStream stream = new DataInputStream(url.openStream());
                ByteArrayOutputStream out = new ByteArrayOutputStream();

                byte[] buffer = new byte[BUFFER_SIZE];
                while(contentRead < contentLength)
                {
                    int buffSize = stream.read(buffer, 0, BUFFER_SIZE);
                    contentRead += buffSize;
                    publishProgress(contentRead);
                    out.write(buffer, 0, buffSize);
                }

                stream.close();

                return Utils.unTar(context, Utils.decompress(out.toByteArray() ));
            }
            catch(Exception e)
            {
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // impede que o processador entre em standby quando o usuário pressiona o botão de desligar
            // se já existir um lock, não precisamos criar um
            if (mWakeLock==null)
            {
                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        getClass().getName());
                mWakeLock.acquire();
            }
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            mProgressDialog.setProgress(progress[0]/1024);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (!result)
            {
                toast = Toast.makeText(context, getString(R.string.error_during_download), Toast.LENGTH_LONG);
                fireLongToast();
            }
            else
            {
                try
                {
                    JSONObject local = new JSONObject(Utils.loadFile(context, "checksum.json"));
                    // atualiza o catálogo local com data/hora da "atualização"
                    local.put("u", Utils.formatedDate());
                    Utils.saveFile(context, "checksum.json", local);
                } catch (Exception e)
                {

                }

                Toast.makeText(context, getString(R.string.database_updated), Toast.LENGTH_SHORT).show();
                hasDB=true;
            }
        }

    }

}
