package com.example.palacios.eduardo.android.assigment3_rotation;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

/**
 * This helper class encapsulates several static methods that are used
 * to download image files.
 */
public class Utils {
    /**
     * Used for debugging.
     */
    private final static String TAG = "Utils";
    
    /**
     * If you have access to a stable Internet connection for testing
     * purposes, feel free to change this variable to false so it
     * actually downloads the image from a remote server.
     */
    static final boolean DOWNLOAD_OFFLINE = false;
    
    /**
     * The resource that we write to the file system in offline
     * mode. 
     */
    static final int OFFLINE_TEST_IMAGE = R.raw.dougs;

    /**
     * The file name that we should use to store the image in offline
     * mode.
     */
    static final String OFFLINE_FILENAME = "dougs.jpg";
    
    /**
     * Apply a grayscale filter to the @a imageEntity and return it.
     */
    public static Uri grayScaleFilter(Context context,
                                      Uri pathToImageFile) {
        Log.d(TAG, "grayScaleFilter");
        Bitmap grayScaleImage = null;

        try (InputStream inputStream = new FileInputStream(pathToImageFile.toString())) {
            Bitmap originalImage =
                    BitmapFactory.decodeStream(inputStream);

            // Bail out of we get an invalid bitmap.
            if (originalImage == null)
                return null;

            grayScaleImage =
                    originalImage.copy(originalImage.getConfig(),
                            true);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        boolean hasTransparent = grayScaleImage.hasAlpha();
        int width = grayScaleImage.getWidth();
        int height = grayScaleImage.getHeight();

        // A common pixel-by-pixel grayscale conversion algorithm
        // using values obtained from en.wikipedia.org/wiki/Grayscale.
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {

                // Check if the pixel is transparent in the original
                // by checking if the alpha is 0
                if (hasTransparent
                        && ((grayScaleImage.getPixel(j, i) & 0xff000000) >> 24) == 0) {
                    continue;
                }

                // Convert the pixel to grayscale.
                int pixel = grayScaleImage.getPixel(j, i);
                int grayScale =
                        (int) (Color.red(pixel) * .299
                                + Color.green(pixel) * .587
                                + Color.blue(pixel) * .114);
                grayScaleImage.setPixel(j, i,
                        Color.rgb(grayScale, grayScale, grayScale)
                );
            }
        }

        getGrayBitmap(grayScaleImage);
        return Utils.createDirectoryAndSaveFile
                (context,
                        grayScaleImage,
                        // Name of the image file that we're filtering.
                        pathToImageFile.toString());
    }
    
    /**
     * Download the image located at the provided Internet url using
     * the URL class, store it on the android file system using a
     * FileOutputStream, and return the path to the image file on
     * disk.
     *
     * @param context	the context in which to write the file.
     * @param url       the web url.
     * 
     * @return          the absolute path to the downloaded image file on the file system.
     */
    public static Uri downloadImage(Context context,
                                    Uri url) {
    	try {
            if (!isExternalStorageWritable()) {
                Log.d(TAG,
                      "external storage is not writable");
                return null;
            }

            // Input stream.
            InputStream inputStream;

            // Filename that we're downloading (or opening).
            String filename;

            // If we're offline, open the image in our resources.
            if (DOWNLOAD_OFFLINE) {
                // Get a stream from the image resource.
                inputStream =
                    context.getResources().openRawResource(OFFLINE_TEST_IMAGE);
                filename = OFFLINE_FILENAME;
                
            // Otherwise, download the file requested by the user.
            } else {
                // Download the contents at the URL, which should
                // reference an image.
                inputStream =
                    (InputStream) new URL(url.toString()).getContent();
                filename = url.toString();
            }
//            int weight = inputStream.read();


            // Decode the InputStream into a Bitmap image.
            Bitmap bitmap =
                BitmapFactory.decodeStream(inputStream);

            getBitmap(bitmap);
//            getImageBitHeight(bitmap);
            // Bail out of we get an invalid bitmap.
            if (bitmap == null)
                return null;
            else
                // Create an output file and save the image into it.
                return Utils.createDirectoryAndSaveFile(context, 
                                                        bitmap,
                                                        filename);
        } catch (Exception e) {
            Log.e(TAG, "Exception while downloading. Returning null.");
            Log.e(TAG, e.toString());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decode an InputStream into a Bitmap and store it in a file on
     * the device.
     *
     * @param context	   the context in which to write the file.
     * @param image        the image to save
     * @param fileName     name of the file.
     * 
     * @return          the absolute path to the downloaded image file on the file system.
     */
    private static Uri createDirectoryAndSaveFile(Context context,
                                                  Bitmap image,
                                                  String fileName) {
        File directory =
            new File(Environment.getExternalStoragePublicDirectory
                     (Environment.DIRECTORY_DCIM)
                     + "/ImageDir");

        if (!directory.exists()) {
            File newDirectory =
                new File(directory.getAbsolutePath());
            newDirectory.mkdirs();
        }

        File file = new File(directory,
                             getTemporaryFilename(fileName));
        if (file.exists())
            file.delete();

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            image.compress(Bitmap.CompressFormat.JPEG,
                           100,
                           outputStream);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            // Indicate a failure.
            return null;
        }

        setLengthFile(file);
        // Get the absolute path of the image.
        String absolutePathToImage = file.getAbsolutePath();

        // Provide metadata so the downloaded image is viewable in the
        // Gallery.
        ContentValues values =
            new ContentValues();
        values.put(Images.Media.TITLE,
                   fileName);
        values.put(Images.Media.DESCRIPTION,
                   fileName);
        values.put(Images.Media.DATE_TAKEN,
                   System.currentTimeMillis ());
        values.put(Images.ImageColumns.BUCKET_DISPLAY_NAME,
                   file.getName().toLowerCase(Locale.US));
        values.put("_data",
                   absolutePathToImage);
        
        ContentResolver cr = 
            context.getContentResolver();

        // Store the metadata for the image into the Gallery.
        cr.insert(Images.Media.EXTERNAL_CONTENT_URI,
                  values);

        Log.d(TAG,
              "absolute path to image file is " 
              + absolutePathToImage);

            
        return Uri.parse(absolutePathToImage);
    }

    /**
     * This method checks if we can write image to external storage
     * 
     * @return true if an image can be written, and false otherwise
     */
    private static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals
            (Environment.getExternalStorageState());
    }

    static public void getBitmap(Bitmap bitmap) {
        imageBitmap = bitmap;
    }

//    static public void getImageBitHeight(Bitmap bitmap) {
//        imageBitHeight = bitmap.getHeight();
//    }

    static private void getGrayBitmap(Bitmap bitmap) {
        grayImageBitmap = bitmap;
    }

    static private void setLengthFile(File file) {
        lengthFile = file.length();
    }

    static public Bitmap imageBitmap;
    static public Bitmap grayImageBitmap;
//    static public int imageBitHeight;
    static public long lengthFile;

    /**
     * Create a temporary filename to store the result of a download.
     * 
     * @param url Name of the URL.
     * @return String containing the temporary filename.
     */
    static private String getTemporaryFilename(final String url) {
        // This is what you'd normally call to get a unique temporary
        // filename, but for testing purposes we always name the file
        // the same to avoid filling up student phones with numerous
        // files!
        //
        // return Base64.encodeToString(url.getBytes(),
        //                              Base64.NO_WRAP)
        //                              + System.currentTimeMillis());
        return Base64.encodeToString(url.getBytes(),
                                     Base64.NO_WRAP);
    }

    /**
     * Show a toast message.
     */
    public static void showToast(Context context,
                                 String message) {
        Toast.makeText(context,
                       message,
                       Toast.LENGTH_SHORT).show();
    }
}
