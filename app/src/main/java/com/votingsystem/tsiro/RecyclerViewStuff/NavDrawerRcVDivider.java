package com.votingsystem.tsiro.RecyclerViewStuff;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.votingsystem.tsiro.votingsystem.R;

import java.util.HashMap;

/**
 * Created by user on 28/11/2015.
 */
public class NavDrawerRcVDivider extends RecyclerView.ItemDecoration {

    private static final String debugTag = "NavDrawerRcVDivider";
    private Drawable mDivider;
    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;
    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;
    private int mOrientation;
    private HashMap<String, Integer> mNavDrawerMetricsHash;
    /**
     * Default divider will be used
     */
    public NavDrawerRcVDivider(Context context, int drawableId, int mOrientation, HashMap<String, Integer> mNavDrawerMetricsHash){
        mDivider = ContextCompat.getDrawable(context, drawableId);
        setOrientation(mOrientation);
        this.mNavDrawerMetricsHash = mNavDrawerMetricsHash;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if( parent.getChildAdapterPosition(view) == 4 ) outRect.bottom = mNavDrawerMetricsHash.get("vertical spacing");
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent, state);
        } else {
            drawVertical(c, parent, state);
        }
    }

    private void drawVertical(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int childAdapterPosition = parent.getChildAdapterPosition(child);
            if ( childAdapterPosition == 5 ) {
                int top = child.getBottom() - mNavDrawerMetricsHash.get("row height") - mNavDrawerMetricsHash.get("vertical spacing")/2;
                int bottom = top + mDivider.getIntrinsicHeight();
                Log.d(debugTag, "item: " + i + " left: " + String.valueOf(left) + " top: " + String.valueOf(top) + " right: " + String.valueOf(right) + " bottom: " + String.valueOf(bottom));
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
                return;
            }
        }
    }

    private void drawHorizontal(Canvas c, RecyclerView parent, RecyclerView.State state) {
        //Log.d(debugTag, "horizontal");
        int top = parent.getPaddingTop();
        int bottom = parent.getHeight() - parent.getPaddingBottom();

        int childCount = parent.getChildCount();

        //Log.d(debugTag, "child count: "+childCount);
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int childadapterposition = parent.getChildAdapterPosition(child);
            if ( childadapterposition == 5 ) {
                int leftchild = parent.getPaddingLeft();
                int topchild = child.getTop();
                int rightchild = parent.getWidth();
                Log.d(debugTag, " left: " + leftchild + " top: " + topchild + "right: " + rightchild);
                mDivider.setBounds(leftchild, topchild, rightchild, topchild + 3);
                mDivider.draw(c);
                return;
            }
            //Log.d(debugTag,"Child adapter position: "+childadapterposition);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int left = child.getRight() + params.rightMargin;
            int right = left + mDivider.getIntrinsicHeight();
            //Log.d(debugTag, "item: " + i + " left: " + String.valueOf(left) + " top: " + String.valueOf(top) + " right: " + String.valueOf(right) + " bottom: " + String.valueOf(bottom));
            //mDivider.setBounds(left, top, right, bottom);
            //mDivider.draw(c);
        }
    }

    private void setOrientation(int orientation) {
        if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
            throw new IllegalArgumentException("invalid orientation");
        }
        mOrientation = orientation;
    }
}
