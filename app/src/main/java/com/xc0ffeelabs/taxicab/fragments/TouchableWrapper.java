package com.xc0ffeelabs.taxicab.fragments;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchableWrapper extends FrameLayout {

    private OnTouchEvent mListener;

    public TouchableWrapper(Context context) {
        super(context);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mListener.onMapTouchDown();
                break;

            case MotionEvent.ACTION_UP:
                mListener.onMapTouchUp();
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    public interface OnTouchEvent {
        void onMapTouchDown();
        void onMapTouchUp();
    }

    public void setOnTouchEvent(OnTouchEvent listener) {
        mListener = listener;
    }
}
