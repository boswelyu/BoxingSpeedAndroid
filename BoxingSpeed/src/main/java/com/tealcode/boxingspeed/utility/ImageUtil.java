package com.tealcode.boxingspeed.utility;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

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
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            Log.e(TAG, "cropBitmap failed to save bitmap to local URI because exception: " + e.toString());
            return null;
        }

        return Uri.fromFile(imageFile);
    }
}
