package com.votingsystem.tsiro.LoginActivityMVC;

import android.util.Log;

import com.votingsystem.tsiro.POJO.Firm;
import com.votingsystem.tsiro.POJO.LoginAndResetUserPasswordStuff;
import com.votingsystem.tsiro.POJO.LoginFormBody;
import com.votingsystem.tsiro.POJO.RegisterFormBody;
import com.votingsystem.tsiro.POJO.RegisterUserStuff;
import com.votingsystem.tsiro.POJO.ResetPassowrdBody;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.RetrofitSingleton;
import com.votingsystem.tsiro.helperClasses.FirmNameWithID;
import com.votingsystem.tsiro.rest.ApiService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by user on 5/2/2016.
 */
public class LAMVCInteractorImpl implements LAMVCInteractor {

    private static final String debugTag = LAMVCInteractorImpl.class.getSimpleName();
    private ApiService apiService;

    public LAMVCInteractorImpl() {
        apiService = RetrofitSingleton.getInstance().getApiService();
    }

    @Override
    public void validateForm(RegisterFormBody registerFormBody, final boolean isAdded, final LAMVCFinishedListener LAMVCfinishedListener) {
        Call<RegisterUserStuff> registerUserCall = apiService.registerUser(registerFormBody);
        registerUserCall.enqueue(new Callback<RegisterUserStuff>() {
            @Override
            public void onResponse(Response<RegisterUserStuff> response, Retrofit retrofit) {
                if (isAdded) {
                    if (response.body().getCode() != AppConfig.STATUS_OK) {
                        LAMVCfinishedListener.onFailure(response.body().getCode(), response.body().getTag(), response.body().getHint(), null);
                    } else {
                        LAMVCfinishedListener.onSuccess();
                    }
                    Log.e(debugTag, "Code: "+response.body().getCode()+ " TAG: "+response.body().getTag()+" Hint: "+response.body().getHint());
                }
            }
            @Override
            public void onFailure(Throwable t) {
                if (isAdded) {
                    if (t instanceof IOException) {
                        LAMVCfinishedListener.displayFeedbackMsg(AppConfig.UNAVAILABLE_SERVICE);
                    } else {
                        LAMVCfinishedListener.displayFeedbackMsg(AppConfig.INTERNAL_ERROR);
                    }
                }
                Log.e(debugTag, t.toString());
            }
        });
    }

    @Override
    public void resetPassword(ResetPassowrdBody resetPassowrdBody, final boolean isAdded, final LAMVCFinishedListener LAMVCfinishedListener) {
        Call<LoginAndResetUserPasswordStuff> resetUserPasswordStuffCall = apiService.resetUserPassword(resetPassowrdBody);
        resetUserPasswordStuffCall.enqueue(new Callback<LoginAndResetUserPasswordStuff>() {
            @Override
            public void onResponse(Response<LoginAndResetUserPasswordStuff> response, Retrofit retrofit) {
                if (isAdded) {
                    if (response.body().getCode() != AppConfig.STATUS_OK) {
                        String retry_in = (response.body().getRetry_in() != null) ? response.body().getRetry_in() : null;
                        LAMVCfinishedListener.onFailure(response.body().getCode(), "", response.body().getHint(), retry_in);
                    } else {
                        LAMVCfinishedListener.onSuccess();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (isAdded) {
                    if (t instanceof IOException) {
                        LAMVCfinishedListener.displayFeedbackMsg(AppConfig.UNAVAILABLE_SERVICE);
                    } else {
                        LAMVCfinishedListener.displayFeedbackMsg(AppConfig.INTERNAL_ERROR);
                    }
                }
            }
        });
    }

    public void loginUser(LoginFormBody loginFormBody, final boolean isAdded, final LAMVCFinishedListener LAMVCfinishedListener) {
        Call<LoginAndResetUserPasswordStuff> loginUserPasswordStuffCall = apiService.loginUser(loginFormBody);
        loginUserPasswordStuffCall.enqueue(new Callback<LoginAndResetUserPasswordStuff>() {
            @Override
            public void onResponse(Response<LoginAndResetUserPasswordStuff> response, Retrofit retrofit) {
                if (isAdded) {
                    if (response.body().getCode() != AppConfig.STATUS_OK) {
                        LAMVCfinishedListener.onFailure(response.body().getCode(), "", "", null);
                    } else {
                        LAMVCfinishedListener.onSuccess();
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (isAdded) {
                    if (t instanceof IOException) {
                        LAMVCfinishedListener.displayFeedbackMsg(AppConfig.UNAVAILABLE_SERVICE);
                    } else {
                        LAMVCfinishedListener.displayFeedbackMsg(AppConfig.INTERNAL_ERROR);
                    }
                }
            }
        });
    }

    @Override
    public void populateFirmNamesSpnr(final ArrayList<FirmNameWithID> firmNameWithIDArrayList, final LAMVCFinishedListener LAMVCFinishedListener) {
        Call<Firm> call = apiService.getFirmNames("getFirmNames");
        call.enqueue(new Callback<Firm>() {
            @Override
            public void onResponse(Response<Firm> response, Retrofit retrofit) {
                if (response.body().getStatus() == AppConfig.STATUS_OK) {
                    List<Firm.FirmElement> firmElementList = response.body().getFirm_element();
                    for (int i = 0; i < firmElementList.size(); i++) {
                        Log.d(debugTag, "firm_id: " + firmElementList.get(i).getFirm_id() + " firm_name: " + firmElementList.get(i).getFirm_name());
                        firmNameWithIDArrayList.add(new FirmNameWithID(firmElementList.get(i).getFirm_name(), firmElementList.get(i).getFirm_id()));
                    }
                    LAMVCFinishedListener.onSuccessfirmNamesSpnrLoad(firmNameWithIDArrayList);
                }
            }
            @Override
            public void onFailure(Throwable t) {
                LAMVCFinishedListener.onFailurefirmNamesSpnrLoad(firmNameWithIDArrayList);
            }
        });
    }
}
