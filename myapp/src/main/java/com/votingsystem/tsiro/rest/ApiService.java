package com.votingsystem.tsiro.rest;

import com.votingsystem.tsiro.POJO.AllSurveys;
import com.votingsystem.tsiro.POJO.DashboardBody;
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

    @POST("mobileactions.php")
    Call<RegisterUserStuff> registerUser(@Body RegisterFormBody registerFormBody);

    @POST("mobileactions.php")
    Call<LoginAndResetUserPasswordStuff> resetUserPassword(@Body ResetPassowrdBody resetPassowrdBody);

    @POST("mobileactions.php")
    Call<LoginAndResetUserPasswordStuff> loginUser(@Body LoginFormBody loginFormBody);

    @POST("mobileactions.php")
    Call<Firm> getFirmNames(@Body Firm firm);

    @POST("mobileactions.php")
    Call<FirmSurveyDetails> getFirmSurveyDetails(@Body DashboardBody dashboardBody);

    @POST("mobileactions.php")
    Call<AllSurveys> getAllSurveys(@Body AllSurveysBody getAllSurveysBody);

    @POST("mobileactions.php")
    Call<SurveyQuestions> getSurveyQuestions(@Body SurveyQuestionBody surveyQuestionBody);

    @POST("mobileactions.php")
    Call<SurveyDetails> uploadSurveyAnswersOrGetSurveyStats(@Body SurveyAnswersBody surveyAnswersBody);

    @POST("mobileactions.php")
    Call<AllSurveys> uploadNewUserSurvey(@Body NewSurvey newSurvey);

    @POST("mobileactions.php")
    Call<AllSurveys> deleteUserSurvey(@Body NewSurvey newSurvey);


//    @FormUrlEncoded
//    @POST("functions/firmRequests.php")
//    Call<Firm> getFirmByNameAndCode(@Field("action") String action, @Field("firm-name") String firmName, @Field("firm-code") String firmCode);

}
