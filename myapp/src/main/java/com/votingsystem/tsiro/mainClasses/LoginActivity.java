package com.votingsystem.tsiro.mainClasses;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.androidadvance.topsnackbar.TSnackbar;
import com.rey.material.app.BottomSheetDialog;
import com.rey.material.widget.EditText;

import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rey.material.widget.SnackBar;
import com.squareup.leakcanary.RefWatcher;
import com.votingsystem.tsiro.ObserverPattern.NetworkStateListeners;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.broadcastReceivers.NetworkState;
import com.votingsystem.tsiro.fragments.ForgotPasswordFragment;
import com.votingsystem.tsiro.fragments.RegisterFragment;
import com.votingsystem.tsiro.fragments.SignInFragment;
import com.votingsystem.tsiro.interfaces.DismissErrorContainerSnackBar;
import com.votingsystem.tsiro.interfaces.LoginActivityCommonElementsAndMuchMore;
import com.votingsystem.tsiro.votingsystem.R;
import java.io.UnsupportedEncodingException;
import java.util.Observable;
import java.util.Observer;

public class LoginActivity extends AppCompatActivity implements NetworkStateListeners, LoginActivityCommonElementsAndMuchMore, DismissErrorContainerSnackBar {

    private static final String debugTag = LoginActivity.class.getSimpleName();
    private SpannableStringBuilder spannableStringBuilder;
    private StyleSpan styleSpan;
    private SignInFragment signInFragment;
    private RegisterFragment registerFragment;
    private ForgotPasswordFragment forgotPasswordFgmt;
    private ClickableSpan clickableSpan;
    private Bundle loginActivityBundle;
    private int connectionStatus;
    private NetworkState networkState;
    private RelativeLayout errorContainerRlt;
    private SnackBar loginActivitySnkBar;
    private BottomSheetDialog connectionSettingsDialog;
    private View connectionSettingsDialogView;
    private TSnackbar errorContainerSnackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        if ( savedInstanceState == null ) {
            networkState        = new NetworkState();
            loginActivityBundle = new Bundle();

            errorContainerRlt       =   (RelativeLayout) findViewById(R.id.errorContainerRlt);
            loginActivitySnkBar     =   (SnackBar) findViewById(R.id.loginActivitySnkBar);

            signInFragment = new SignInFragment();
            signInFragment.setArguments(loginActivityBundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.loginActivityFgmtContainer, signInFragment, getResources().getString(R.string.signInFgmt))
                    .commit();

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
            loginActivitySnkBar.actionClickListener(new SnackBar.OnActionClickListener() {
                @Override
                public void onActionClick(SnackBar sb, int actionId) {
                    connectionSettingsDialog = new BottomSheetDialog(LoginActivity.this, R.style.ConnectionSettingsBottomSheetDialog);
                    connectionSettingsDialogView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.connection_settings_bottom_sheet_dialog, null);
                    connectionSettingsDialog.setContentView(connectionSettingsDialogView);
                    connectionSettingsDialog.show();
                }
            });
            networkState.addListener(this);
            this.registerReceiver(networkState, new IntentFilter(getResources().getString(R.string.connectivity_change)));
        }
    }

    public void connectionSettingsLltOnClick(View view){
        if (connectionSettingsDialog != null && connectionSettingsDialog.isShowing()) connectionSettingsDialog.dismiss();
        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkState.removeListener(this);
        this.unregisterReceiver(networkState);
        RefWatcher refWatcher = MyApplication.getRefWatcher(this);
        refWatcher.watch(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) getSupportFragmentManager().popBackStack(0, 0);
        if (errorContainerSnackbar != null) errorContainerSnackbar.dismiss();
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
        forgotPasswordFgmt.setArguments(loginActivityBundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.loginActivityFgmtContainer, forgotPasswordFgmt, getResources().getString(R.string.forgotPasswordFgmt))
                .addToBackStack(getResources().getString(R.string.forgotPasswordFgmt))
                .commit();
    }

    @Override
    public void registerOnClick() {
        registerFragment = new RegisterFragment();
        if ( getSupportFragmentManager().getBackStackEntryCount() > 0 ) getSupportFragmentManager().popBackStack();
        loginActivityBundle.putInt(getResources().getString(R.string.connectivity_status), connectionStatus);
        registerFragment.setArguments(loginActivityBundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.loginActivityFgmtContainer, registerFragment, getResources().getString(R.string.registerFgmt))
                .addToBackStack(getResources().getString(R.string.registerFgmt))
                .commit();
    }

    @Override
    public void signInHereOnClick() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) getSupportFragmentManager().popBackStack();
    }

    public SnackBar getSnackBar() {
        return loginActivitySnkBar;
    }

    public RelativeLayout getErrorContainerRlt() { return errorContainerRlt; }

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
    public boolean validateEditText(EditText[] fields) {
        for (EditText field : fields) {
            if (field.getText().toString().isEmpty()) return false;
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

    @Override
    public void networkStatus(int connectionType) {
        Log.e(debugTag, "listeners: "+connectionType);
        if (connectionType != AppConfig.NO_CONNECTION) if (connectionSettingsDialog != null && connectionSettingsDialog.isShowing()) connectionSettingsDialog.dismiss();
        connectionStatus = connectionType;
        Intent intent = new Intent(getResources().getString(R.string.network_state_update));
        intent.putExtra(getResources().getString(R.string.connectivity_status), connectionStatus);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public void dismissErrorContainerSnackBar(TSnackbar errorContainerSnackbar) {
        if (errorContainerSnackbar != null) this.errorContainerSnackbar = errorContainerSnackbar;
    }
}
