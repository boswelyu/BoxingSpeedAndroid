package com.tealcode.boxingspeed.helper.http;

import android.content.Context;
import android.util.Log;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * Created by YuBo on 2017/9/27.
 */

public class HttpImageLoader {

    private static final String TAG = "HttpImageLoader";
    private static ImageLoaderConfiguration configure;
    private static ImageLoader instance;


    public static void InitConfig(Context context)
    {
        configure = ImageLoaderConfiguration.createDefault(context);
        instance = ImageLoader.getInstance();
        instance.init(configure);
    }

    public static ImageLoader getLoaderInstance() {
        return instance;
    }

    public static void ClearCache()
    {
        try {
            if(instance != null) {
                instance.clearMemoryCache();
                instance.clearDiskCache();
            }
        }catch(Exception e) {
            Log.e(TAG, e.toString());
        }
    }

}
