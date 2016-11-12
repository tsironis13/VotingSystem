package com.votingsystem.tsiro.animation;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;

/**
 * Created by giannis on 6/11/2016.
 */

public class AnimationUtils {

    public static void animate(RecyclerView.ViewHolder holder, boolean goesDown) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(holder.itemView, "translationY", goesDown?300:-300, 0);
        animator.setDuration(600);
        animator.start();
    }

}
