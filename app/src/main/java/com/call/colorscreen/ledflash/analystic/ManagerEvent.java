package com.call.colorscreen.ledflash.analystic;

import android.os.Bundle;

public class ManagerEvent {
    //splash tracking
    public static Event splashShow(){
        return new Event(EventKey.SPLASH_SHOW,new Bundle());
    }
    //main tracking
    public static Event mainShow(){
        return new Event(EventKey.MAIN_SHOW,new Bundle());
    }

    public static Event mainSettingClick(){
        return new Event(EventKey.MAIN_SETTING_CLICKED,new Bundle());
    }

    public static Event mainDialogOpen(){
        return new Event(EventKey.MAINDIALOG_OPEN,new Bundle());
    }
    public static Event mainDialogVideo(){
        return new Event(EventKey.MAINDIALOG_VIDEOS_CLICKED,new Bundle());
    }
    public static Event mainDialogPicture(){
        return new Event(EventKey.MAINDIALOG_IMAGES_CLICKED,new Bundle());
    }

    //apply tracking
    public static Event applyShow(){
        return new Event(EventKey.APPLY_SHOW,new Bundle());
    }

    public static Event applyVideoViewError(int what,int extra){
        Bundle bundle =  new Bundle();
        bundle.putInt("what",what);
        bundle.putInt("extra",extra);
        return new Event("ApplyVideoViewError",bundle);
    }
    public static Event applyVideoThemeSelected(String name) {
        Bundle bundle = new Bundle();
        bundle.putString("Video_Theme_selected",name);
        return new Event("Video_Theme_Apply",bundle);
    }
    public static Event applyApplyClick(){
        return new Event(EventKey.APPLY_APPLY_CLICKED,new Bundle());
    }
    public static Event applyBackClick(){
        return new Event(EventKey.APPLY_BACK_CLICKED,new Bundle());
    }
    public static Event applyDeleteClick(){
        return new Event(EventKey.APPLY_DELETE_CLICKED,new Bundle());
    }
    public static Event applyContactClick(){
        return new Event(EventKey.APPLY_CONTACT_CLICKED,new Bundle());
    }

    //setting
    public static Event settingShow(){
        return new Event(EventKey.SETTING_SHOW,new Bundle());
    }

    public static Event settingBackClick(){
        return new Event(EventKey.SETTING_BACK_CLICKED,new Bundle());
    }

    public static Event settingPolicyClick(){
        return new Event(EventKey.SETTING_POLICY_CLICKED,new Bundle());
    }
    public static Event settingFlashClick(){
        return new Event(EventKey.SETTING_FLASH_CLICKED,new Bundle());
    }
    public static Event settingRateClick(){
        return new Event(EventKey.SETTING_RATE_CLICKED,new Bundle());
    }
    public static Event settingShareAppClick(){
        return new Event(EventKey.SETTING_SHAREAPP_CLICKED,new Bundle());
    }

    //call screen show
    public static Event callshow(){
        return new Event(EventKey.CALL_SHOW,new Bundle());
    }
    public static Event callAcceptCall(){
        return new Event(EventKey.CALL_ACCETP_CALL,new Bundle());
    }
    public static Event callRejectCall(){
        return new Event(EventKey.CALL_REJECT_CALL,new Bundle());
    }

    public static Event callVideoViewError(int what,int extra){
        Bundle bundle =  new Bundle();
        bundle.putInt("what",what);
        bundle.putInt("extra",extra);
        return new Event("CallVideoViewError",bundle);
    }
}
