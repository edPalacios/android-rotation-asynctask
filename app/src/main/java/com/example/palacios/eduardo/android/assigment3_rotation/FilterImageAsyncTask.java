package com.example.palacios.eduardo.android.assigment3_rotation;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by Eduardo on 22/04/2015.
 */
public class FilterImageAsyncTask extends AsyncTask<Uri, Integer, Bitmap> {


    private int calculatedProgress = 0;
    private Activity activity;

    public FilterImageAsyncTask(Activity activity) {
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
        Log.d("FilterImageAsyncTask", "onPreExecute");
        if (activity != null) {
            ((MainActivity)activity).progressBarStartDownloading();
        }

    }

    @Override
    protected Bitmap doInBackground(Uri... url) {
        Log.d("FilterImageAsyncTask", "doInBackground");
        Bitmap grayBitmap = null;
        Uri toGrayUrl = null;
        try {
            toGrayUrl = Utils.downloadImage(activity.getApplicationContext(), url[0]);
            Utils.grayScaleFilter(activity.getApplicationContext(), toGrayUrl);
            Log.d("FilterImageAsyncTask", "filter to scale image ");
            grayBitmap = Utils.grayImageBitmap;
            setPublishProgress();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return grayBitmap;
    }

    private void setPublishProgress() {
        for (int i = 0; i < Utils.lengthFile; i++) {
            publishProgress(i);
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (activity == null) {
            Log.d("DownloadImageAsyncTask", "onProgressUpdate error --> activity = null");
        } else {
            calculatedProgress = (int) (((double) values[0]) / Utils.lengthFile * 100);
            ((MainActivity)activity).updateProgressBar(calculatedProgress);
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        ((MainActivity)activity).setBitmap(bitmap);
        ((MainActivity)activity).progressBarFinishAndHide();

    }
}
