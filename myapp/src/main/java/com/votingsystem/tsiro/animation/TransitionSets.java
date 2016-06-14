package com.votingsystem.tsiro.animation;

import android.annotation.TargetApi;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;

/**
 * Created by giannis on 4/6/2016.
 */
@TargetApi(Build.VERSION_CODES.KITKAT)
public class TransitionSets extends TransitionSet {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TransitionSets() {
        setOrdering(ORDERING_TOGETHER);
            addTransition(new ChangeBounds()).
            addTransition(new ChangeTransform()).
            addTransition(new ChangeImageTransform());
    }
}
