package com.votingsystem.tsiro.rest;

import com.votingsystem.tsiro.POJO.Firm;
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

    @FormUrlEncoded
    @POST("connectionStaff/registration/registration-requests.php")
    Call<UserConnectionStaff> isUsernameValid(@Field("action") String action, @Field("username") String username);

    @FormUrlEncoded
    @POST("connectionStaff/registration/registration-requests.php")
    Call<UserConnectionStaff> isEmailValid(@Field("action") String action, @Field("email") String email);

    @FormUrlEncoded
    @POST("registration/register-user.php")
    Call<UserConnectionStaff> registerUser(@Field("username") String username, @Field("password") String password, @Field("confirm-password") String confirm_password, @Field("email") String email, @Field("firm-name") String firm_name, @Field("firm-code") String firm_code);

    @FormUrlEncoded
    @POST("login/login.php")
    Call<User> validateUser(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("functions/firmRequests.php")
    Call<Firm> getFirmNames(@Field("action") String action);

    @FormUrlEncoded
    @POST("functions/firmRequests.php")
    Call<Firm> getFirmByNameAndCode(@Field("action") String action, @Field("firm-name") String firmName, @Field("firm-code") String firmCode);



    @POST("functions/populateDataModel.php")
    Call<Survey> addSurvey(@Body Survey survey);
}
