package com.votingsystem.tsiro.app;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

//import com.squareup.leakcanary.LeakCanary;
//import com.squareup.leakcanary.RefWatcher;

/**
 * Created by user on 10/10/2015.
 */
public class MyApplication extends Application {

    private static MyApplication sInstance;
    //private RefWatcher refWatcher;

    //public static RefWatcher getRefWatcher(Context context) {
        //MyApplication application = (MyApplication) context.getApplicationContext();
        //return application.refWatcher;
    //}

    @Override
    public void onCreate() {
        super.onCreate();
        //refWatcher = LeakCanary.install(this);
        //sInstance = this;
    }

    //public static MyApplication getInstance(){
      //  return sInstance;
    //}

   // public static Context getAppContext(){
      //  return sInstance.getApplicationContext();
    //}

    public static double convertPixelToDpAndViceVersa(Context context, double pixels, double dps) {
        double value    = 0;
        int density     = 0;
        switch (context.getResources().getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                density = 120;
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                density = 160;
                break;
            case DisplayMetrics.DENSITY_HIGH:
                density = 240;
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                density = 320;
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                density = 480;
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                density = 640;
                break;
        }
        if (pixels != 0) {
            value = pixels / (density / 160);
        } else {
            value = dps * ( density / 160 );
        }
        Log.e("sadds", value+"");
        return value;
    }
}
