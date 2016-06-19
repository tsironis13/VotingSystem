package com.votingsystem.tsiro.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;;import com.rey.material.widget.TextView;
import com.votingsystem.tsiro.animation.AnimationListener;
import com.votingsystem.tsiro.interfaces.LoginActivityCommonElementsAndMuchMore;
import com.votingsystem.tsiro.interfaces.SplashScreenAnimationCallback;
import com.votingsystem.tsiro.votingsystem.R;

/**
 * Created by giannis on 1/6/2016.
 */
public class SplashScreenFragment extends Fragment implements SplashScreenAnimationCallback {

    private static final String debugTag = SignInFragment.class.getSimpleName();
    private View view;
    private LinearLayout fromSharedLogo;
    private TextView welcomeTtV;
    private LoginActivityCommonElementsAndMuchMore commonElements;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if ( context instanceof Activity) this.commonElements = (LoginActivityCommonElementsAndMuchMore) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if ( view == null ) view = inflater.inflate(R.layout.fragment_splashscreen, container, false);
        fromSharedLogo  =   (LinearLayout) view.findViewById(R.id.fromSharedLogo);
        welcomeTtV      =   (TextView) view.findViewById(R.id.welcomeText);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) fromSharedLogo.setTransitionName(getResources().getString(R.string.fromshared_logo_trns));
        initializeSplashscreenAnimations();
    }

    private void initializeSplashscreenAnimations() {
        Animation mLoadAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in);
        mLoadAnimation.setDuration(1300);
        mLoadAnimation.setAnimationListener(new AnimationListener(null, this, getResources().getString(R.string.splashscreen_fgmt), getResources().getString(R.string.middle_animation)));
        fromSharedLogo.startAnimation(mLoadAnimation);
    }

    @Override
    public void onAnimationEnd(boolean status, String action) {
        if (action.equals(getResources().getString(R.string.middle_animation))) {
            welcomeTtV.setText(commonElements.decodeUtf8(commonElements.encodeUtf8(getResources().getString(R.string.welcome_text))));
            Animation slideAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
            slideAnimation.setDuration(1000);
            slideAnimation.setAnimationListener(new AnimationListener(null, this, getResources().getString(R.string.splashscreen_fgmt), getResources().getString(R.string.final_animation)));
            welcomeTtV.startAnimation(slideAnimation);
        } else {
            this.commonElements.onSplashScreenAnimationFinish();
        }

    }
}


