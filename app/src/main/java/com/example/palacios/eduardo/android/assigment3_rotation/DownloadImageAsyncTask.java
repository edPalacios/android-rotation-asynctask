package com.example.palacios.eduardo.android.assigment3_rotation;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;


/**
 * Created by Eduardo on 14/04/2015.
 */
public class DownloadImageAsyncTask extends AsyncTask<Uri, Integer, Bitmap> {


    private int calculatedProgress = 0;
    private Activity activity;

    public DownloadImageAsyncTask( Activity activity) {
        onAttach(activity);
    }

    public void onAttach(Activity activity) {
        this.activity = activity;
    }

    public void onDetach() {
        activity = null;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("DownloadImageAsyncTask", "onPreExecute");
        if (activity != null) {
            ((MainActivity)activity).progressBarStartDownloading();
        }
    }

    @Override
    protected Bitmap doInBackground(Uri... url) {
        Log.d("DownloadImageAsyncTask", "doInBackground");
        Bitmap bitmap = null;
        try {
            Utils.downloadImage(activity.getApplicationContext(), url[0]);
            Log.d("DownloadImageAsyncTask", " downloading image ");
            bitmap = Utils.imageBitmap;
            setPublishProgress();
        }

         catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private void setPublishProgress() {
        for (int i = 0; i < Utils.lengthFile; i++) {
             publishProgress(i);
         }
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
//        Log.d("DownloadImageAsyncTask", "onProgressUpdate");
        if (activity == null) {
            Log.d("DownloadImageAsyncTask", "onProgressUpdate error --> activity = null");
        } else {
            calculatedProgress = (int) (((double) progress[0]) / Utils.lengthFile * 100);
            ((MainActivity)activity).updateProgressBar(calculatedProgress);
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        Log.d("DownloadImageAsyncTask", "onPostExecute");
        ((MainActivity)activity).setBitmap(bitmap);
        ((MainActivity)activity).progressBarFinishAndHide();

    }



}
