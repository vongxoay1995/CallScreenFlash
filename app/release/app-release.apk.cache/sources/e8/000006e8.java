package com.call.colorscreen.ledflash.ui.main;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentManager;
import com.call.colorscreen.ledflash.database.Theme;
import com.call.colorscreen.ledflash.ui.main.MainActivity;
import com.call.colorscreen.ledflash.ui.setting.SettingActivity;
import com.call.screen.themes.color.phone.flashlight.R;
import com.facebook.internal.t;
import com.google.android.play.core.review.ReviewInfo;
import com.orhanobut.hawk.Hawk;
import defpackage.ff0;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

/* loaded from: classes.dex */
public final class MainActivity extends qa0<bb0> implements View.OnClickListener {
    public static final /* synthetic */ int D = 0;
    public md0 E;
    public he0 F;
    public ud0 G;
    public ka0 H;
    public oa0 I;
    public fv6 J;
    public int K = 0;
    public Integer[] L = {1, 4, 7};

    /* loaded from: classes.dex */
    public static final class a implements cb8<dc0> {
        public final /* synthetic */ boolean b;

        public a(boolean z) {
            this.b = z;
        }

        /* JADX WARN: Code restructure failed: missing block: B:13:0x00a0, code lost:
            if (((java.util.List) r1).size() < 10) goto L13;
         */
        @Override // defpackage.cb8
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void a(ab8<dc0> ab8Var, vb8<dc0> vb8Var) {
            ix7.f(ab8Var, "call");
            ix7.f(vb8Var, "response");
            Log.e("TAN", "onResponse: success");
            dc0 dc0Var = vb8Var.b;
            if (dc0Var != null) {
                ix7.c(dc0Var);
                List<Theme> list = dc0Var.a;
                if (list == null) {
                    ix7.l("app");
                    throw null;
                } else if (list.size() > 0) {
                    MainActivity mainActivity = MainActivity.this;
                    dc0 dc0Var2 = vb8Var.b;
                    ix7.c(dc0Var2);
                    List<Theme> list2 = dc0Var2.a;
                    if (list2 != null) {
                        dc0 dc0Var3 = vb8Var.b;
                        ix7.c(dc0Var3);
                        bc0 a = dc0Var3.a();
                        ix7.c(a);
                        int a2 = a.a();
                        int i = MainActivity.D;
                        Objects.requireNonNull(mainActivity);
                        new ArrayList();
                        Object obj = Hawk.get("VERSION_API", 0);
                        ix7.e(obj, "get(VERSION_API, 0)");
                        int intValue = ((Number) obj).intValue();
                        Object obj2 = Hawk.get("LIST_THEME", new ArrayList());
                        ix7.e(obj2, "get(LIST_THEME, mutableListOf())");
                        List list3 = (List) obj2;
                        Object obj3 = Hawk.get("LIST_THEME", new ArrayList());
                        ix7.e(obj3, "get(LIST_THEME, mutableListOf())");
                        int size = ((List) obj3).size();
                        if (a2 <= intValue) {
                            Object obj4 = Hawk.get("LIST_THEME", new ArrayList());
                            ix7.e(obj4, "get(LIST_THEME, mutableListOf())");
                        }
                        Log.e("TAN", "checkData: lan dau");
                        int size2 = list2.size();
                        for (int i2 = 0; i2 < size2; i2++) {
                            list2.get(i2).setPosition(size + i2);
                            list3.add(list2.get(i2));
                        }
                        Hawk.put("VERSION_API", Integer.valueOf(a2));
                        ix7.f(list3, "themes");
                        Hawk.put("LIST_THEME", list3);
                    } else {
                        ix7.l("app");
                        throw null;
                    }
                }
            }
            r88.b().i(new de0(true, this.b));
        }

        @Override // defpackage.cb8
        public void b(ab8<dc0> ab8Var, Throwable th) {
            ix7.f(ab8Var, "call");
            ix7.f(th, t.a);
            Log.e("TAN", "onFailure: " + th.getMessage());
            r88.b().i(new de0(true, this.b));
        }
    }

    @Override // defpackage.qa0
    public int G() {
        return R.layout.activity_main;
    }

    @Override // defpackage.qa0
    public void H() {
    }

    @Override // defpackage.qa0
    public void I(Bundle bundle) {
        oa0 a2 = oa0.a(this);
        ix7.e(a2, "getInstance(this)");
        this.I = a2;
        a2.b.b.b(null, "MAIN_SHOW", new Bundle(), false, true, null);
        RelativeLayout relativeLayout = F().q;
        ix7.e(relativeLayout, "binding.layoutHeader");
        ff0.a.j(this, relativeLayout);
        F().p.setOnClickListener(this);
        J(true);
        if (ff0.a.c(this)) {
            RelativeLayout relativeLayout2 = F().r;
            ix7.e(relativeLayout2, "binding.llAds");
            ka0 ka0Var = new ka0(this, "ca-app-pub-3940256099942544/6300978111", relativeLayout2);
            this.H = ka0Var;
            ka0Var.a();
            ka0 ka0Var2 = this.H;
            if (ka0Var2 != null) {
                ka0Var2.f = false;
                jd0 jd0Var = new jd0();
                ix7.f(jd0Var, "adsListener");
                ka0Var2.c = jd0Var;
            } else {
                ix7.l("bannerAdsUtils");
                throw null;
            }
        } else {
            F().r.setVisibility(8);
        }
        View childAt = F().s.getChildAt(0);
        ix7.d(childAt, "null cannot be cast to non-null type android.widget.LinearLayout");
        LinearLayout linearLayout = (LinearLayout) childAt;
        int childCount = linearLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            linearLayout.getChildAt(i).setOnLongClickListener(new View.OnLongClickListener() { // from class: bd0
                @Override // android.view.View.OnLongClickListener
                public final boolean onLongClick(View view) {
                    int i2 = MainActivity.D;
                    return true;
                }
            });
        }
        FragmentManager u = u();
        ix7.e(u, "supportFragmentManager");
        this.E = new md0(u);
        this.F = new he0();
        this.G = new ud0();
        md0 md0Var = this.E;
        ix7.c(md0Var);
        he0 he0Var = this.F;
        String string = getString(R.string.themesFragmentTitle);
        if (he0Var != null) {
            md0Var.i.add(he0Var);
        }
        if (string != null) {
            md0Var.j.add(string);
        }
        md0 md0Var2 = this.E;
        ix7.c(md0Var2);
        ud0 ud0Var = this.G;
        String string2 = getString(R.string.customFragmentTitle);
        if (ud0Var != null) {
            md0Var2.i.add(ud0Var);
        }
        if (string2 != null) {
            md0Var2.j.add(string2);
        }
        F().t.setAdapter(this.E);
        F().s.setupWithViewPager(F().t);
        F().t.setCurrentItem(0);
        F().t.setOffscreenPageLimit(1);
    }

    public final void J(boolean z) {
        gc0 gc0Var = gc0.a;
        Object value = gc0.c.getValue();
        ix7.e(value, "<get-api>(...)");
        ((ic0) value).a().y0(new a(z));
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0067  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x006a  */
    @Override // androidx.activity.ComponentActivity, android.app.Activity
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void onBackPressed() {
        int i;
        Object obj = Hawk.get("ALLOW_RATE", Boolean.TRUE);
        ix7.e(obj, "get(ALLOW_RATE, true)");
        if (((Boolean) obj).booleanValue()) {
            Object obj2 = Hawk.get("COUNT_EXIT_APP", 0);
            ix7.e(obj2, "get(COUNT_EXIT_APP, 0)");
            int intValue = ((Number) obj2).intValue() + 1;
            Hawk.put("COUNT_EXIT_APP", Integer.valueOf(intValue));
            Integer[] numArr = this.L;
            Integer valueOf = Integer.valueOf(intValue);
            ix7.f(numArr, "<this>");
            ix7.f(numArr, "<this>");
            if (valueOf == null) {
                int length = numArr.length;
                i = 0;
                while (i < length) {
                    if (numArr[i] == null) {
                        break;
                    }
                    i++;
                }
                i = -1;
                if (i >= 0) {
                    Boolean bool = (Boolean) Hawk.get("IS_RATED", Boolean.FALSE);
                    ix7.c(bool);
                    if (!bool.booleanValue()) {
                        this.J = new fv6(this, R.style.SheetDialog);
                        ViewDataBinding c = re.c(getLayoutInflater(), R.layout.layout_bottom_sheet_rate, null, false);
                        ix7.e(c, "inflate(layoutInflater, …_sheet_rate, null, false)");
                        final vb0 vb0Var = (vb0) c;
                        fv6 fv6Var = this.J;
                        ix7.c(fv6Var);
                        fv6Var.setContentView(vb0Var.h);
                        fv6 fv6Var2 = this.J;
                        ix7.c(fv6Var2);
                        fv6Var2.d().M(3);
                        fv6 fv6Var3 = this.J;
                        ix7.c(fv6Var3);
                        fv6Var3.show();
                        vb0Var.u.setOnClickListener(new View.OnClickListener() { // from class: fd0
                            @Override // android.view.View.OnClickListener
                            public final void onClick(View view) {
                                final MainActivity mainActivity = MainActivity.this;
                                int i2 = MainActivity.D;
                                ix7.f(mainActivity, "this$0");
                                int i3 = mainActivity.K;
                                if (i3 == 2) {
                                    Context applicationContext = mainActivity.getApplicationContext();
                                    if (applicationContext == null) {
                                        applicationContext = mainActivity;
                                    }
                                    v77 v77Var = new v77(new z77(applicationContext));
                                    ix7.e(v77Var, "create(this)");
                                    s87<ReviewInfo> b = v77Var.b();
                                    ix7.e(b, "reviewManager.requestReviewFlow()");
                                    final ld0 ld0Var = new ld0(v77Var, mainActivity);
                                    d87<? super ReviewInfo> d87Var = new d87() { // from class: cd0
                                        @Override // defpackage.d87
                                        public final void onSuccess(Object obj3) {
                                            ow7 ow7Var = ow7.this;
                                            int i4 = MainActivity.D;
                                            ix7.f(ow7Var, "$tmp0");
                                            ow7Var.j(obj3);
                                        }
                                    };
                                    Executor executor = f87.a;
                                    b.b(executor, d87Var);
                                    b.a(executor, new c87() { // from class: dd0
                                        @Override // defpackage.c87
                                        public final void a(Exception exc) {
                                            MainActivity mainActivity2 = MainActivity.this;
                                            int i4 = MainActivity.D;
                                            ix7.f(mainActivity2, "this$0");
                                            try {
                                                Intent intent = new Intent("android.intent.action.VIEW");
                                                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + mainActivity2.getPackageName()));
                                                mainActivity2.startActivity(intent);
                                            } catch (ActivityNotFoundException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else if (i3 == 1) {
                                    String string = mainActivity.getString(R.string.mail_subject);
                                    ix7.e(string, "getString(R.string.mail_subject)");
                                    Intent intent = new Intent("android.intent.action.SEND");
                                    intent.setType("text/email");
                                    intent.putExtra("android.intent.extra.EMAIL", gf0.b);
                                    intent.putExtra("android.intent.extra.SUBJECT", string);
                                    intent.putExtra("android.intent.extra.TEXT", "");
                                    if (mainActivity.getPackageManager().getLaunchIntentForPackage("com.google.android.gm") != null) {
                                        intent.setPackage("com.google.android.gm");
                                    }
                                    mainActivity.startActivity(Intent.createChooser(intent, string + ':'));
                                }
                                fv6 fv6Var4 = mainActivity.J;
                                ix7.c(fv6Var4);
                                fv6Var4.dismiss();
                            }
                        });
                        fv6 fv6Var4 = this.J;
                        ix7.c(fv6Var4);
                        fv6Var4.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: ed0
                            @Override // android.content.DialogInterface.OnDismissListener
                            public final void onDismiss(DialogInterface dialogInterface) {
                                int i2 = MainActivity.D;
                            }
                        });
                        new Handler().postDelayed(new Runnable() { // from class: ad0
                            @Override // java.lang.Runnable
                            public final void run() {
                                vb0 vb0Var2 = vb0.this;
                                int i2 = MainActivity.D;
                                ix7.f(vb0Var2, "$bottomBinding");
                                vb0Var2.r.A(5, true);
                            }
                        }, 500L);
                        vb0Var.r.setListener(new id0(this, vb0Var));
                        return;
                    }
                }
            } else {
                int length2 = numArr.length;
                for (int i2 = 0; i2 < length2; i2++) {
                    if (ix7.a(valueOf, numArr[i2])) {
                        i = i2;
                        break;
                    }
                }
                i = -1;
                if (i >= 0) {
                }
            }
        }
        this.t.a();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        Integer valueOf = view != null ? Integer.valueOf(view.getId()) : null;
        if (valueOf != null && valueOf.intValue() == R.id.btnSetting) {
            oa0 oa0Var = this.I;
            if (oa0Var == null) {
                ix7.l("analystic");
                throw null;
            }
            oa0Var.b.b.b(null, "MAIN_SETTING_CLICKED", new Bundle(), false, true, null);
            startActivity(new Intent(this, SettingActivity.class));
        }
    }
}