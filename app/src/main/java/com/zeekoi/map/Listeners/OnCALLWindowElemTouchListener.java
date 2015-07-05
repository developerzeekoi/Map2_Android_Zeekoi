package com.zeekoi.map.Listeners;

/**
 * Created by Zeekoi Technologies Private Ltd. on 5/22/2015.
 */

import android.content.Context;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import com.google.android.gms.maps.model.Marker;

public abstract class OnCALLWindowElemTouchListener implements OnTouchListener {
    private final View view;
    private final Handler handler = new Handler();
    private final Context mcontext;
    private Marker marker;
    private boolean pressed = false;

    public OnCALLWindowElemTouchListener(View view, Context context) {
        this.view = view;
        this.mcontext = context;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    @Override
    public boolean onTouch(View vv, MotionEvent event) {
        int action = event.getActionMasked();
        if (0 <= event.getX() && event.getX() <= view.getWidth() &&
                0 <= event.getY() && event.getY() <= view.getHeight()) {
            if (action == MotionEvent.ACTION_DOWN) {
                onClickConfirmed(view, marker);
            }
        }
        return true;
    }
    /**
     * This is called after a successful click
     */
    protected abstract void onClickConfirmed(View v, Marker marker);
}