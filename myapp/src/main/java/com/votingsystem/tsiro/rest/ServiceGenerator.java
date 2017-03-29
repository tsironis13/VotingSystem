package com.votingsystem.tsiro.rest;

import android.util.Log;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.votingsystem.tsiro.app.AppConfig;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by user on 10/11/2015.
 */
public class ServiceGenerator {

    private static OkHttpClient okHttpClient = new OkHttpClient();

    public static <S> S createService(Class<S> serviceClass) {

        Retrofit.Builder builder = new Retrofit.Builder()
                                       .baseUrl(AppConfig.BASE_URL)
                                       .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.client(okHttpClient).build();
        Log.d("Service Generator: ", retrofit.toString());
        return retrofit.create(serviceClass);
    }

    public static <S> S createServiceCustomGson(Class<S> serviceClass, Gson gson) {
        Retrofit.Builder builder = new Retrofit.Builder()
                                       .baseUrl(AppConfig.BASE_URL)
                                       .addConverterFactory(GsonConverterFactory.create(gson));
        Retrofit retrofit = builder.client(okHttpClient).build();
        Log.d("Service Generator: ", retrofit.toString());
        return retrofit.create(serviceClass);
    }
}
