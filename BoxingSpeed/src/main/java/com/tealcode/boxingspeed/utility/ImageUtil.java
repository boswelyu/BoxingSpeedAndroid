package com.tealcode.boxingspeed.utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by YuBo on 2017/10/23.
 */

public class ImageUtil {

    private static final String TAG = "ImageUtil";

    public static Bitmap getBitmapFromUri(Activity activity, Uri uri)
    {
        if(uri != null) {
            try {
                return MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
            } catch (IOException e) {
                return null;
            }
        }

        Log.e(TAG, "getBitmapFromUri got empty uri");
        return null;
    }

    public static Uri saveBitmapToUri(Activity activity, Bitmap bitmap, String folder, String filename)
    {
        if(activity == null || bitmap == null || folder == null || folder.isEmpty() || filename == null || filename.isEmpty())
        {
            Log.e(TAG, "Invalid input parameter for saveBitmapToUri");
            return null;
        }

        // 把Bitmap内容保存到本地一个临时文件
        File tempDir = new File(activity.getFilesDir() + "/" + folder);
        if(!tempDir.exists()) {
            tempDir.mkdir();
        }

        File imageFile = new File(tempDir.getAbsolutePath(), filename);
        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.e(TAG, "cropBitmap failed to save bitmap to local URI because exception: " + e.toString());
            return null;
        }

        return Uri.fromFile(imageFile);
    }

    public static Bitmap scaleBitmapToFixSize(Bitmap bitmap, int destWidth, int destHeight)
    {
        int sWidth = bitmap.getWidth();
        int sHeight = bitmap.getHeight();
        if(sWidth <= destWidth && sHeight <= destHeight) {
            return bitmap;
        }

        float scaleWidth = (float)destWidth / (float)sWidth;
        float scaleHeight = (float)destHeight / (float)sHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bitmap, 0, 0, sWidth, sHeight, matrix, true);
    }

    public static Bitmap scaleBitmapToFitScreen(Context context, Bitmap bitmap)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if(wm == null) {
            Log.e(TAG, "Failed to get Window Size, don't know how to scale");
            return bitmap;
        }

        DisplayMetrics outMatrix = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMatrix);
        int screenWidth = outMatrix.widthPixels;
        int screenHeight = outMatrix.heightPixels;

        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        if(imageWidth >= screenWidth || imageHeight >= screenHeight) {
            // Image is big, don't need scale
            return bitmap;
        }

        float scaleWidth = (float)screenWidth / (float)imageWidth;
        float scaleHeight = (float)screenHeight / (float)imageHeight;
        float scale = scaleWidth;
        if(scaleHeight < scaleWidth) {
            scale = scaleHeight;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        return Bitmap.createBitmap(bitmap, 0, 0, imageWidth, imageHeight, matrix, true);
    }
}
