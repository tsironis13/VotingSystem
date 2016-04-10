package com.votingsystem.tsiro.rest;

import com.votingsystem.tsiro.POJO.Firm;
import com.votingsystem.tsiro.POJO.RegisterFormBody;
import com.votingsystem.tsiro.POJO.RegisterUserStuff;
import com.votingsystem.tsiro.POJO.ResetPassowrdBody;
import com.votingsystem.tsiro.POJO.ResetUserPasswordStuff;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by user on 9/11/2015.
 */
public interface ApiService {

    @POST("requests/user/register-user.php")
    Call<RegisterUserStuff> registerUser(@Body RegisterFormBody registerFormBody);

    @POST("requests/user/reset-password.php")
    Call<ResetUserPasswordStuff> resetUserPassword(@Body ResetPassowrdBody resetPassowrdBody);

    @FormUrlEncoded
    @POST("requests/firms/firm-requests.php")
    Call<Firm> getFirmNames(@Field("action") String action);

    @FormUrlEncoded
    @POST("functions/firmRequests.php")
    Call<Firm> getFirmByNameAndCode(@Field("action") String action, @Field("firm-name") String firmName, @Field("firm-code") String firmCode);

}
