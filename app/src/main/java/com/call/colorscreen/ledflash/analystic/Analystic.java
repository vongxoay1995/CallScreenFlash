package com.call.colorscreen.ledflash.analystic;

import android.content.Context;

import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.analytics.FirebaseAnalytics;

public class Analystic {
    private FirebaseAnalytics mFirebaseAnalytics;
    public static Analystic mAnalytics;

    private Analystic(Context context) {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public static Analystic getInstance(Context context) {
        synchronized (Analystic.class) {
            if (mAnalytics == null) {
                mAnalytics = new Analystic(context);
            }
        }
        return mAnalytics;
    }
    public void trackEvent(Event event){
        mFirebaseAnalytics.logEvent(event.getKey(), event.getBundleValue());
    }
}
