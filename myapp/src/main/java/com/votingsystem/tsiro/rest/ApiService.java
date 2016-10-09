package com.votingsystem.tsiro.rest;

import com.votingsystem.tsiro.POJO.AllSurveys;
import com.votingsystem.tsiro.POJO.Firm;
import com.votingsystem.tsiro.POJO.FirmSurveyDetails;
import com.votingsystem.tsiro.POJO.AllSurveysBody;
import com.votingsystem.tsiro.POJO.LoginAndResetUserPasswordStuff;
import com.votingsystem.tsiro.POJO.LoginFormBody;
import com.votingsystem.tsiro.POJO.NewSurvey;
import com.votingsystem.tsiro.POJO.RegisterFormBody;
import com.votingsystem.tsiro.POJO.RegisterUserStuff;
import com.votingsystem.tsiro.POJO.ResetPassowrdBody;
import com.votingsystem.tsiro.POJO.SurveyAnswersBody;
import com.votingsystem.tsiro.POJO.SurveyDetails;
import com.votingsystem.tsiro.POJO.SurveyQuestionBody;
import com.votingsystem.tsiro.POJO.SurveyQuestions;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by user on 9/11/2015.
 */
public interface ApiService {

    @POST("requests/users.php")
    Call<RegisterUserStuff> registerUser(@Body RegisterFormBody registerFormBody);

    @POST("requests/users.php")
    Call<LoginAndResetUserPasswordStuff> resetUserPassword(@Body ResetPassowrdBody resetPassowrdBody);

    @POST("requests/users.php")
    Call<LoginAndResetUserPasswordStuff> loginUser(@Body LoginFormBody loginFormBody);

    @FormUrlEncoded
    @POST("requests/firms.php")
    Call<Firm> getFirmNames(@Field("action") String action);

    @FormUrlEncoded
    @POST("requests/firms.php")
    Call<FirmSurveyDetails> getFirmSurveyDetails(@Field("action") String action, @Field("user_id") int user_id, @Field("firm_id") int firm_id);

    @POST("requests/surveys.php")
    Call<AllSurveys> getAllSurveys(@Body AllSurveysBody getAllSurveysBody);

    @POST("requests/surveys.php")
    Call<SurveyQuestions> getSurveyQuestions(@Body SurveyQuestionBody surveyQuestionBody);

    @POST("requests/surveys.php")
    Call<SurveyDetails> uploadSurveyAnswersOrGetSurveyStats(@Body SurveyAnswersBody surveyAnswersBody);

    @POST("requests/surveys.php")
    Call<AllSurveys> uploadNewUserSurvey(@Body NewSurvey newSurvey);

//    @FormUrlEncoded
//    @POST("functions/firmRequests.php")
//    Call<Firm> getFirmByNameAndCode(@Field("action") String action, @Field("firm-name") String firmName, @Field("firm-code") String firmCode);

}
