package com.example.palacios.eduardo.android.assigment3_rotation;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Eduardo on 21/04/2015.
 */
public class ImageRetainedFragment extends Fragment {

    public DownloadImageAsyncTask downloadImageAsyncTask;
    public FilterImageAsyncTask filterImageAsyncTask;
    private Activity activity;

    public ImageRetainedFragment() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        if (downloadImageAsyncTask != null) {
            downloadImageAsyncTask.onAttach(activity);
        }
        if (filterImageAsyncTask != null) {
                     filterImageAsyncTask.onAttach(activity);
                 }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (downloadImageAsyncTask != null) {
            downloadImageAsyncTask.onDetach();
        }
        if (filterImageAsyncTask != null) {
                     filterImageAsyncTask.onDetach();

                 }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void startBackgroundTask(Uri... url) {
        downloadImageAsyncTask = new DownloadImageAsyncTask(activity);
        downloadImageAsyncTask.execute(url);
    }

    public void startFilterBackgroundTask(Uri... url) {
             filterImageAsyncTask = new FilterImageAsyncTask(activity);
             filterImageAsyncTask.execute(url);
         }
}
