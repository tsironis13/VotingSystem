package com.votingsystem.tsiro.app;

/**
 * Created by user on 10/10/2015.
 */
public class AppConfig {

    public static final String BASE_URL                         =   "http://aetos.it.teithe.gr/~tsironis/Ptixiaki/";

    public static final String SESSION_PREFS                    =   "sessionPrefs";
    public static final String KEY_USER_LEARNED_PREFS           =   "user_learned_prefs";

    public static final int NAVDRAWER_ITEM_VERTICAL_HEIGHT      =   48;
    public static final int NAVDRAWER_DEFAULT_SELECTED_POSITION =   0;

    public static final String URL_LOGIN = "http://aetos.it.teithe.gr/~tsironis/Ptixiaki/login/login.php";
    public static final String TAG_LOGIN_REQUEST = "login_request"; //tag for canceling specific request
    public static final String URL_FIRMNAMES = "http://aetos.it.teithe.gr/~tsironis/Ptixiaki/functions/firmNamesRequest.php";
    public static final String TAG_FIRMNAMES_REQUEST = "firmnames_request";
    public static final String URL_FIRM = "http://aetos.it.teithe.gr/~tsironis/Ptixiaki/functions/firmPopupRequest.php";
    public static final String TAG_FIRM_REQUEST = "firm_request";
    //JSON node names
    public static final String TAG_FIRM_ARRAY = "firms"; // json array
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_FIRM_NAME = "firm_name";
    public static final String TAG_FIRM_CITY = "firm city";
    public static final String TAG_ERROR = "error";
    public static final String TAG_ERROR_MSG = "error_msg";
    public static final String TAG_USERNAME = "username";
    public static final String TAG_PASSWORD = "password";
    public static final String TAG_EMAIL = "email";
}
