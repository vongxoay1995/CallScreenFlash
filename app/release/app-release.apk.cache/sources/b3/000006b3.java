package com.call.colorscreen.ledflash.call;

import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.android.internal.telephony.ITelephony;
import com.call.colorscreen.ledflash.call.IncommingCallActivity;
import com.call.colorscreen.ledflash.database.AppDatabase;
import com.call.colorscreen.ledflash.database.Contact;
import com.call.colorscreen.ledflash.database.Theme;
import com.call.colorscreen.ledflash.service.AcceptCallActivity;
import com.call.screen.themes.color.phone.flashlight.R;
import com.orhanobut.hawk.Hawk;
import defpackage.ff0;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/* loaded from: classes.dex */
public final class IncommingCallActivity extends qa0<za0> implements View.OnClickListener {
    public static final /* synthetic */ int D = 0;
    public vj E;
    public Theme G;
    public Theme H;
    public int I;
    public String J;
    public Bitmap L;
    public Contact M;
    public TelephonyManager O;
    public ITelephony P;
    public oa0 Q;
    public String F = "";
    public String K = "";
    public final nu7 N = cc5.G0(ou7.SYNCHRONIZED, new b(this, null, null));
    public BroadcastReceiver R = new a();

    /* loaded from: classes.dex */
    public static final class a extends BroadcastReceiver {
        public a() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            ix7.f(intent, "intent");
            if (ix7.a(intent.getAction(), "com.callcolor.endCall")) {
                IncommingCallActivity.this.finish();
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class b extends jx7 implements dw7<AppDatabase> {
        public final /* synthetic */ ComponentCallbacks p;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public b(ComponentCallbacks componentCallbacks, na8 na8Var, dw7 dw7Var) {
            super(0);
            this.p = componentCallbacks;
        }

        /* JADX WARN: Type inference failed for: r0v2, types: [java.lang.Object, com.call.colorscreen.ledflash.database.AppDatabase] */
        @Override // defpackage.dw7
        public final AppDatabase b() {
            return cc5.e0(this.p).a(px7.a(AppDatabase.class), null, null);
        }
    }

    @Override // defpackage.qa0
    public int G() {
        return R.layout.activity_incomming_call;
    }

    @Override // defpackage.qa0
    public void H() {
        getWindow().setFlags(1024, 1024);
        getWindow().addFlags(524288);
        getWindow().addFlags(4194304);
        getWindow().addFlags(128);
        getWindow().addFlags(2097152);
    }

    @Override // defpackage.qa0
    public void I(Bundle bundle) {
        String path_file;
        String path_file2;
        this.E = vj.a(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.callcolor.endCall");
        vj vjVar = this.E;
        ix7.c(vjVar);
        vjVar.b(this.R, intentFilter);
        Log.e("TAN", "onViewReady: aaaa");
        this.F = String.valueOf(getIntent().getStringExtra("phone_number"));
        Object obj = Hawk.get("THEME_SELECT", new Theme(0, 0, "thumb/default_1.webp", "/raw/default_1", false, "default_1"));
        ix7.e(obj, "get(\n                THE…      theme\n            )");
        Theme theme = (Theme) obj;
        this.G = theme;
        ix7.c(theme);
        this.I = theme.getType();
        try {
            Context applicationContext = getApplicationContext();
            ix7.e(applicationContext, "applicationContext");
            hf0 h = ff0.a.h(applicationContext, this.F);
            this.J = h.a;
            this.K = h.b;
            F().t.setText(this.J);
            String str = this.J;
            if (str == null ? false : str.equals("")) {
                F().t.setText(getString(R.string.unknowContact));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Context baseContext = getBaseContext();
        ix7.e(baseContext, "baseContext");
        String str2 = this.F;
        ix7.f(baseContext, "context");
        ix7.f(str2, "number");
        Bitmap decodeResource = BitmapFactory.decodeResource(baseContext.getResources(), R.drawable.avatar);
        if (!ix7.a(str2, "")) {
            Cursor query = baseContext.getContentResolver().query(Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(str2)), new String[]{"display_name", "_id"}, null, null, null);
            String str3 = null;
            if (query != null) {
                while (query.moveToNext()) {
                    str3 = query.getString(query.getColumnIndexOrThrow("_id"));
                }
                query.close();
            }
            if (str3 != null) {
                try {
                    ContentResolver contentResolver = baseContext.getContentResolver();
                    Uri uri = ContactsContract.Contacts.CONTENT_URI;
                    Long valueOf = Long.valueOf(str3);
                    ix7.e(valueOf, "valueOf(contactId)");
                    InputStream openContactPhotoInputStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, ContentUris.withAppendedId(uri, valueOf.longValue()), true);
                    if (openContactPhotoInputStream != null) {
                        decodeResource = BitmapFactory.decodeStream(openContactPhotoInputStream);
                    }
                    if (openContactPhotoInputStream != null) {
                        openContactPhotoInputStream.close();
                    }
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        }
        this.L = decodeResource;
        F().q.setImageBitmap(this.L);
        F().u.setText(this.F);
        F().v.setVisibility(0);
        List<Contact> contactById = ((AppDatabase) this.N.getValue()).serverDao().getContactById(this.K);
        if (!contactById.isEmpty()) {
            this.M = contactById.get(0);
            go7 go7Var = new go7();
            Contact contact = this.M;
            ix7.c(contact);
            this.H = (Theme) go7Var.b(contact.getTheme(), Theme.class);
        }
        Theme theme2 = this.H;
        if (theme2 != null) {
            this.G = theme2;
            ix7.c(theme2);
            this.I = theme2.getType();
        }
        int i = this.I;
        if (i == 0) {
            F().s.setVisibility(8);
            F().v.setVisibility(0);
            Theme theme3 = this.G;
            String path_file3 = theme3 != null ? theme3.getPath_file() : null;
            ix7.c(path_file3);
            if (!my7.a(path_file3, "storage", false, 2)) {
                Theme theme4 = this.G;
                ix7.c(theme4);
                if (!my7.a(theme4.getPath_file(), "/data/data", false, 2)) {
                    Theme theme5 = this.G;
                    ix7.c(theme5);
                    if (!my7.a(theme5.getPath_file(), "data/user/", false, 2)) {
                        StringBuilder u = a00.u("android.resource://");
                        u.append(getPackageName());
                        Theme theme6 = this.G;
                        ix7.c(theme6);
                        u.append(theme6.getPath_file());
                        path_file = u.toString();
                        F().v.setVideoURI(Uri.parse(path_file));
                        F().v.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: sa0
                            @Override // android.media.MediaPlayer.OnErrorListener
                            public final boolean onError(MediaPlayer mediaPlayer, int i2, int i3) {
                                IncommingCallActivity incommingCallActivity = IncommingCallActivity.this;
                                int i4 = IncommingCallActivity.D;
                                ix7.f(incommingCallActivity, "this$0");
                                oa0 oa0Var = incommingCallActivity.Q;
                                if (oa0Var == null) {
                                    ix7.l("analystic");
                                    throw null;
                                }
                                Bundle bundle2 = new Bundle();
                                bundle2.putInt("what", i2);
                                bundle2.putInt("extra", i3);
                                oa0Var.b.b.b(null, "CallVideoViewError", bundle2, false, true, null);
                                incommingCallActivity.finish();
                                return true;
                            }
                        });
                        F().v.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: ta0
                            @Override // android.media.MediaPlayer.OnPreparedListener
                            public final void onPrepared(MediaPlayer mediaPlayer) {
                                IncommingCallActivity incommingCallActivity = IncommingCallActivity.this;
                                int i2 = IncommingCallActivity.D;
                                ix7.f(incommingCallActivity, "this$0");
                                mediaPlayer.setLooping(true);
                                mediaPlayer.setVolume(0.0f, 0.0f);
                                incommingCallActivity.F().v.start();
                            }
                        });
                    }
                }
            }
            Theme theme7 = this.G;
            ix7.c(theme7);
            path_file = theme7.getPath_file();
            F().v.setVideoURI(Uri.parse(path_file));
            F().v.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: sa0
                @Override // android.media.MediaPlayer.OnErrorListener
                public final boolean onError(MediaPlayer mediaPlayer, int i2, int i3) {
                    IncommingCallActivity incommingCallActivity = IncommingCallActivity.this;
                    int i4 = IncommingCallActivity.D;
                    ix7.f(incommingCallActivity, "this$0");
                    oa0 oa0Var = incommingCallActivity.Q;
                    if (oa0Var == null) {
                        ix7.l("analystic");
                        throw null;
                    }
                    Bundle bundle2 = new Bundle();
                    bundle2.putInt("what", i2);
                    bundle2.putInt("extra", i3);
                    oa0Var.b.b.b(null, "CallVideoViewError", bundle2, false, true, null);
                    incommingCallActivity.finish();
                    return true;
                }
            });
            F().v.setOnPreparedListener(new MediaPlayer.OnPreparedListener() { // from class: ta0
                @Override // android.media.MediaPlayer.OnPreparedListener
                public final void onPrepared(MediaPlayer mediaPlayer) {
                    IncommingCallActivity incommingCallActivity = IncommingCallActivity.this;
                    int i2 = IncommingCallActivity.D;
                    ix7.f(incommingCallActivity, "this$0");
                    mediaPlayer.setLooping(true);
                    mediaPlayer.setVolume(0.0f, 0.0f);
                    incommingCallActivity.F().v.start();
                }
            });
        } else if (i == 1) {
            F().s.setVisibility(0);
            Theme theme8 = this.G;
            ix7.c(theme8);
            if (my7.a(theme8.getPath_file(), "default", false, 2)) {
                Theme theme9 = this.G;
                ix7.c(theme9);
                if (my7.a(theme9.getPath_file(), "thumbDefault", false, 2)) {
                    StringBuilder u2 = a00.u("file:///android_asset/");
                    Theme theme10 = this.G;
                    ix7.c(theme10);
                    u2.append(theme10.getPath_file());
                    path_file2 = u2.toString();
                    c00.d(getApplicationContext()).e(path_file2).d(n20.b).B(0.1f).x(F().s);
                    F().v.setVisibility(8);
                }
            }
            Theme theme11 = this.G;
            ix7.c(theme11);
            path_file2 = theme11.getPath_file();
            c00.d(getApplicationContext()).e(path_file2).d(n20.b).B(0.1f).x(F().s);
            F().v.setVisibility(8);
        }
        new Handler().postDelayed(new Runnable() { // from class: wa0
            @Override // java.lang.Runnable
            public final void run() {
                IncommingCallActivity incommingCallActivity = IncommingCallActivity.this;
                Objects.requireNonNull(incommingCallActivity);
                Animation loadAnimation = AnimationUtils.loadAnimation(incommingCallActivity, R.anim.ani_bling_call);
                ix7.e(loadAnimation, "loadAnimation(this, com.…sh.R.anim.ani_bling_call)");
                incommingCallActivity.F().p.startAnimation(loadAnimation);
            }
        }, 400L);
        Object systemService = getSystemService("phone");
        ix7.d(systemService, "null cannot be cast to non-null type android.telephony.TelephonyManager");
        TelephonyManager telephonyManager = (TelephonyManager) systemService;
        this.O = telephonyManager;
        try {
            if (Build.VERSION.SDK_INT < 29) {
                ix7.c(telephonyManager);
                Class<?> cls = Class.forName(telephonyManager.getClass().getName());
                ix7.e(cls, "forName(telephonyManager!!.javaClass.name)");
                Method declaredMethod = cls.getDeclaredMethod("getITelephony", new Class[0]);
                ix7.e(declaredMethod, "clazz.getDeclaredMethod(\"getITelephony\")");
                declaredMethod.setAccessible(true);
                Object invoke = declaredMethod.invoke(this.O, new Object[0]);
                ix7.d(invoke, "null cannot be cast to non-null type com.android.internal.telephony.ITelephony");
                this.P = (ITelephony) invoke;
            }
        } catch (Exception e3) {
            finish();
            e3.printStackTrace();
        }
        F().p.setOnClickListener(new View.OnClickListener() { // from class: va0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                IncommingCallActivity incommingCallActivity = IncommingCallActivity.this;
                int i2 = IncommingCallActivity.D;
                ix7.f(incommingCallActivity, "this$0");
                oa0 oa0Var = incommingCallActivity.Q;
                if (oa0Var == null) {
                    ix7.l("analystic");
                    throw null;
                }
                oa0Var.b.b.b(null, "CALL_ACCETP_CALL", new Bundle(), false, true, null);
                if (Build.VERSION.SDK_INT >= 26) {
                    Object systemService2 = incommingCallActivity.getSystemService("telecom");
                    ix7.d(systemService2, "null cannot be cast to non-null type android.telecom.TelecomManager");
                    TelecomManager telecomManager = (TelecomManager) systemService2;
                    if (u9.a(incommingCallActivity, "android.permission.ANSWER_PHONE_CALLS") != 0) {
                        return;
                    }
                    telecomManager.acceptRingingCall();
                } else {
                    Intent intent = new Intent(incommingCallActivity.getApplicationContext(), AcceptCallActivity.class);
                    intent.setFlags(276856832);
                    incommingCallActivity.getApplicationContext().startActivity(intent);
                }
                incommingCallActivity.finish();
            }
        });
        F().r.setOnClickListener(new View.OnClickListener() { // from class: ua0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                IncommingCallActivity incommingCallActivity = IncommingCallActivity.this;
                int i2 = IncommingCallActivity.D;
                ix7.f(incommingCallActivity, "this$0");
                oa0 oa0Var = incommingCallActivity.Q;
                if (oa0Var == null) {
                    ix7.l("analystic");
                    throw null;
                }
                oa0Var.b.b.b(null, "CALL_REJECT_CALL", new Bundle(), false, true, null);
                try {
                    if (Build.VERSION.SDK_INT >= 28) {
                        Object systemService2 = incommingCallActivity.getApplicationContext().getSystemService("telecom");
                        ix7.d(systemService2, "null cannot be cast to non-null type android.telecom.TelecomManager");
                        ((TelecomManager) systemService2).endCall();
                    } else {
                        ITelephony iTelephony = incommingCallActivity.P;
                        ix7.c(iTelephony);
                        iTelephony.endCall();
                    }
                    incommingCallActivity.finish();
                } catch (Exception unused) {
                    incommingCallActivity.finish();
                }
            }
        });
        oa0 a2 = oa0.a(this);
        ix7.e(a2, "getInstance(this)");
        this.Q = a2;
        a2.b.b.b(null, "CALL_SHOW", new Bundle(), false, true, null);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
    }

    @Override // defpackage.o0, defpackage.wg, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        vj vjVar = this.E;
        ix7.c(vjVar);
        vjVar.d(this.R);
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (z) {
            getWindow().getDecorView().setSystemUiVisibility(5894);
        }
    }
}