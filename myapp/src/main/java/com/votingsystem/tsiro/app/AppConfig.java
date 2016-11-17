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
    //SQLITE DATABASE
    public static final int VERSION                             =   1;
    //CODES
    public static final int NO_CONNECTION                       =   -1;
    public static final int WIFI_CONNECTION                     =   1;
    public static final int CECULAR_CONNECTION                  =   0;
    public static final int STATUS_OK                           =   200;
    public static final int STATUS_ERROR                        =   -200;
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
    public static final int ERROR_NOT_VERIFIED_YET              =   -17;
    public static final int ERROR_EMAIL_DOESNT_EXIST            =   -18;
    public static final int ERROR_RESET_PASWD_ALREADY_REQUESTED =   -19;
    public static final int ERROR_INVALID_USERNAME_PASSWORD     =   -20;
    public static final int ERROR_ACCOUNT_NOT_VERIFIED_YET      =   -21;
    public static final int ERROR_INVALID_ID                    =   -24;
    public static final int ERROR_EMPTY_REQUIRED_FIELDS         =   -25;
    public static final int ERROR_ADD_ANSWERS_NOTE              =   -26;
    public static final int ERROR_FILL_OUT_ANSWERS              =   -27;
    public static final int ERROR_EMPTY_LIST                    =   -205;
    public static final int SUCCESS_USER_SURVEY_DELETED         =   1000;
    //LOGIN ACTIVITY
    public static final int VISIBLE_PASSWORD_INPUT_TYPE         =   145;
    public static final int PASSWORD_INPUT_TYPE                 =   129;
    //SURVEYS
    public static final int NAVDRAWER_ITEM_VERTICAL_HEIGHT      =   48;
    public static final int NAVDRAWER_DEFAULT_SELECTED_POSITION =   0;

    public static final long showhideAcceptDelay                =   4000;

    private static final SparseIntArray inputValidationCodes = new SparseIntArray(3);

    public static SparseIntArray getCodes() {
        inputValidationCodes.put(NO_CONNECTION, R.string.no_connection);
        inputValidationCodes.put(STATUS_ERROR, R.string.error_occured);
        inputValidationCodes.put(UNAVAILABLE_SERVICE, R.string.unavailable_service);
        inputValidationCodes.put(INTERNAL_ERROR, R.string.error_occured);
        inputValidationCodes.put(ERROR_EMPTY_REQUIRED_FIELD, R.string.error_empty_requried_field);
        inputValidationCodes.put(ERROR_EMPTY_REQUIRED_FIELDS, R.string.fill_out_required_fields);
        inputValidationCodes.put(ERROR_INVALID_INPUT, R.string.error_invalid_input);
        inputValidationCodes.put(ERROR_INPUT_EXISTS, R.string.error_input_exists);
        inputValidationCodes.put(ERROR_FIRM_CODE_MISMATCH, R.string.error_firm_code_mismatch);
        inputValidationCodes.put(ERROR_INVALID_PASSWORD_LENGTH, R.string.error_invalid_password_length);
        inputValidationCodes.put(ERROR_PASSWORD_MISMATCH, R.string.error_passwords_mismatch);
        inputValidationCodes.put(ERROR_INVALID_EMAIL, R.string.error_invalid_email);
        inputValidationCodes.put(ERROR_NOT_VERIFIED_YET, R.string.error_not_verified_yet);
        inputValidationCodes.put(ERROR_EMAIL_DOESNT_EXIST, R.string.error_email_doesnt_exist);
        inputValidationCodes.put(ERROR_RESET_PASWD_ALREADY_REQUESTED, R.string.error_reset_password_already_requested);
        inputValidationCodes.put(ERROR_INVALID_USERNAME_PASSWORD, R.string.error_invalid_username_password);
        inputValidationCodes.put(ERROR_ACCOUNT_NOT_VERIFIED_YET, R.string.error_account_not_verified_yet);
        inputValidationCodes.put(ERROR_INVALID_ID, R.string.error_invalid_id);
        inputValidationCodes.put(ERROR_ADD_ANSWERS_NOTE, R.string.add_answers_note);
        inputValidationCodes.put(ERROR_FILL_OUT_ANSWERS, R.string.fill_out_answers);
        inputValidationCodes.put(SUCCESS_USER_SURVEY_DELETED, R.string.success_user_survey_deletion);

        return inputValidationCodes;
    }

}
