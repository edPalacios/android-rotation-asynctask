package com.example.palacios.eduardo.android.assigment3_rotation;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private EditText userInput;
    private ImageView imageView;
    private ProgressBar progressBar;
    private ImageRetainedFragment downloadImageFragment;
    private Uri mDefaultUrl =
        Uri.parse("http://www.dre.vanderbilt.edu/~schmidt/robot.png");
    Uri urlUser;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userInput = (EditText) findViewById(R.id.user_input_url);
        imageView = (ImageView) findViewById(R.id.image_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        if (savedInstanceState == null) {
                         downloadImageFragment = new ImageRetainedFragment();
            getFragmentManager().beginTransaction().add(downloadImageFragment, "downloadImageFragment").commit();

        } else {
            downloadImageFragment =  (ImageRetainedFragment) getFragmentManager()
                    .findFragmentByTag("downloadImageFragment");
        }
        if (downloadImageFragment != null) {
            if ((downloadImageFragment.downloadImageAsyncTask != null //downloadImageAsyncTask check
                    && downloadImageFragment.downloadImageAsyncTask.getStatus()
                                == AsyncTask.Status.RUNNING)
               ||(downloadImageFragment.filterImageAsyncTask != null  //or filterImageAsyncTask check
                    && downloadImageFragment.filterImageAsyncTask.getStatus()
                                == AsyncTask.Status.RUNNING)) {
                                     progressBar.setVisibility(View.VISIBLE);

                                 }
        }

    }

    /**
     * Buttons set
     * @param view
     */
    public void downloadImage(View view) {
        try {
            hideKeyboard(this,
                    userInput.getWindowToken());
            urlUser = getUrl();
            downloadImageFragment.startBackgroundTask(urlUser);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scaleToGrayImage(View view) {
        try {
            hideKeyboard(this,
                    userInput.getWindowToken());
            urlUser = getUrl();
            downloadImageFragment.startFilterBackgroundTask(urlUser);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Get the URL to download based on user input.
     */
    protected Uri getUrl() {
        Uri url = Uri.parse(userInput.getText().toString());
        String uri = url.toString();
        if (uri == null || uri.equals(""))
            url = mDefaultUrl;
        if (URLUtil.isValidUrl(uri) || uri.isEmpty()) {
            return url;
        } else {
            Toast.makeText(this,
                    "Invalid URL ",
                    Toast.LENGTH_SHORT).show();
            return url;
        }
    }


    /**
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public void hideKeyboard(Activity activity,
                             IBinder windowToken) {
        InputMethodManager mgr =
                (InputMethodManager) activity.getSystemService
                        (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken,
                0);
    }

    /**
     * Set o methods to update progressbar with downloaded image
     * @param progress
     */
    public void updateProgressBar(int progress) {
        progressBar.setProgress(progress);

    }

    public void progressBarStartDownloading() {
        if ((downloadImageFragment.downloadImageAsyncTask != null)
        || (downloadImageFragment.filterImageAsyncTask != null)) {
            if (progressBar.getVisibility() != View.VISIBLE && progressBar.getProgress() != progressBar.getMax()) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    public void progressBarFinishAndHide() {
        if ((downloadImageFragment.downloadImageAsyncTask != null)
                || (downloadImageFragment.filterImageAsyncTask != null)) {
            if (progressBar.getVisibility() == View.VISIBLE) {
                progressBar.setVisibility(View.GONE);
                progressBar.setProgress(0);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setContentView(R.layout.activity_main);
            Log.d(TAG, "portrait");
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setContentView(R.layout.activity_main);
            Log.d(TAG, "landscape");
        }
    }

    public void setBitmap(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }
}
