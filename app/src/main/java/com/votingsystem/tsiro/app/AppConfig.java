package com.votingsystem.tsiro.app;

import android.util.SparseArray;
import android.util.SparseIntArray;

import com.votingsystem.tsiro.votingsystem.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 10/10/2015.
 */
public class AppConfig {
    public static final String BASE_URL                         =   "http://aetos.it.teithe.gr/~tsironis/Ptixiaki/";
    public static final String SESSION_PREFS                    =   "sessionPrefs";
    public static final String KEY_USER_LEARNED_PREFS           =   "user_learned_prefs";

    public static final int NO_CONNECTION                       =   -1;
    public static final int WIFI_CONNECTION                     =   1;
    public static final int CECULAR_CONNECTION                  =   0;
    public static final int REQUEST_OK                          =   200;
    public static final int SERVICE_NOT_AVAILABLE               =   503;
    public static final int VISIBLE_PASSWORD_INPUT_TYPE         =   145;
    public static final int PASSWORD_INPUT_TYPE                 =   129;

    public static final int NAVDRAWER_ITEM_VERTICAL_HEIGHT      =   48;
    public static final int NAVDRAWER_DEFAULT_SELECTED_POSITION =   0;

    public static final int INPUT_OK                            =   10;
    public static final int ERROR_INVALID_INPUT                 =   -10;
    public static final int ERROR_UNAVAILABLE_INPUT             =   -11;

    public static final long showhideAcceptDelay                =   4000;

    public static final SparseIntArray inputValidationCodes = new SparseIntArray(3);
    public static SparseIntArray getCodes() {
        inputValidationCodes.put(INPUT_OK, R.string.input_ok);
        inputValidationCodes.put(ERROR_INVALID_INPUT, R.string.invalid_usernamepassword);
        inputValidationCodes.put(ERROR_UNAVAILABLE_INPUT, R.string.input_exists);
        return inputValidationCodes;
    }
}
