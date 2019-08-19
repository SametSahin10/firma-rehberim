package net.dijitalbeyin.firma_rehberim;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OverlayService extends Service {
    RelativeLayout overlaying_view;
    TextView tv_caller_name;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("TAG", "onCreate service");
        overlaying_view = new RelativeLayout(this);
        overlaying_view.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        int layoutParams;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("TAG", "Higher than Oreo");
            if (Settings.canDrawOverlays(getApplicationContext())) {
                Log.d("TAG", "Can draw overlays");
            }
            layoutParams = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            Log.d("TAG", "Lower than Oreo");
            layoutParams = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(getApplicationContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutParams,
                 WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.BOTTOM;
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(overlaying_view, params);

        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
//        overlaying_view.setLayoutParams(relativeLayoutParams);

        tv_caller_name = new TextView(this);
        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.righteous_regular);
        tv_caller_name.setTypeface(typeface);
        tv_caller_name.setTextColor(Color.WHITE);
        tv_caller_name.setTextSize(32);
        overlaying_view.addView(tv_caller_name, relativeLayoutParams);

        ImageButton ib_btn_close_panel = new ImageButton(this);
        ib_btn_close_panel.setImageDrawable(getDrawable(R.drawable.ic_close));
        ib_btn_close_panel.setBackground(null);
        overlaying_view.addView(ib_btn_close_panel, relativeLayoutParams);

        ib_btn_close_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlaying_view.setVisibility(View.GONE);
                Log.d("TAG", "Button clicked");
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlaying_view != null) {
            WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            windowManager.removeView(overlaying_view);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra("username")) {
            if (intent.getExtras() != null) {
                Log.d("TAG", "Intent has extra");
                String username = intent.getExtras().getString("username");
                tv_caller_name.setText(username);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
