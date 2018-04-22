package com.raziel.razielmovie;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Raziel Shushan on 21/03/2018.
 * This Task made to save the poster in the phone app storge'
 * the image save in - /data/data/raizelMovie/app_"MovieName"/poster.jpg
 * the function return the path
 */

public class ImageStorge {
    Activity activity;

    public ImageStorge(Activity activity) {
        this.activity = activity;
    }

    public String saveToInternalStorage(Bitmap bitmapImage, String movieName){
        ContextWrapper cw = new ContextWrapper(activity.getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir(movieName, Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"poster.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
}
