package com.votingsystem.tsiro.animation;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.animation.Animation;

import com.votingsystem.tsiro.fragments.SplashScreenFragment;
import com.votingsystem.tsiro.interfaces.LoginActivityCommonElementsAndMuchMore;
import com.votingsystem.tsiro.interfaces.SplashScreenAnimationCallback;

import java.util.Objects;

/**
 * Created by giannis on 2/6/2016.
 */
public class AnimationListener extends Animation implements Animation.AnimationListener {

    private LoginActivityCommonElementsAndMuchMore commonElements;
    private SplashScreenAnimationCallback splashScreenAnimationCallback;
    private int type;
    private String animationAction;

    public AnimationListener(LoginActivityCommonElementsAndMuchMore commonElements, Fragment fragment, String fragmentType, String animationAction) {
        this.commonElements = commonElements;
        switch (fragmentType) {
            case "splashScreenFgmt"     :   type = 1;
                                            this.splashScreenAnimationCallback  =   (SplashScreenAnimationCallback) fragment;
                                            this.animationAction                =   animationAction;
                                            break;
            case "signInFgmt"           :   type = 2;
                                            break;
            case "forgotPasswordFgmt"   :   type = 3;
                                            break;
            case "registerFgmt"         :   type = 4;
                                            break;
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
        if (this.commonElements != null && type != 1) commonElements.animationOccured(true);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {}

    @Override
    public void onAnimationEnd(Animation animation) {
        if (this.commonElements != null && type != 1) commonElements.animationOccured(false);
        if (this.splashScreenAnimationCallback != null && type == 1) {
            if (animationAction.equals("middle")) {
                splashScreenAnimationCallback.onAnimationEnd(true, "middle");
            } else {
                splashScreenAnimationCallback.onAnimationEnd(true, "final");
            }
        }
    }
}
