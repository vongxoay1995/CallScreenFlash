package com.call.colorscreen.ledflash.ui.setting;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.SwitchCompat;
import androidx.databinding.ViewDataBinding;
import com.call.colorscreen.ledflash.ui.setting.SettingActivity;
import com.call.screen.themes.color.phone.flashlight.R;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.play.core.review.ReviewInfo;
import com.orhanobut.hawk.Hawk;
import defpackage.ff0;
import defpackage.of0;
import java.util.concurrent.Executor;

/* loaded from: classes.dex */
public final class SettingActivity extends qa0<hb0> implements View.OnClickListener, nf0 {
    public static final /* synthetic */ int D = 0;
    public NativeAd E;
    public com.facebook.ads.NativeAd F;
    public LinearLayout G;
    public boolean H;
    public boolean I;
    public boolean J;
    public oa0 K;
    public fv6 L;
    public int M = 0;

    @Override // defpackage.qa0
    public int G() {
        return R.layout.activity_setting;
    }

    @Override // defpackage.qa0
    public void H() {
    }

    @Override // defpackage.qa0
    public void I(Bundle bundle) {
        RelativeLayout relativeLayout = F().q;
        ix7.e(relativeLayout, "binding.layoutHeader");
        ff0.a.j(this, relativeLayout);
        SwitchCompat switchCompat = F().x;
        Object obj = Hawk.get("KEY_ON_OFF", Boolean.FALSE);
        ix7.e(obj, "get(KEY_ON_OFF, false)");
        switchCompat.setChecked(((Boolean) obj).booleanValue());
        SwitchCompat switchCompat2 = F().y;
        Object obj2 = Hawk.get("KEY_FLASH", Boolean.FALSE);
        ix7.e(obj2, "get(KEY_FLASH, false)");
        switchCompat2.setChecked(((Boolean) obj2).booleanValue());
        F().x.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: ke0
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                SettingActivity settingActivity = SettingActivity.this;
                int i = SettingActivity.D;
                ix7.f(settingActivity, "this$0");
                settingActivity.I = z;
                if (settingActivity.J) {
                    settingActivity.J = false;
                } else {
                    of0.a.a(settingActivity, settingActivity);
                }
            }
        });
        F().y.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: re0
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public final void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                SettingActivity settingActivity = SettingActivity.this;
                int i = SettingActivity.D;
                ix7.f(settingActivity, "this$0");
                settingActivity.H = z;
                of0.a aVar = of0.a;
                of0.a aVar2 = of0.a;
                String[] strArr = of0.b;
                ix7.f(strArr, "permission");
                int length = strArr.length;
                boolean z2 = false;
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        z2 = true;
                        break;
                    }
                    String str = strArr[i2];
                    StringBuilder sb = new StringBuilder();
                    sb.append("checkPermission: ");
                    sb.append(str);
                    sb.append("--");
                    ix7.c(settingActivity);
                    sb.append(u9.a(settingActivity, str));
                    Log.e("TAN", sb.toString());
                    if (u9.a(settingActivity, str) != 0) {
                        break;
                    }
                    i2++;
                }
                if (z2) {
                    settingActivity.K();
                    return;
                }
                ix7.c(settingActivity);
                c9.c(settingActivity, strArr, 4);
            }
        });
        F().p.setOnClickListener(this);
        F().u.setOnClickListener(this);
        F().v.setOnClickListener(this);
        F().w.setOnClickListener(this);
        F().t.setOnClickListener(this);
        new AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110").forNativeAd(new NativeAd.OnNativeAdLoadedListener() { // from class: ne0
            @Override // com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener
            public final void onNativeAdLoaded(NativeAd nativeAd) {
                SettingActivity settingActivity = SettingActivity.this;
                int i = SettingActivity.D;
                ix7.f(settingActivity, "this$0");
                ix7.f(nativeAd, "nativeAd");
                if (settingActivity.isDestroyed() || settingActivity.isFinishing() || settingActivity.isChangingConfigurations()) {
                    nativeAd.destroy();
                    return;
                }
                nativeAd.destroy();
                settingActivity.E = nativeAd;
                View inflate = settingActivity.getLayoutInflater().inflate(R.layout.ad_unified, (ViewGroup) null);
                ix7.d(inflate, "null cannot be cast to non-null type com.google.android.gms.ads.nativead.NativeAdView");
                NativeAdView nativeAdView = (NativeAdView) inflate;
                ix7.f(nativeAd, "nativeAd");
                ix7.f(nativeAdView, "adView");
                View findViewById = nativeAdView.findViewById(R.id.ad_media);
                ix7.d(findViewById, "null cannot be cast to non-null type com.google.android.gms.ads.nativead.MediaView");
                nativeAdView.setMediaView((MediaView) findViewById);
                nativeAdView.setHeadlineView(nativeAdView.findViewById(R.id.ad_headline));
                nativeAdView.setBodyView(nativeAdView.findViewById(R.id.ad_body));
                nativeAdView.setCallToActionView(nativeAdView.findViewById(R.id.ad_call_to_action));
                nativeAdView.setIconView(nativeAdView.findViewById(R.id.ad_app_icon));
                nativeAdView.setPriceView(nativeAdView.findViewById(R.id.ad_price));
                nativeAdView.setStarRatingView(nativeAdView.findViewById(R.id.ad_stars));
                nativeAdView.setStoreView(nativeAdView.findViewById(R.id.ad_store));
                nativeAdView.setAdvertiserView(nativeAdView.findViewById(R.id.ad_advertiser));
                View headlineView = nativeAdView.getHeadlineView();
                ix7.d(headlineView, "null cannot be cast to non-null type android.widget.TextView");
                ((TextView) headlineView).setText(nativeAd.getHeadline());
                MediaView mediaView = nativeAdView.getMediaView();
                ix7.c(mediaView);
                mediaView.setMediaContent(nativeAd.getMediaContent());
                if (nativeAd.getBody() == null) {
                    nativeAdView.getBodyView().setVisibility(4);
                } else {
                    nativeAdView.getBodyView().setVisibility(0);
                    View bodyView = nativeAdView.getBodyView();
                    ix7.d(bodyView, "null cannot be cast to non-null type android.widget.TextView");
                    ((TextView) bodyView).setText(nativeAd.getBody());
                }
                if (nativeAd.getCallToAction() == null) {
                    nativeAdView.getCallToActionView().setVisibility(4);
                } else {
                    nativeAdView.getCallToActionView().setVisibility(0);
                    View callToActionView = nativeAdView.getCallToActionView();
                    ix7.d(callToActionView, "null cannot be cast to non-null type android.widget.Button");
                    ((Button) callToActionView).setText(nativeAd.getCallToAction());
                }
                ng2 ng2Var = (ng2) nativeAd;
                if (ng2Var.c == null) {
                    nativeAdView.getIconView().setVisibility(8);
                } else {
                    View iconView = nativeAdView.getIconView();
                    ix7.d(iconView, "null cannot be cast to non-null type android.widget.ImageView");
                    mg2 mg2Var = ng2Var.c;
                    ix7.c(mg2Var);
                    ((ImageView) iconView).setImageDrawable(mg2Var.b);
                    View iconView2 = nativeAdView.getIconView();
                    ix7.d(iconView2, "null cannot be cast to non-null type android.widget.ImageView");
                    ((ImageView) iconView2).setVisibility(0);
                }
                if (nativeAd.getPrice() == null) {
                    nativeAdView.getPriceView().setVisibility(4);
                } else {
                    nativeAdView.getPriceView().setVisibility(0);
                    View priceView = nativeAdView.getPriceView();
                    ix7.d(priceView, "null cannot be cast to non-null type android.widget.TextView");
                    ((TextView) priceView).setText(nativeAd.getPrice());
                }
                if (nativeAd.getStore() == null) {
                    nativeAdView.getStoreView().setVisibility(4);
                } else {
                    nativeAdView.getStoreView().setVisibility(0);
                    View storeView = nativeAdView.getStoreView();
                    ix7.d(storeView, "null cannot be cast to non-null type android.widget.TextView");
                    ((TextView) storeView).setText(nativeAd.getStore());
                }
                if (nativeAd.getStarRating() == null) {
                    nativeAdView.getStarRatingView().setVisibility(4);
                } else {
                    View starRatingView = nativeAdView.getStarRatingView();
                    ix7.d(starRatingView, "null cannot be cast to non-null type android.widget.RatingBar");
                    ((RatingBar) starRatingView).setRating((float) nativeAd.getStarRating().doubleValue());
                    nativeAdView.getStarRatingView().setVisibility(0);
                }
                if (nativeAd.getAdvertiser() == null) {
                    nativeAdView.getAdvertiserView().setVisibility(4);
                } else {
                    View advertiserView = nativeAdView.getAdvertiserView();
                    ix7.d(advertiserView, "null cannot be cast to non-null type android.widget.TextView");
                    ((TextView) advertiserView).setText(nativeAd.getAdvertiser());
                    nativeAdView.getAdvertiserView().setVisibility(0);
                }
                nativeAdView.setNativeAd(nativeAd);
                settingActivity.F().s.removeAllViews();
                settingActivity.F().s.addView(nativeAdView);
            }
        }).withAdListener(new te0(this)).build().loadAd(new AdRequest.Builder().build());
        oa0 a = oa0.a(this);
        ix7.e(a, "getInstance(this)");
        this.K = a;
        a.b.b.b(null, "SETTING_SHOW", new Bundle(), false, true, null);
    }

    public final void J() {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void K() {
        Hawk.put("KEY_FLASH", Boolean.valueOf(this.H));
    }

    public final void L() {
        this.J = true;
        F().x.setChecked(true ^ this.I);
    }

    @Override // defpackage.nf0
    public void o() {
        Hawk.put("KEY_ON_OFF", Boolean.valueOf(this.I));
    }

    @Override // defpackage.wg, androidx.activity.ComponentActivity, android.app.Activity
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        Log.e("TAN", "onActivityResult: aaaaaaaa");
        if (!ff0.a.a(this) || !ff0.a.d(this)) {
            this.J = true;
            F().x.setChecked(false);
            new Handler().postDelayed(new Runnable() { // from class: oe0
                @Override // java.lang.Runnable
                public final void run() {
                    SettingActivity settingActivity = SettingActivity.this;
                    int i3 = SettingActivity.D;
                    ix7.f(settingActivity, "this$0");
                    settingActivity.J = false;
                }
            }, 100L);
        }
        if (i != 1) {
            if (i == 2 && ff0.a.d(this)) {
                this.I = true;
                F().x.setChecked(true);
                o();
            }
        } else if (!ff0.a.a(this) || ff0.a.d(this)) {
        } else {
            this.I = true;
            L();
            Log.e("TAN", "onActivityResult: showNotificationAccess");
            ff0.a.m(this);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        Integer valueOf = view != null ? Integer.valueOf(view.getId()) : null;
        if (valueOf != null && valueOf.intValue() == R.id.btnBack) {
            oa0 oa0Var = this.K;
            if (oa0Var == null) {
                ix7.l("analystic");
                throw null;
            }
            oa0Var.b.b.b(null, "SETTING_BACK_CLICKED", new Bundle(), false, true, null);
            finish();
        } else if (valueOf != null && valueOf.intValue() == R.id.llShare) {
            oa0 oa0Var2 = this.K;
            if (oa0Var2 == null) {
                ix7.l("analystic");
                throw null;
            }
            oa0Var2.b.b.b(null, "SETTING_SHAREAPP_CLICKED", new Bundle(), false, true, null);
            Intent intent = new Intent();
            intent.setAction("android.intent.action.SEND");
            StringBuilder u = a00.u("https://play.google.com/store/apps/details?id=");
            u.append(getPackageName());
            intent.putExtra("android.intent.extra.TEXT", u.toString());
            intent.setType("text/plain");
            startActivity(intent);
        } else if (valueOf == null || valueOf.intValue() != R.id.llRate) {
            if (valueOf != null && valueOf.intValue() == R.id.llFlash) {
                oa0 oa0Var3 = this.K;
                if (oa0Var3 == null) {
                    ix7.l("analystic");
                    throw null;
                }
                oa0Var3.b.b.b(null, "SETTING_FLASH_CLICKED", new Bundle(), false, true, null);
                Toast.makeText(this, "Developing", 0).show();
            } else if (valueOf != null && valueOf.intValue() == R.id.llPolicy) {
                oa0 oa0Var4 = this.K;
                if (oa0Var4 != null) {
                    oa0Var4.b.b.b(null, "SETTING_POLICY_CLICKED", new Bundle(), false, true, null);
                    try {
                        Intent intent2 = new Intent("android.intent.action.VIEW");
                        intent2.setData(Uri.parse("https://sites.google.com/view/privacy-policy-for-call-color"));
                        startActivity(intent2);
                        return;
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                ix7.l("analystic");
                throw null;
            }
        } else {
            oa0 oa0Var5 = this.K;
            if (oa0Var5 != null) {
                oa0Var5.b.b.b(null, "SETTING_RATE_CLICKED", new Bundle(), false, true, null);
                Boolean bool = (Boolean) Hawk.get("IS_RATED", Boolean.FALSE);
                ix7.c(bool);
                if (bool.booleanValue()) {
                    J();
                    return;
                }
                this.L = new fv6(this, R.style.SheetDialog);
                ViewDataBinding c = re.c(getLayoutInflater(), R.layout.layout_bottom_sheet_rate, null, false);
                ix7.e(c, "inflate(layoutInflater, …_sheet_rate, null, false)");
                final vb0 vb0Var = (vb0) c;
                fv6 fv6Var = this.L;
                ix7.c(fv6Var);
                fv6Var.setContentView(vb0Var.h);
                fv6 fv6Var2 = this.L;
                ix7.c(fv6Var2);
                fv6Var2.d().M(3);
                fv6 fv6Var3 = this.L;
                ix7.c(fv6Var3);
                fv6Var3.show();
                vb0Var.u.setOnClickListener(new View.OnClickListener() { // from class: pe0
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        final SettingActivity settingActivity = SettingActivity.this;
                        int i = SettingActivity.D;
                        ix7.f(settingActivity, "this$0");
                        int i2 = settingActivity.M;
                        if (i2 == 2) {
                            Context applicationContext = settingActivity.getApplicationContext();
                            if (applicationContext == null) {
                                applicationContext = settingActivity;
                            }
                            v77 v77Var = new v77(new z77(applicationContext));
                            ix7.e(v77Var, "create(this)");
                            s87<ReviewInfo> b = v77Var.b();
                            ix7.e(b, "reviewManager.requestReviewFlow()");
                            final we0 we0Var = new we0(v77Var, settingActivity);
                            d87<? super ReviewInfo> d87Var = new d87() { // from class: je0
                                @Override // defpackage.d87
                                public final void onSuccess(Object obj) {
                                    ow7 ow7Var = ow7.this;
                                    int i3 = SettingActivity.D;
                                    ix7.f(ow7Var, "$tmp0");
                                    ow7Var.j(obj);
                                }
                            };
                            Executor executor = f87.a;
                            b.b(executor, d87Var);
                            b.a(executor, new c87() { // from class: ie0
                                @Override // defpackage.c87
                                public final void a(Exception exc) {
                                    SettingActivity settingActivity2 = SettingActivity.this;
                                    int i3 = SettingActivity.D;
                                    ix7.f(settingActivity2, "this$0");
                                    settingActivity2.J();
                                }
                            });
                        } else if (i2 == 1) {
                            String string = settingActivity.getString(R.string.mail_subject);
                            ix7.e(string, "getString(R.string.mail_subject)");
                            Intent intent3 = new Intent("android.intent.action.SEND");
                            intent3.setType("text/email");
                            intent3.putExtra("android.intent.extra.EMAIL", gf0.b);
                            intent3.putExtra("android.intent.extra.SUBJECT", string);
                            intent3.putExtra("android.intent.extra.TEXT", "");
                            if (settingActivity.getPackageManager().getLaunchIntentForPackage("com.google.android.gm") != null) {
                                intent3.setPackage("com.google.android.gm");
                            }
                            settingActivity.startActivity(Intent.createChooser(intent3, string + ':'));
                        }
                        fv6 fv6Var4 = settingActivity.L;
                        ix7.c(fv6Var4);
                        fv6Var4.dismiss();
                    }
                });
                fv6 fv6Var4 = this.L;
                ix7.c(fv6Var4);
                fv6Var4.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: le0
                    @Override // android.content.DialogInterface.OnDismissListener
                    public final void onDismiss(DialogInterface dialogInterface) {
                        int i = SettingActivity.D;
                    }
                });
                new Handler().postDelayed(new Runnable() { // from class: me0
                    @Override // java.lang.Runnable
                    public final void run() {
                        vb0 vb0Var2 = vb0.this;
                        int i = SettingActivity.D;
                        ix7.f(vb0Var2, "$bottomBinding");
                        vb0Var2.r.A(5, true);
                    }
                }, 500L);
                vb0Var.r.setListener(new se0(this, vb0Var));
                return;
            }
            ix7.l("analystic");
            throw null;
        }
    }

    @Override // defpackage.o0, defpackage.wg, android.app.Activity
    public void onDestroy() {
        NativeAd nativeAd = this.E;
        if (nativeAd != null) {
            ix7.c(nativeAd);
            nativeAd.destroy();
        }
        com.facebook.ads.NativeAd nativeAd2 = this.F;
        if (nativeAd2 != null) {
            nativeAd2.destroy();
        }
        super.onDestroy();
    }

    @Override // defpackage.wg, androidx.activity.ComponentActivity, android.app.Activity
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        ix7.f(strArr, "permissions");
        ix7.f(iArr, "grantResults");
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 3) {
            if (!(!(iArr.length == 0)) || !ff0.a.f(iArr)) {
                L();
            } else if (!ff0.a.a(this)) {
                L();
                ff0.a.b(this);
            } else if (ff0.a.d(this)) {
            } else {
                L();
                ff0.a.m(this);
            }
        }
    }
}