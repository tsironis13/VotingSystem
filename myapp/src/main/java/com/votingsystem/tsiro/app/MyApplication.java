package com.votingsystem.tsiro.app;

import android.app.Application;
import android.content.Context;

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
}
