package com.call.colorscreen.ledflash.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;

@SuppressWarnings("NonFinalStaticField")
public class GoogleMobileAdsConsentManager {
    private static GoogleMobileAdsConsentManager instance;
    private final ConsentInformation consentInformation;

    /**
     * Private constructor.
     */
    private GoogleMobileAdsConsentManager(Context context) {
        this.consentInformation = UserMessagingPlatform.getConsentInformation(context);
    }

    /**
     * Public constructor.
     */
    public static GoogleMobileAdsConsentManager getInstance(Context context) {
        if (instance == null) {
            instance = new GoogleMobileAdsConsentManager(context);
        }

        return instance;
    }

    /**
     * Interface definition for a callback to be invoked when consent gathering is complete.
     */
    public interface OnConsentGatheringCompleteListener {
        void consentGatheringComplete(FormError error);

        void conSentShow();

        void conSentDismiss();
    }

    /**
     * Helper variable to determine if the app can request ads.
     */
    public boolean canRequestAds() {
        return consentInformation.canRequestAds();
    }

    /**
     * Helper variable to determine if the privacy options form is required.
     */
    public boolean isPrivacyOptionsRequired() {
        return consentInformation.getPrivacyOptionsRequirementStatus()
                == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED;
    }

    /**
     * Helper method to call the UMP SDK methods to request consent information and load/present a
     * consent form if necessary.
     */
    private boolean isCanreQuestAds;
    private boolean isDismiss;
    private boolean isShow;
    private long time;

    public void gatherConsent(
            Activity activity, OnConsentGatheringCompleteListener onConsentGatheringCompleteListener) {
        isCanreQuestAds = canRequestAds();
        time = System.currentTimeMillis();
        // For testing purposes, you can force a DebugGeography of EEA or NOT_EEA.
        ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(activity)
                //.setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                // Check your logcat output for the hashed device ID e.g.
                // "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345")" to use
                // the debug functionality.
                //.addTestDeviceHashedId("71F8D91E56AD5BA87EED99DF6A589246")
                .build();

        ConsentRequestParameters params = new ConsentRequestParameters.Builder()
                .setConsentDebugSettings(debugSettings)
                .build();
        // Requesting an update to consent information should be called on every app launch.
        consentInformation.requestConsentInfoUpdate(activity,
                params,
                new ConsentInformation.OnConsentInfoUpdateSuccessListener() {
                    @Override
                    public void onConsentInfoUpdateSuccess() {
                        // The consent information state was updated.
                        // You are now ready to check if a form is available.
                        Log.e("TAN", "onConsentInfoUpdateSuccess: "+consentInformation.getConsentStatus()+"##"+consentInformation.getPrivacyOptionsRequirementStatus());
                    }
                },
                new ConsentInformation.OnConsentInfoUpdateFailureListener() {
                    @Override
                    public void onConsentInfoUpdateFailure(FormError formError) {
                        // Handle the error.
                        onConsentGatheringCompleteListener.consentGatheringComplete(formError);
                    }
                });
    }

    public void showForm(Activity activity, OnConsentGatheringCompleteListener onConsentGatheringCompleteListener){
        Log.e("TAN", "showForm: "+consentInformation.isConsentFormAvailable()+"##"+consentInformation.getConsentStatus());
        if (consentInformation.isConsentFormAvailable() && consentInformation.getConsentStatus() == ConsentInformation.ConsentStatus.REQUIRED) {
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                    activity,
                    formError -> {
                        // Consent has been gathered.
                        isDismiss = true;
                        if (isShow) {
                            onConsentGatheringCompleteListener.conSentDismiss();
                        }
                        Log.e("TAN", "showForm: "+onConsentGatheringCompleteListener+"##"+formError);
                        onConsentGatheringCompleteListener.consentGatheringComplete(formError);
                    });
            if (!isCanreQuestAds && !isDismiss) {
                isShow = true;
                Log.e("TAN", "onConsentInfoUpdateSuccess: time để hiển thị là " + (System.currentTimeMillis() - time));
                onConsentGatheringCompleteListener.conSentShow();
            }
        }
    }

    /**
     * Helper method to call the UMP SDK method to present the privacy options form.
     */
    public void showPrivacyOptionsForm(
            Activity activity,
            ConsentForm.OnConsentFormDismissedListener onConsentFormDismissedListener) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, onConsentFormDismissedListener);
    }
}

