package com.zeekoi.map.Listeners;

/**
 * Created by Zeekoi Technologies Private Ltd. on 5/22/2015.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import com.google.android.gms.maps.model.Marker;
import com.zeekoi.map.Managers.SessionManager;

public abstract class OnInfoWindowElemTouchListener implements OnTouchListener {
    private final View view;
    private final Drawable bgDrawableNormal;
    private final Drawable bgDrawablePressed;
    private final Handler handler = new Handler();
    private final Context mcontext;
    private SessionManager session;

    private Marker marker;
    private boolean pressed = false;

    public OnInfoWindowElemTouchListener(View view, Drawable bgDrawableNormal, Drawable bgDrawablePressed, Context context) {

        this.view = view;
        this.bgDrawableNormal = bgDrawableNormal;
        this.bgDrawablePressed = bgDrawablePressed;
        this.mcontext = context;
        System.out.println("clickeddd");
        session = new SessionManager(mcontext);
        if (session.getClick() == null) ;
        {
            session.setCLick("1");
        }
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    @Override
    public boolean onTouch(View vv, MotionEvent event) {

        if (0 <= event.getX() && event.getX() <= view.getWidth() &&
                0 <= event.getY() && event.getY() <= view.getHeight()) {

            if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                if (session.getClick().equals("1")) {
                    System.out.println("session.getClick " + session.getClick());
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        view.setBackgroundDrawable(bgDrawablePressed);
                    } else {
                        view.setBackground(bgDrawablePressed);
                    }
                    if (marker != null)
                        marker.showInfoWindow();
                    session.setCLick("0");
                    onClickConfirmed(view, marker);
                } else {
                    System.out.println("session.getClick " + session.getClick());
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        view.setBackgroundDrawable(bgDrawableNormal);
                    } else {
                        view.setBackground(bgDrawableNormal);
                    }
                    if (marker != null)
                        marker.showInfoWindow();
                    session.setCLick("1");
                    onClickConfirmed(view, marker);
                }
            }

        }
        return true;
    }

    /**
     * This is called after a successful click
     */
    protected abstract void onClickConfirmed(View v, Marker marker);
}