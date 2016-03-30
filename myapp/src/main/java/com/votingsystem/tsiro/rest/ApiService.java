package com.votingsystem.tsiro.rest;

import com.votingsystem.tsiro.POJO.Firm;
import com.votingsystem.tsiro.POJO.RegisterFormBody;
import com.votingsystem.tsiro.POJO.Survey;
import com.votingsystem.tsiro.POJO.User;
import com.votingsystem.tsiro.POJO.UserConnectionStaff;
import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by user on 9/11/2015.
 */
public interface ApiService {

    @POST("requests/registration/register-user.php")
    Call<UserConnectionStaff> registerUser(@Body RegisterFormBody registerFormBody);

    @FormUrlEncoded
    @POST("login/login.php")
    Call<User> validateUser(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("requests/firms/firmRequests.php")
    Call<Firm> getFirmNames(@Field("action") String action);

    @FormUrlEncoded
    @POST("functions/firmRequests.php")
    Call<Firm> getFirmByNameAndCode(@Field("action") String action, @Field("firm-name") String firmName, @Field("firm-code") String firmCode);



    @POST("functions/populateDataModel.php")
    Call<Survey> addSurvey(@Body Survey survey);
}
