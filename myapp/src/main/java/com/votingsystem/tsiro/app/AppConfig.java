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
    //public static final String BASE_URL                         =   "http://christosioannidis.com/giannis/Ptixiaki/";
    public static final String BASE_URL                         =   "http://192.168.100.192/Ptixiaki/";
    public static final String SESSION_PREFS                    =   "sessionPrefs";
    public static final String KEY_USER_LEARNED_PREFS           =   "user_learned_prefs";

    //CODES
    public static final int NO_CONNECTION                       =   -1;
    public static final int WIFI_CONNECTION                     =   1;
    public static final int CECULAR_CONNECTION                  =   0;
    public static final int STATUS_OK                           =   200;
    public static final int UNAVAILABLE_SERVICE                 =   503;
    public static final int INPUT_OK                            =   10;
    public static final int INTERNAL_ERROR                      =   -8;
    public static final int ERROR_EMPTY_REQUIRED_FIELD          =   -9;
    public static final int ERROR_INVALID_INPUT                 =   -10;
    public static final int ERROR_INPUT_EXISTS                  =   -11;
    public static final int ERROR_NO_FIRM_PICKED                =   -12;
    public static final int ERROR_PASSWORD_MISMATCH             =   -13;
    public static final int ERROR_FIRM_CODE_MISMATCH            =   -14;
    public static final int ERROR_INVALID_PASSWORD_LENGTH       =   -15;
    public static final int ERROR_INVALID_EMAIL                 =   -16;
    //LOGIN ACTIVITY
    public static final int VISIBLE_PASSWORD_INPUT_TYPE         =   145;
    public static final int PASSWORD_INPUT_TYPE                 =   129;

    public static final int NAVDRAWER_ITEM_VERTICAL_HEIGHT      =   48;
    public static final int NAVDRAWER_DEFAULT_SELECTED_POSITION =   0;

    public static final long showhideAcceptDelay                =   4000;

    public static final SparseIntArray inputValidationCodes = new SparseIntArray(3);

    public static SparseIntArray getCodes() {
        inputValidationCodes.put(NO_CONNECTION, R.string.no_connection);
        inputValidationCodes.put(UNAVAILABLE_SERVICE, R.string.unavailable_service);
        inputValidationCodes.put(INTERNAL_ERROR, R.string.error_occured);
        inputValidationCodes.put(ERROR_EMPTY_REQUIRED_FIELD, R.string.error_empty_requried_field);
        inputValidationCodes.put(ERROR_INVALID_INPUT, R.string.error_invalid_input);
        inputValidationCodes.put(ERROR_INPUT_EXISTS, R.string.error_input_exists);
        inputValidationCodes.put(ERROR_FIRM_CODE_MISMATCH, R.string.error_firm_code_mismatch);
        inputValidationCodes.put(ERROR_INVALID_PASSWORD_LENGTH, R.string.error_invalid_password_length);
        inputValidationCodes.put(ERROR_PASSWORD_MISMATCH, R.string.error_passwords_mismatch);
        inputValidationCodes.put(ERROR_INVALID_EMAIL, R.string.error_invalid_email);
        return inputValidationCodes;
    }
}
