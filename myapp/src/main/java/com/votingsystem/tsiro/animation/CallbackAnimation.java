package com.votingsystem.tsiro.animation;

import android.view.animation.Animation;
import android.view.animation.Transformation;
import com.votingsystem.tsiro.interfaces.TransformationListener;

/**
 * Created by giannis on 18/10/2016.
 */

public class CallbackAnimation extends Animation {

    private TransformationListener mListener;

    public CallbackAnimation(TransformationListener listener) {
        mListener = listener;
        if (listener == null) {
            mListener = listener;
        }
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        mListener.onApplyTrans(interpolatedTime);
    }

    public void setListener(TransformationListener listener) {
        if (listener == null) {
            return;
        }
        mListener = listener;
    }
}
