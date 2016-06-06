package com.votingsystem.tsiro.mainClasses;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.transition.Fade;
import android.util.Base64;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import com.androidadvance.topsnackbar.TSnackbar;
import com.rey.material.app.BottomSheetDialog;
import com.rey.material.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.rey.material.widget.ProgressView;
import com.rey.material.widget.TextView;
import com.rey.material.widget.SnackBar;
import com.squareup.leakcanary.RefWatcher;
import com.votingsystem.tsiro.ObserverPattern.NetworkStateListeners;
import com.votingsystem.tsiro.ObserverPattern.RegistrationTokenListeners;
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
import com.votingsystem.tsiro.services.gcm.RegistrationIntentService;
import com.votingsystem.tsiro.votingsystem.R;
import java.io.UnsupportedEncodingException;
import io.codetail.animation.SupportAnimator;
import io.codetail.widget.RevealFrameLayout;

public class LoginActivity extends AppCompatActivity implements NetworkStateListeners, LoginActivityCommonElementsAndMuchMore, RegistrationTokenListeners {

    private static final String debugTag = LoginActivity.class.getSimpleName();
    private Bundle loginActivityBundle;
    private int connectionStatus;
    private NetworkStateReceiver networkStateReceiver;
    private RelativeLayout errorContainerRlt;
    private SnackBar loginActivitySnkBar;
    private BottomSheetDialog connectionSettingsDialog;
    private View connectionSettingsDialogView;
    private TSnackbar errorContainerSnackbar;
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
    private SharedPreferences sp;
    public static boolean settingsDialogWasOpened = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        if (savedInstanceState == null) {
            sp = getSessionPrefs(this);

            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
            inputValidationCodes        =   AppConfig.getCodes();
            networkStateReceiver        =   new NetworkStateReceiver();
            registrationTokenReceiver   =   new RegistrationTokenReceiver();
            loginActivityBundle         =   new Bundle();

            revealContainer             =   (RevealFrameLayout) findViewById(R.id.revealContainer);
            themeImgv                   =   (ImageView) findViewById(R.id.themeImgv);
            errorContainerRlt           =   (RelativeLayout) findViewById(R.id.errorContainerRlt);
            loginActivitySnkBar         =   (SnackBar) findViewById(R.id.loginActivitySnkBar);

            splashScreenFgmt            =   new SplashScreenFragment();
            signInFgmt                  =   new SignInFragment();
            signInFgmt.setArguments(loginActivityBundle);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !sp.getString(getResources().getString(R.string.splash_loaded), "").equals(getResources().getString(R.string.yes))) {
                signInFgmt.setSharedElementEnterTransition(new TransitionSets());
                signInFgmt.setEnterTransition(new Explode());
            }

            if (!sp.getString(getResources().getString(R.string.splash_loaded), "").equals(getResources().getString(R.string.yes))) {
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
                                    .replace(R.id.loginActivityFgmtContainer, signInFgmt, getResources().getString(R.string.signInFgmt))
                                    .addToBackStack(getResources().getString(R.string.signInFgmt))
                                    .commit();
            }
            networkStateReceiver.addListener(this);
            registrationTokenReceiver.addListener(this);
            this.registerReceiver(networkStateReceiver, new IntentFilter(getResources().getString(R.string.connectivity_change)));
            LocalBroadcastManager.getInstance(this).registerReceiver(registrationTokenReceiver, new IntentFilter(getResources().getString(R.string.registration_token)));
            getStatusBarHeight();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loginActivitySnkBar.actionClickListener(new SnackBar.OnActionClickListener() {
            @Override
            public void onActionClick(SnackBar sb, int actionId) {
                connectionSettingsDialog        = new BottomSheetDialog(LoginActivity.this, R.style.ConnectionSettingsBottomSheetDialog);
                connectionSettingsDialogView    = LayoutInflater.from(LoginActivity.this).inflate(R.layout.connection_settings_bottom_sheet_dialog, null);
                connectionSettingsDialog.setContentView(connectionSettingsDialogView);
                connectionSettingsDialog.show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        networkStateReceiver.removeListener(this);
        registrationTokenReceiver.removeListener(this);
        this.unregisterReceiver(networkStateReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(registrationTokenReceiver);
        //RefWatcher refWatcher = MyApplication.getRefWatcher(this);
        //refWatcher.watch(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            baseDismissErrorContainerSnackBar();
            String fragmentTag = getTopBackStackFragment();
            if (fragmentTag != null && (fragmentTag.equals(getResources().getString(R.string.splashScreenFgmt)) || fragmentTag.equals(getResources().getString(R.string.signInFgmt)))) {
                Log.e(debugTag, "HERE");
                this.finish();
            } else {
                if (settingsDialogWasOpened) settingsDialogWasOpened = false;
                if (!animationIsHappening) {
                    assert fragmentTag != null;
                    Fragment fragment = null;
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
        return true;
    }

    public void connectionSettingsLltOnClick(View view){
        settingsDialogWasOpened = true;
        if (connectionSettingsDialog != null && connectionSettingsDialog.isShowing()) connectionSettingsDialog.dismiss();
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
                getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.loginActivityFgmtContainer, splashScreenFgmt, getResources().getString(R.string.splashScreenFgmt))
                                    .addToBackStack(getResources().getString(R.string.splashScreenFgmt))
                                    .commit();
            }

            @Override
            public void onAnimationCancel() {}

            @Override
            public void onAnimationRepeat() {}
        });
        mAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimator.setDuration(1000);
        mAnimator.start();
    }

    @Override
    public void getRegistrationToken(String token) {
        //Log.e(debugTag, token);
        if (token != null) registrationToken = token;
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
        spannableStringBuilder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getApplicationContext(), R.color.loginSpanColor)), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(spannableStringBuilder);
    }

    @Override
    public void forgotPasswordOnClick() {
        if (settingsDialogWasOpened) settingsDialogWasOpened = false;
        baseDismissErrorContainerSnackBar();
        forgotPasswordFgmt = new ForgotPasswordFragment();
        loginActivityBundle.putInt(getResources().getString(R.string.connectivity_status), connectionStatus);
        loginActivityBundle.putString(getResources().getString(R.string.registration_token), registrationToken);
        forgotPasswordFgmt.setArguments(loginActivityBundle);
        getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                                .replace(R.id.loginActivityFgmtContainer, forgotPasswordFgmt, getResources().getString(R.string.forgotPasswordFgmt))
                                .addToBackStack(getResources().getString(R.string.forgotPasswordFgmt))
                                .commit();
    }

    @Override
    public void registerOnClick() {
        if (settingsDialogWasOpened) settingsDialogWasOpened = false;
        baseDismissErrorContainerSnackBar();
        registerFgmt = new RegisterFragment();
        loginActivityBundle.putInt(getResources().getString(R.string.connectivity_status), connectionStatus);
        loginActivityBundle.putString(getResources().getString(R.string.registration_token), registrationToken);
        registerFgmt.setArguments(loginActivityBundle);
        getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit)
                                .replace(R.id.loginActivityFgmtContainer, registerFgmt, getResources().getString(R.string.registerFgmt))
                                .addToBackStack(getResources().getString(R.string.registerFgmt))
                                .commit();
    }

    @Override
    public void signInHereOnClick() {
        baseDismissErrorContainerSnackBar();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            loginActivityBundle = signInFgmt.getArguments();
            Log.e(debugTag, loginActivityBundle+"");
            loginActivityBundle.putInt(getResources().getString(R.string.connectivity_status), connectionStatus);
            getSupportFragmentManager().popBackStack(getResources().getString(R.string.signInFgmt), 0);
        }
    }

    @Override
    public void showSnackBar(int code) {
        loginActivitySnkBar.actionText("");
        if (code == AppConfig.NO_CONNECTION) {
            loginActivitySnkBar
                            .text(getResources().getString(R.string.no_connection))
                            .applyStyle(R.style.SnackBarNoConnection);
        } else if (code == AppConfig.UNAVAILABLE_SERVICE) {
            loginActivitySnkBar
                            .text(getResources().getString(R.string.unavailable_service))
                            .applyStyle(R.style.SnackBarUnavailableService);
        } else if (code == AppConfig.INTERNAL_ERROR || code == AppConfig.STATUS_ERROR) {
            loginActivitySnkBar
                            .text(getResources().getString(R.string.error_occured))
                            .applyStyle(R.style.SnackBarInternalError);
        }
        loginActivitySnkBar.show();
    }

    public SnackBar getSnackBar() {
        return loginActivitySnkBar;
    }

    public static SharedPreferences getSessionPrefs(Context context) {
        //Mode private so only this app can modify this SharedPreferences file
        return context.getSharedPreferences(AppConfig.SESSION_PREFS, Context.MODE_PRIVATE);
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
        Log.e(debugTag, "listeners: "+connectionType);
        if (connectionType != AppConfig.NO_CONNECTION) if (connectionSettingsDialog != null && connectionSettingsDialog.isShowing()) connectionSettingsDialog.dismiss();
        connectionStatus = connectionType;
        Intent intent = new Intent(getResources().getString(R.string.network_state_update));
        intent.putExtra(getResources().getString(R.string.connectivity_status), connectionStatus);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    @Override
    public void showErrorContainerSnackbar(final String error_type, View errorView, int code) {
        if (errorView != null) setText(getResources().getString(R.string.error), errorView, baseDecodeUtf8(baseEncodeUtf8(getResources().getString(inputValidationCodes.get(code)))), "#DD2C00");
        errorContainerSnackbar = TSnackbar.make(errorContainerRlt, error_type, TSnackbar.LENGTH_INDEFINITE);
        View snackBarView = errorContainerSnackbar.getView();
        snackBarView.setBackgroundColor(Color.parseColor(getResources().getString(R.string.errorContainer_Snackbar_background_color)));
        android.widget.TextView snackBarTtv = (android.widget.TextView) snackBarView.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        snackBarTtv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        snackBarTtv.setTextColor(Color.parseColor(getResources().getString(R.string.errorContainer_Snackbar_Ttv_color)));
        errorContainerSnackbar.show();
    }

    @Override
    public void dismissErrorContainerSnackBar() {
        baseDismissErrorContainerSnackBar();
    }

    private void baseDismissErrorContainerSnackBar() {
        if (errorContainerSnackbar != null && errorContainerSnackbar.isShown()) errorContainerSnackbar.dismiss();
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
            ImageView sharedLogo = (ImageView) findViewById(R.id.fromSharedLogo);
            if (sharedLogo != null) fragmentTransaction.addSharedElement(sharedLogo, getResources().getString(R.string.tosharedlogoTrns));
        } else {
            fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        }
        fragmentTransaction.replace(R.id.loginActivityFgmtContainer, signInFgmt, getResources().getString(R.string.signInFgmt));
        fragmentTransaction.addToBackStack(getResources().getString(R.string.signInFgmt));
        fragmentTransaction.commit();

        SharedPreferences.Editor editor = sp.edit();
        editor.putString(getResources().getString(R.string.splash_loaded), getResources().getString(R.string.yes));
        editor.apply();

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

    private void getStatusBarHeight() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            errorContainerRlt.setPadding(0, Math.round(25 * getResources().getDisplayMetrics().density + 0.5f), 0 ,0);
        } else {
            errorContainerRlt.setPadding(0, Math.round(24 * getResources().getDisplayMetrics().density + 0.5f), 0 ,0);
        }
    }
}
