package com.votingsystem.tsiro.mainClasses;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import com.rey.material.widget.EditText;
import android.widget.TextView;
import com.squareup.leakcanary.RefWatcher;
import com.votingsystem.tsiro.ObserverPattern.ConnectivityObserver;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.ConnectivitySingleton;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.fragments.ForgotPasswordFragment;
import com.votingsystem.tsiro.fragments.RegisterFragment;
import com.votingsystem.tsiro.fragments.SignInFragment;
import com.votingsystem.tsiro.interfaces.LoginActivityCommonElementsAndMuchMore;
import com.votingsystem.tsiro.votingsystem.R;
import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.Observer;

public class LoginActivity extends AppCompatActivity implements LoginActivityCommonElementsAndMuchMore, Observer {

    private static final String debugTag = LoginActivity.class.getSimpleName();
    private SpannableStringBuilder spannableStringBuilder;
    private StyleSpan styleSpan;
    private SignInFragment signInFragment;
    private RegisterFragment registerFragment;
    private ForgotPasswordFragment forgotPasswordFgmt;
    private ClickableSpan clickableSpan;
    private ConnectivityObserver connectivityObserver;
    private Bundle loginActivityBundle;
    public static boolean connectionStatusUpdated;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        connectivityObserver = ConnectivitySingleton.getInstance();
        loginActivityBundle = new Bundle();
        if ( savedInstanceState == null ) {
            signInFragment = new SignInFragment();
            loginActivityBundle.putParcelable("connectivityObserver", connectivityObserver);
            signInFragment.setArguments(loginActivityBundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_container, signInFragment, AppConfig.SINGIN_FRAGMENT_TAG)
                    .commit();
        }
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                Log.d(debugTag, "BACK STACK CHANGED!");
                Log.d(debugTag, "BACK STACK COUNT: " + getSupportFragmentManager().getBackStackEntryCount());
                for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
                    Log.d(debugTag, "BACK STACK ENTRIES: " + getSupportFragmentManager().getBackStackEntryAt(i).getName());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectivityObserver.addObserver(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        connectivityObserver.deleteObserver(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher(this);
        refWatcher.watch(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if ( getSupportFragmentManager().getBackStackEntryCount() > 0 ) getSupportFragmentManager().popBackStack(0, 0);
    }

    @Override
    public void setLoginActivitySpan(TextView textView, String stringResource, int start, int end, final int code) {
        spannableStringBuilder = new SpannableStringBuilder(stringResource);
        styleSpan = new StyleSpan(Typeface.BOLD);
        clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {

            }
        };
        spannableStringBuilder.setSpan(clickableSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(styleSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.loginSpanColor)), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(spannableStringBuilder);
    }

    @Override
    public void forgotPasswordOnClick() {
        forgotPasswordFgmt = new ForgotPasswordFragment();
        loginActivityBundle.putParcelable("connectivityObserver", connectivityObserver);
        forgotPasswordFgmt.setArguments(loginActivityBundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, forgotPasswordFgmt, AppConfig.FORGOT_PASSWORD_FRAGMENT)
                .addToBackStack(AppConfig.FORGOT_PASSWORD_FRAGMENT)
                .commit();
    }

    @Override
    public void registerOnClick() {
        registerFragment = new RegisterFragment();
        if ( getSupportFragmentManager().getBackStackEntryCount() > 0 ) getSupportFragmentManager().popBackStack();
        loginActivityBundle.putParcelable("connectivityObserver", connectivityObserver);
        registerFragment.setArguments(loginActivityBundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, registerFragment, AppConfig.REGISTER_FRAGMENT_TAG)
                .addToBackStack(AppConfig.REGISTER_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void signInHereOnClick() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) getSupportFragmentManager().popBackStack();
    }

    public static SharedPreferences getSessionPrefs(Context context) {
        //Mode private so only this app can modify this SharedPreferences file
        SharedPreferences sessionPrefs = context.getSharedPreferences(AppConfig.SESSION_PREFS, Context.MODE_PRIVATE);
        return sessionPrefs;
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        connectionStatusUpdated = true;
        intent = new Intent("networkStateUpdated");
        intent.putExtra("connectivityStatus", connectivityObserver.getConnectivityStatus(getApplicationContext()));
        sendBroadcast(intent);
    }

    @Override
    public boolean validateEditText(EditText[] fields) {
        for ( int i = 0 ; i < fields.length; i++ ) {
            if ( fields[i].getText().toString().isEmpty() && fields[i].getTag().equals(getResources().getString(R.string.required))) return false;
        }
        return true;
    }

    @Override
    public String encodeUtf8(String text) {
        try {
            byte[] data = text.getBytes("UTF-8");
            return Base64.encodeToString(data, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String decodeUtf8(String text) {
        try {
            byte[] data = Base64.decode(text, Base64.DEFAULT);
            return new String(data, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
