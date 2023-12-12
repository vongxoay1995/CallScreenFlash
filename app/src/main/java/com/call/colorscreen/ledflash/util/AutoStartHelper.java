package com.call.colorscreen.ledflash.util;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.call.colorscreen.ledflash.R;
import com.call.colorscreen.ledflash.analystic.Analystic;
import com.call.colorscreen.ledflash.analystic.Event;
import com.orhanobut.hawk.Hawk;

import java.util.List;

public class AutoStartHelper {
    /***
     * Xiaomi
     */
    private final String BRAND_XIAOMI = "xiaomi";
    private String PACKAGE_XIAOMI_MAIN = "com.miui.securitycenter";
    private String PACKAGE_XIAOMI_COMPONENT = "com.miui.permcenter.autostart.AutoStartManagementActivity";

    /***
     * Letv
     */
    private final String BRAND_LETV = "letv";
    private String PACKAGE_LETV_MAIN = "com.letv.android.letvsafe";
    private String PACKAGE_LETV_COMPONENT = "com.letv.android.letvsafe.AutobootManageActivity";

    /***
     * ASUS ROG
     */
    private final String BRAND_ASUS = "asus";
    private String PACKAGE_ASUS_MAIN = "com.asus.mobilemanager";
    private String PACKAGE_ASUS_COMPONENT = "com.asus.mobilemanager.powersaver.PowerSaverSettings";

    /***
     * Honor
     */
    private final String BRAND_HONOR = "honor";
    private String PACKAGE_HONOR_MAIN = "com.huawei.systemmanager";
    private String PACKAGE_HONOR_COMPONENT = "com.huawei.systemmanager.optimize.process.ProtectActivity";

    /**
     * Oppo
     */
    private final String BRAND_OPPO = "oppo";
    private String PACKAGE_OPPO_MAIN = "com.coloros.safecenter";
    private String PACKAGE_OPPO_FALLBACK = "com.oppo.safe";
    private String PACKAGE_OPPO_COMPONENT = "com.coloros.safecenter.permission.startup.StartupAppListActivity";
    private String PACKAGE_OPPO_COMPONENT_FALLBACK = "com.oppo.safe.permission.startup.StartupAppListActivity";
    private String PACKAGE_OPPO_COMPONENT_FALLBACK_A = "com.coloros.safecenter.startupapp.StartupAppListActivity";

    /**
     * Vivo
     */

    private final String BRAND_VIVO = "vivo";
    private String PACKAGE_VIVO_MAIN = "com.iqoo.secure";
    private String PACKAGE_VIVO_FALLBACK = "com.vivo.perm;issionmanager";
    private String PACKAGE_VIVO_COMPONENT = "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity";
    private String PACKAGE_VIVO_COMPONENT_FALLBACK = "com.vivo.permissionmanager.activity.BgStartUpManagerActivity";
    private String PACKAGE_VIVO_COMPONENT_FALLBACK_A = "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager";

    /**
     * Nokia
     */

    private final String BRAND_NOKIA = "nokia";
    private String PACKAGE_NOKIA_MAIN = "com.evenwell.powersaving.g3";
    private String PACKAGE_NOKIA_COMPONENT = "com.evenwell.powersaving.g3.exception.PowerSaverExceptionActivity";
    private Analystic analystic;
    private AutoStartHelper() {

    }

    public static AutoStartHelper getInstance() {
        return new AutoStartHelper();
    }


    public void getAutoStartPermission(Context context) {
      analystic = Analystic.getInstance(context);

        String build_info = Build.BRAND.toLowerCase();
        switch (build_info) {
            case BRAND_ASUS:
                autoStartAsus(context,build_info);
                break;
            case BRAND_XIAOMI:
            case "redmi":
                autoStartXiaomi(context,build_info);
                break;
            case BRAND_LETV:
                autoStartLetv(context,build_info);
                break;
            case BRAND_HONOR:
                autoStartHonor(context,build_info);
                break;
            case BRAND_OPPO:
                autoStartOppo(context,build_info);
                break;
            case BRAND_VIVO:
                autoStartVivo(context,build_info);
                break;
            case BRAND_NOKIA:
                autoStartNokia(context,build_info);
                break;

        }

    }

    private void autoStartAsus(final Context context, String name) {
        if (isPackageExists(context, PACKAGE_ASUS_MAIN)) {
            showAlert(context, (dialog, which) -> {
                try {
                    analystic.trackEvent(new Event("SHOW_PER_AUTO_START_FROM_"+name, new Bundle()));
                    Hawk.put("auto_start",true);
                    startIntent(context, PACKAGE_ASUS_MAIN, PACKAGE_ASUS_COMPONENT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            });

        }


    }

    private void showAlert(Context context, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(context).setTitle(context.getString(R.string.title_auto_start))
                .setMessage(context.getString(R.string.content_auto_start))
                .setPositiveButton(context.getString(R.string.allow), onClickListener).show().setCancelable(false);
    }

    private void autoStartXiaomi(final Context context, String name) {
        if (isPackageExists(context, PACKAGE_XIAOMI_MAIN)) {
            showAlert(context, (dialog, which) -> {
                try {
                    analystic.trackEvent(new Event("SHOW_PER_AUTO_START_FROM_"+name, new Bundle()));
                    Hawk.put("auto_start",true);
                    startIntent(context, PACKAGE_XIAOMI_MAIN, PACKAGE_XIAOMI_COMPONENT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


        }
    }

    private void autoStartLetv(final Context context, String name) {
        if (isPackageExists(context, PACKAGE_LETV_MAIN)) {
            showAlert(context, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        analystic.trackEvent(new Event("SHOW_PER_AUTO_START_FROM_"+name, new Bundle()));
                        Hawk.put("auto_start",true);
                        startIntent(context, PACKAGE_LETV_MAIN, PACKAGE_LETV_COMPONENT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


        }
    }


    private void autoStartHonor(final Context context, String name) {
        if (isPackageExists(context, PACKAGE_HONOR_MAIN)) {
            showAlert(context, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        analystic.trackEvent(new Event("SHOW_PER_AUTO_START_FROM_"+name, new Bundle()));
                        Hawk.put("auto_start",true);
                        startIntent(context, PACKAGE_HONOR_MAIN, PACKAGE_HONOR_COMPONENT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });


        }
    }

    private void autoStartOppo(final Context context, String name) {
        if (isPackageExists(context, PACKAGE_OPPO_MAIN) || isPackageExists(context, PACKAGE_OPPO_FALLBACK)) {
            showAlert(context, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        analystic.trackEvent(new Event("SHOW_PER_AUTO_START_FROM_"+name, new Bundle()));
                        Hawk.put("auto_start",true);
                        startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT);
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            analystic.trackEvent(new Event("SHOW_PER_AUTO_START_FROM_"+name, new Bundle()));
                            Hawk.put("auto_start",true);
                            startIntent(context, PACKAGE_OPPO_FALLBACK, PACKAGE_OPPO_COMPONENT_FALLBACK);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            try {
                                analystic.trackEvent(new Event("SHOW_PER_AUTO_START_FROM_"+name, new Bundle()));
                                Hawk.put("auto_start",true);
                                startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT_FALLBACK_A);
                            } catch (Exception exx) {
                                exx.printStackTrace();
                            }

                        }

                    }
                }
            });


        }
    }

    private void autoStartVivo(final Context context, String name) {
        if (isPackageExists(context, PACKAGE_VIVO_MAIN) || isPackageExists(context, PACKAGE_VIVO_FALLBACK)) {
            showAlert(context, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        analystic.trackEvent(new Event("SHOW_PER_AUTO_START_FROM_"+name, new Bundle()));
                        Hawk.put("auto_start",true);
                        startIntent(context, PACKAGE_VIVO_MAIN, PACKAGE_VIVO_COMPONENT);
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            analystic.trackEvent(new Event("SHOW_PER_AUTO_START_FROM_"+name, new Bundle()));
                            Hawk.put("auto_start",true);
                            startIntent(context, PACKAGE_VIVO_FALLBACK, PACKAGE_VIVO_COMPONENT_FALLBACK);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            try {
                                analystic.trackEvent(new Event("SHOW_PER_AUTO_START_FROM_"+name, new Bundle()));
                                Hawk.put("auto_start",true);
                                startIntent(context, PACKAGE_VIVO_MAIN, PACKAGE_VIVO_COMPONENT_FALLBACK_A);
                            } catch (Exception exx) {
                                exx.printStackTrace();
                            }

                        }

                    }

                }
            });
        }
    }

    private void autoStartNokia(final Context context, String name) {
        if (isPackageExists(context, PACKAGE_NOKIA_MAIN)) {
            showAlert(context, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    try {
                        analystic.trackEvent(new Event("SHOW_PER_AUTO_START_FROM_"+name, new Bundle()));
                        Hawk.put("auto_start",true);
                        startIntent(context, PACKAGE_NOKIA_MAIN, PACKAGE_NOKIA_COMPONENT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }


    private void startIntent(Context context, String packageName, String componentName) throws Exception {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(packageName, componentName));
            context.startActivity(intent);
        } catch (Exception var5) {
            var5.printStackTrace();
            throw var5;
        }
    }

    private Boolean isPackageExists(Context context, String targetPackage) {
        List<ApplicationInfo> packages;
        PackageManager pm = context.getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo :
                packages) {
            if (packageInfo.packageName.equals(targetPackage)) {
                return true;
            }
        }

        return false;
    }
}
