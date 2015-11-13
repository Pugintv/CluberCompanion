package com.lendasoft.clubercompanion;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;


/**
 * Created by victorrosas on 11/11/15.
 */
public class ButtonView extends Button {

    private final String TAG_LOG = "ButtonView";

    private boolean mIgnoreMotionEvents = true;

    public ButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Enables or disables the button from responding to touch events.
     *
     * @param ignore If set to true, touch events will be ignored by the button.
     */
    public void setIgnoreMotionEvents(boolean ignore) {
        mIgnoreMotionEvents = ignore;
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        if (mIgnoreMotionEvents) {
            super.onTouchEvent(event);
            return false;
        }

        return super.onTouchEvent(event);
    }


}
