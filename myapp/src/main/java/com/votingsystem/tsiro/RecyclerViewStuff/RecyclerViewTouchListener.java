package com.votingsystem.tsiro.RecyclerViewStuff;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnItemTouchListener;
import android.view.ContextThemeWrapper;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by user on 1/12/2015.
 */
public class RecyclerViewTouchListener implements OnItemTouchListener {

    private GestureDetector gestureDetector;
    private ClickListener clickListener;

    public RecyclerViewTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener){
        this.clickListener = clickListener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) { return true; }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                if ( child != null && clickListener != null ) {
                    clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    };

    @Override
    public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent e) {
        View currentView = recyclerView.findChildViewUnder(e.getX(), e.getY());
        if ( currentView != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
            clickListener.onClick(currentView, recyclerView.getChildAdapterPosition(currentView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView recyclerView, MotionEvent e) {}

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {}

    public interface ClickListener{
        public void onClick(View currentView, int position);
        public void onLongClick(View view, int position);
    }

}
