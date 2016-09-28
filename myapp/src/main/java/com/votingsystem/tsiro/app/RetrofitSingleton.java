package com.votingsystem.tsiro.app;
import android.content.Context;

import com.google.gson.Gson;
import com.votingsystem.tsiro.rest.ApiService;
import com.votingsystem.tsiro.rest.ServiceGenerator;

/**
 * Created by user on 14/11/2015.
 */
public class RetrofitSingleton {

    private static RetrofitSingleton rInstance = null;
    private static RetrofitSingleton rInstanceWithCustomGson = null;
    private ApiService apiService;

    private RetrofitSingleton(){
        apiService = ServiceGenerator.createService(ApiService.class);
    }

    private RetrofitSingleton(Gson gson){
        apiService = ServiceGenerator.createServiceCustomGson(ApiService.class, gson);
    }

    public static RetrofitSingleton getInstance() {
        if ( rInstance == null ) {
            rInstance = new RetrofitSingleton();
        }
        return rInstance;
    }

    public static RetrofitSingleton getInstanceWithCustoGson(Gson gson) {
        if ( rInstanceWithCustomGson == null ) {
            rInstanceWithCustomGson = new RetrofitSingleton(gson);
        }
        return rInstanceWithCustomGson;
    }

    public ApiService getApiService() {
        return apiService;
    }

}
