package com.votingsystem.tsiro.app;

import android.util.SparseArray;
import android.util.SparseIntArray;

import com.votingsystem.tsiro.votingsystem.R;

import java.security.interfaces.RSAKey;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 10/10/2015.
 */
public class AppConfig {
    public static final String BASE_URL                         =   "http://christosioannidis.com/giannis/Ptixiaki/";
    //public static final String BASE_URL                         =   "http://192.168.100.192/Ptixiaki/";
    public static final String SESSION_PREFS                    =   "sessionPrefs";
    public static final String KEY_USER_LEARNED_PREFS           =   "user_learned_prefs";

    //CODES
    public static final int NO_CONNECTION                       =   -1;
    public static final int WIFI_CONNECTION                     =   1;
    public static final int CECULAR_CONNECTION                  =   0;
    public static final int REQUEST_OK                          =   200;
    public static final int UNAVAILABLE_SERVICE                 =   503;
    public static final int INPUT_OK                            =   10;
    public static final int INTERNAL_ERROR                      =   -8;
    public static final int ERROR_EMPTY_INPUT                   =   -9;
    public static final int ERROR_INVALID_INPUT                 =   -10;
    public static final int ERROR_UNAVAILABLE_INPUT             =   -11;
    public static final int ERROR_INVALID_PASSWORD_LENGTH       =   -12;
    public static final int ERROR_PASSWORD_MISMATCH             =   -13;
    public static final int ERROR_INVALID_EMAIL_ADDRESS         =   -14;

    //LOGIN ACTIVITY
    public static final int VISIBLE_PASSWORD_INPUT_TYPE         =   145;
    public static final int PASSWORD_INPUT_TYPE                 =   129;
    public static final int PASSWORD_LENGTH                     =   8;


    public static final int NAVDRAWER_ITEM_VERTICAL_HEIGHT      =   48;
    public static final int NAVDRAWER_DEFAULT_SELECTED_POSITION =   0;

    public static final long showhideAcceptDelay                =   4000;

    public static final SparseIntArray inputValidationCodes = new SparseIntArray(3);

    public static SparseIntArray getCodes() {
        inputValidationCodes.put(NO_CONNECTION, R.string.no_connection);
        inputValidationCodes.put(UNAVAILABLE_SERVICE, R.string.unavailable_service);
        inputValidationCodes.put(INTERNAL_ERROR, R.string.error_occured);
        inputValidationCodes.put(ERROR_EMPTY_INPUT, R.string.empty_requried_field);
        inputValidationCodes.put(INPUT_OK, R.string.input_ok);
        inputValidationCodes.put(ERROR_INVALID_INPUT, R.string.invalid_usernamepassword);
        inputValidationCodes.put(ERROR_UNAVAILABLE_INPUT, R.string.input_exists);
        inputValidationCodes.put(ERROR_INVALID_PASSWORD_LENGTH, R.string.invalid_password_length);
        inputValidationCodes.put(ERROR_PASSWORD_MISMATCH, R.string.passwords_mismatch);
        inputValidationCodes.put(ERROR_INVALID_EMAIL_ADDRESS, R.string.invalid_email);
        return inputValidationCodes;
    }
}
