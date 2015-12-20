package com.votingsystem.tsiro.mainClasses;

import android.content.Context;
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
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.squareup.leakcanary.RefWatcher;
import com.votingsystem.tsiro.ObserverPattern.ConnectivityObserver;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.ConnectivitySingleton;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.fragments.ForgotPasswordFragment;
import com.votingsystem.tsiro.fragments.RegisterFragment;
import com.votingsystem.tsiro.fragments.SignInFragment;
import com.votingsystem.tsiro.interfaces.LoginActivityCommonElements;
import com.votingsystem.tsiro.votingsystem.R;

import java.util.Observable;
import java.util.Observer;

public class LoginActivity extends AppCompatActivity implements LoginActivityCommonElements, Observer {

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
                    .replace(R.id.frame_container, signInFragment, "signInFgmt")
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
                .replace(R.id.frame_container, forgotPasswordFgmt, "forgotPasswordFgmt")
                .addToBackStack("forgotPasswordFgmt")
                .commit();
    }

    @Override
    public void registerOnClick() {
        registerFragment = new RegisterFragment();
        if ( getSupportFragmentManager().getBackStackEntryCount() > 0 ) getSupportFragmentManager().popBackStack();
        loginActivityBundle.putParcelable("connectivityObserver", connectivityObserver);
        registerFragment.setArguments(loginActivityBundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_container, registerFragment, "registerFgmt")
                .addToBackStack("registerFgmt")
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
    }
}
