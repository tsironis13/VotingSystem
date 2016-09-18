package com.votingsystem.tsiro.mainClasses;

import android.app.Activity;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import io.codetail.animation.ViewAnimationUtils;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.transition.Explode;
import android.util.Base64;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import com.rey.material.app.BottomSheetDialog;
import com.rey.material.widget.EditText;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.ObserverPattern.NetworkStateListeners;
import com.votingsystem.tsiro.ObserverPattern.RegistrationTokenListeners;
import com.votingsystem.tsiro.ObserverPattern.SoftKeyboardStateWatcher;
import com.votingsystem.tsiro.animation.TransitionSets;
import com.votingsystem.tsiro.app.AppConfig;
import com.votingsystem.tsiro.app.MyApplication;
import com.votingsystem.tsiro.broadcastReceivers.NetworkStateReceiver;
import com.votingsystem.tsiro.broadcastReceivers.RegistrationTokenReceiver;
import com.votingsystem.tsiro.fragments.ForgotPasswordFragment;
import com.votingsystem.tsiro.fragments.RegisterFragment;
import com.votingsystem.tsiro.fragments.SignInFragment;
import com.votingsystem.tsiro.fragments.SplashScreenFragment;
import com.votingsystem.tsiro.interfaces.LoginActivityCommonElementsAndMuchMore;
import com.votingsystem.tsiro.interfaces.SoftKeyboardStateListener;
import com.votingsystem.tsiro.votingsystem.R;
import java.io.UnsupportedEncodingException;
import io.codetail.animation.SupportAnimator;
import io.codetail.widget.RevealFrameLayout;

public class LoginActivity extends AppCompatActivity implements NetworkStateListeners, LoginActivityCommonElementsAndMuchMore, RegistrationTokenListeners, SoftKeyboardStateListener {

    private static final String debugTag = LoginActivity.class.getSimpleName();
    private Bundle loginActivityBundle;
    private int connectionStatus;
    private NetworkStateReceiver networkStateReceiver;
    private RelativeLayout loginActivityContainer;
    private Snackbar loginActivitySnkBar;
    private BottomSheetDialog connectionSettingsDialog;
    private View connectionSettingsDialogView, divider;
    private RegistrationTokenReceiver registrationTokenReceiver;
    private String registrationToken;
    private SparseIntArray inputValidationCodes;
    private SignInFragment signInFgmt;
    private ForgotPasswordFragment forgotPasswordFgmt;
    private RegisterFragment registerFgmt;
    private SplashScreenFragment splashScreenFgmt;
    private boolean animationIsHappening;
    private RevealFrameLayout revealContainer;
    private ImageView themeImgv;
    public static boolean settingsDialogWasOpened = false;
    private SoftKeyboardStateWatcher softKeyboardStateWatcher;
    private LinearLayout toSharedLogo, middleView, bottomView;
    private InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.background_theme));
        setContentView(R.layout.login_activity);

        if (savedInstanceState == null) {
            Log.e(debugTag, "SESSION ID: "+LoginActivity.getSessionPrefs(getApplicationContext()).getInt(getResources().getString(R.string.user_id), 0));
            if (LoginActivity.getSessionPrefs(getApplicationContext()).getInt(getResources().getString(R.string.user_id), 0) != 0) {
                logUserIn(LoginActivity.getSessionPrefs(getApplicationContext()).getInt(getResources().getString(R.string.user_id), 0));
            } else {
                inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                softKeyboardStateWatcher = new SoftKeyboardStateWatcher(findViewById(android.R.id.content));

                inputValidationCodes        =   AppConfig.getCodes();
                networkStateReceiver        =   new NetworkStateReceiver();
                registrationTokenReceiver   =   new RegistrationTokenReceiver();
                loginActivityBundle         =   new Bundle();

                loginActivityContainer      =   (RelativeLayout) findViewById(R.id.loginActivityContainer);
                revealContainer             =   (RevealFrameLayout) findViewById(R.id.revealContainer);
                themeImgv                   =   (ImageView) findViewById(R.id.themeImgv);

                splashScreenFgmt            =   new SplashScreenFragment();
                signInFgmt                  =   new SignInFragment();
                signInFgmt.setArguments(loginActivityBundle);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.splash_loaded), "").equals(getResources().getString(R.string.yes))) {
                    signInFgmt.setSharedElementEnterTransition(new TransitionSets());
                    signInFgmt.setEnterTransition(new Explode());
                }

                if (!PreferenceManager.getDefaultSharedPreferences(this).getString(getResources().getString(R.string.splash_loaded), "").equals(getResources().getString(R.string.yes))) {
                    themeImgv.setBackgroundResource(R.drawable.background_theme);
                    ViewTreeObserver viewTreeObserver = revealContainer.getViewTreeObserver();
                    if (viewTreeObserver.isAlive()) {
                        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                revealTheme();
                                revealContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            }
                        });
                    }
                } else {
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.loginActivityFgmtContainer, signInFgmt, getResources().getString(R.string.signin_fgmt))
                            .addToBackStack(getResources().getString(R.string.signin_fgmt))
                            .commit();
                }
                softKeyboardStateWatcher.addSoftKeyboardStateListener(this);
                networkStateReceiver.addListener(this);
                registrationTokenReceiver.addListener(this);
                this.registerReceiver(networkStateReceiver, new IntentFilter(getResources().getString(R.string.connectivity_change)));
                LocalBroadcastManager.getInstance(this).registerReceiver(registrationTokenReceiver, new IntentFilter(getResources().getString(R.string.registration_token)));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (connectionSettingsDialog != null && connectionSettingsDialog.isShowing()) connectionSettingsDialog.dismiss();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (softKeyboardStateWatcher != null && networkStateReceiver != null && registrationTokenReceiver != null) {
            softKeyboardStateWatcher.removeSoftKeyboardStateListener(this);
            networkStateReceiver.removeListener(this);
            registrationTokenReceiver.removeListener(this);
            this.unregisterReceiver(networkStateReceiver);
            LocalBroadcastManager.getInstance(this).unregisterReceiver(registrationTokenReceiver);
        }
        //RefWatcher refWatcher = MyApplication.getRefWatcher(this);
        //refWatcher.watch(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            String fragmentTag = getTopBackStackFragment();
            if (fragmentTag != null && (fragmentTag.equals(getResources().getString(R.string.splashscreen_fgmt)) || fragmentTag.equals(getResources().getString(R.string.signin_fgmt)))) {
                moveTaskToBack(true);
            } else {
                if (settingsDialogWasOpened) settingsDialogWasOpened = false;
                if (!animationIsHappening) {
                    Fragment fragment = null;
                    if (fragmentTag != null) {
                        switch (fragmentTag) {
                            case "signInFgmt"          :    fragment = signInFgmt;
                                break;
                            case "forgotPasswordFgmt"  :    fragment = forgotPasswordFgmt;
                                break;
                            case "registerFgmt"        :    fragment = registerFgmt;
                                break;
                        }
                        assert fragment != null;
                        if (fragment.getArguments() != null) {
                            loginActivityBundle = fragment.getArguments();
                            loginActivityBundle.putInt(getResources().getString(R.string.connectivity_status), connectionStatus);
                        }
                        getSupportFragmentManager().popBackStack();
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void getRegistrationToken(String token) {
        if (token != null) {
            Log.e(debugTag, token);
            registrationToken = token;
            SharedPreferences.Editor editor = LoginActivity.getSessionPrefs(getApplicationContext()).edit();
            editor.putString(getResources().getString(R.string.registration_token), token);
            editor.apply();
        }
    }

    @Override
    public void setLoginActivitySpan(TextView textView, String stringResource, int start, int end, final int code) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(stringResource);
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {}
        };
        spannableStringBuilder.setSpan(clickableSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(styleSpan, start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        spannableStringBuilder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.accentColor)), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(spannableStringBuilder);
    }

    @Override
    public void forgotPasswordOnClick() {
        if (settingsDialogWasOpened) settingsDialogWasOpened = false;
        forgotPasswordFgmt = new ForgotPasswordFragment();
        loginActivityBundle.putString(getResources().getString(R.string.registration_token), registrationToken);
        forgotPasswordFgmt.setArguments(loginActivityBundle);
        getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                                .replace(R.id.loginActivityFgmtContainer, forgotPasswordFgmt, getResources().getString(R.string.forgotpassword_fgmt))
                                .addToBackStack(getResources().getString(R.string.forgotpassword_fgmt))
                                .commit();
    }

    @Override
    public void registerOnClick() {
        if (toSharedLogo != null && bottomView != null) if (toSharedLogo.getVisibility() == View.GONE || bottomView.getVisibility() == View.GONE) inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        if (settingsDialogWasOpened) settingsDialogWasOpened = false;
        registerFgmt = new RegisterFragment();
        loginActivityBundle.putInt(getResources().getString(R.string.connectivity_status), connectionStatus);
        loginActivityBundle.putString(getResources().getString(R.string.registration_token), registrationToken);
        registerFgmt.setArguments(loginActivityBundle);
        getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                                .replace(R.id.loginActivityFgmtContainer, registerFgmt, getResources().getString(R.string.register_fgmt))
                                .addToBackStack(getResources().getString(R.string.register_fgmt))
                                .commit();
    }

    @Override
    public void signInHereOnClick() {
        if (toSharedLogo != null && bottomView != null) if (toSharedLogo.getVisibility() == View.GONE || bottomView.getVisibility() == View.GONE) inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            loginActivityBundle = signInFgmt.getArguments();
            //Log.e(debugTag, loginActivityBundle+"");
            loginActivityBundle.putInt(getResources().getString(R.string.connectivity_status), connectionStatus);
            getSupportFragmentManager().popBackStack(getResources().getString(R.string.signin_fgmt), 0);
        }
    }

    @Override
    public void showSnackBar(int code) {
        initializeSnackBar(null, null, code);
    }

    @Override
    public String encodeUtf8(String text) {
        return baseEncodeUtf8(text);
    }

    @Override
    public String decodeUtf8(String text) {
       return baseDecodeUtf8(text);
    }

    private String baseEncodeUtf8(String text) {
        try {
            byte[] data = text.getBytes("UTF-8");
            return Base64.encodeToString(data, Base64.DEFAULT);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String baseDecodeUtf8(String text) {
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
        //Log.e(debugTag, "listeners: "+connectionType);
        if (connectionType != AppConfig.NO_CONNECTION) if (connectionSettingsDialog != null && connectionSettingsDialog.isShowing()) connectionSettingsDialog.dismiss();
        connectionStatus = connectionType;
        Intent intent = new Intent(getResources().getString(R.string.network_state_update));
        intent.putExtra(getResources().getString(R.string.connectivity_status), connectionStatus);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public void showErrorContainerSnackbar(final String error_type, View errorView, int code) {
        initializeSnackBar(error_type, errorView, code);
    }

    @Override
    public void setText(String action, View view, String decodedMessage, String color) {
        baseSetText(action, view, decodedMessage, color);
    }

    @Override
    public void animationOccured(boolean ishappening) {
        this.animationIsHappening = ishappening;
    }

    @Override
    public void onSplashScreenAnimationFinish() {
        loginActivityBundle.putInt(getResources().getString(R.string.connectivity_status), connectionStatus);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LinearLayout sharedLogo = (LinearLayout) findViewById(R.id.fromSharedLogo);
            if (sharedLogo != null) fragmentTransaction.addSharedElement(sharedLogo, getResources().getString(R.string.toshared_logo_trns));
        } else {
            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        }
        fragmentTransaction.replace(R.id.loginActivityFgmtContainer, signInFgmt, getResources().getString(R.string.signin_fgmt));
        fragmentTransaction.addToBackStack(getResources().getString(R.string.signin_fgmt));
        fragmentTransaction.commit();

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(getResources().getString(R.string.splash_loaded), getResources().getString(R.string.yes));
        editor.apply();

    }

    @Override
    public void onSoftKeyboardOpened(int keyboardHeightInPx) {
        toSharedLogo    =   (LinearLayout) findViewById(R.id.toSharedLogo);
        bottomView      =   (LinearLayout) findViewById(R.id.bottomView);
        middleView      =   (LinearLayout) findViewById(R.id.middleView);
        if (toSharedLogo != null && bottomView != null) {
            toSharedLogo.setVisibility(View.GONE);
            bottomView.setVisibility(View.GONE);
        }
        if (middleView != null) {
            LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) middleView.getLayoutParams();
            lParams.weight = 100f;
            middleView.setLayoutParams(lParams);
        }
    }

    @Override
    public void onSoftKeyboardClosed() {
        toSharedLogo    =   (LinearLayout) findViewById(R.id.toSharedLogo);
        bottomView      =   (LinearLayout) findViewById(R.id.bottomView);
        middleView      =   (LinearLayout) findViewById(R.id.middleView);
        if (middleView != null) {
            LinearLayout.LayoutParams lParams = (LinearLayout.LayoutParams) middleView.getLayoutParams();
            lParams.weight = 62f;
            middleView.setLayoutParams(lParams);
        }
        if (toSharedLogo != null && bottomView != null) {
            toSharedLogo.setVisibility(View.VISIBLE);
            bottomView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bottomView.setVisibility(View.VISIBLE);
                }
            },100);
        }
    }

    private void logUserIn(int user_id) {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.putExtra(getResources().getString(R.string.user_id), user_id);
        //intent.putExtra(getResources().getString(R.string.username_tag), username);
        //intent.putExtra(getResources().getString(R.string.email_tag), email);
        //intent.putExtra(getResources().getString(R.string.firm_id), firm_id);
        startActivity(intent);
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void initializeSnackBar(String error_type, View errorView, int code) {
        String sbText       =   "";
        boolean hasAction   =   false;
        if (code == AppConfig.NO_CONNECTION || code == AppConfig.UNAVAILABLE_SERVICE || code == AppConfig.INTERNAL_ERROR || code == AppConfig.STATUS_ERROR) {
            sbText    = getResources().getString(inputValidationCodes.get(code));
            hasAction = true;
        } else {
            if (errorView != null) setText(getResources().getString(R.string.error), errorView, baseDecodeUtf8(baseEncodeUtf8(getResources().getString(inputValidationCodes.get(code)))), "#FF5722");
            sbText   = error_type;
        }
        loginActivitySnkBar = Snackbar.make(loginActivityContainer, sbText, Snackbar.LENGTH_LONG);

        if (hasAction) loginActivitySnkBar.setAction(getResources().getString(R.string.more), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectionSettingsDialog        = new BottomSheetDialog(LoginActivity.this, R.style.ConnectionSettingsBottomSheetDialog);
                connectionSettingsDialogView    = LayoutInflater.from(LoginActivity.this).inflate(R.layout.connection_settings_bottom_sheet_dialog, null);
                connectionSettingsDialog.setContentView(connectionSettingsDialogView);
                connectionSettingsDialog.show();
            }
        });

        View sbView = loginActivitySnkBar.getView();
        sbView.setMinimumHeight((int)MyApplication.convertPixelToDpAndViceVersa(this, 0, 80));
        sbView.setPadding(24,24,24,24);
        android.widget.TextView textView = (android.widget.TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextSize(14);
        if (!hasAction) textView.setTextColor(ContextCompat.getColor(this, R.color.accentColor));
        loginActivitySnkBar.show();
    }

    public Snackbar getSnackBar() {
        //Log.e(debugTag, loginActivitySnkBar+"");
        return loginActivitySnkBar;
    }

    public static SharedPreferences getSessionPrefs(Context context) {
        //Mode private so only this app can modify this SharedPreferences file
        return context.getSharedPreferences(AppConfig.SESSION_PREFS, Context.MODE_PRIVATE);
    }

    public void connectionSettingsLltOnClick(View view){
        settingsDialogWasOpened = true;
        startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
    }

    private void revealTheme() {
        int cx  =   themeImgv.getRight();
        int cy  =   themeImgv.getBottom();

        // get the final radius for the clipping circle
        int dx  =   Math.max(cx, themeImgv.getWidth() - cx);
        int dy  =   Math.max(cy, themeImgv.getHeight() - cy);
        final float finalRadius = (float) Math.hypot(dx, dy);

        SupportAnimator mAnimator = ViewAnimationUtils.createCircularReveal(themeImgv, cx, cy, 0 ,finalRadius);
        mAnimator.addListener(new SupportAnimator.AnimatorListener() {
            @Override
            public void onAnimationStart() {}

            @Override
            public void onAnimationEnd() {
//                themeImgv.setBackgroundResource(0);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.loginActivityFgmtContainer, splashScreenFgmt, getResources().getString(R.string.splashscreen_fgmt))
                        .addToBackStack(getResources().getString(R.string.splashscreen_fgmt))
                        .commit();
            }

            @Override
            public void onAnimationCancel() {}

            @Override
            public void onAnimationRepeat() {}
        });
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.setDuration(1500);
        mAnimator.start();
    }

    private void baseSetText(String action, View view, String decodedMessage, String color) {
        if (view instanceof EditText) {
            if (action.equals(getResources().getString(R.string.error))) ((EditText) view).setHelper(Html.fromHtml("<font color=" + color + ">" + decodedMessage + "</font>"));
        } else if ( view instanceof TextView) {
            ((com.rey.material.widget.TextView) view).setText(decodedMessage);
        }
    }

    private String getTopBackStackFragment() {
        return getSupportFragmentManager().getBackStackEntryCount() > 0 ? getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName() : null;
    }

}
