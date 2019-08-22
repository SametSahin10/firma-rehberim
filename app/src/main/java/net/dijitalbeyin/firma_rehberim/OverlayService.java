package net.dijitalbeyin.firma_rehberim;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OverlayService extends Service {
    RelativeLayout root_overlaying_view;
    TextView tv_company_name;
    TextView tv_authoritative_name;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        String channelId = "channelid";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = getString(R.string.app_name);
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelId, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(channelId);
            notificationChannel.setSound(null, null);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_second_launcher)
                .setContentTitle("Firma Rehberim")
                .setContentText("Arayan firma tespiti aktif")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        startForeground(111, builder.build());
        Log.d("TAG", "onCreate service");

        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(getApplicationContext())) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            setupViews();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (root_overlaying_view != null) {
            WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            windowManager.removeView(root_overlaying_view);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.hasExtra("userName")) {
                if (intent.getExtras() != null) {
                    Log.d("TAG", "Intent has extra");
                    if (root_overlaying_view != null) {
                        root_overlaying_view.setVisibility(View.VISIBLE);
                        String userName = intent.getExtras().getString("userName");
                        String authoritativeName = intent.getExtras().getString("authoritativeName");
                        tv_company_name.setText(userName);
                        tv_authoritative_name.setText(authoritativeName);
                    }
                }
            }

            if (intent.hasExtra("Sender")) {
                if (intent.getExtras() != null) {
                    Log.d("TAG", "Intent has extra");
                    if (root_overlaying_view != null) {
                        root_overlaying_view.setVisibility(View.INVISIBLE);
                    }
                }
            }

            if (intent.hasExtra("After onTaskRemoved")) {
                Log.d("TAG", "After onTaskRemoved");
                if (intent.getExtras() != null) {
                    Log.d("TAG", "Intent has extra");
                    if (root_overlaying_view != null) {
                        root_overlaying_view.setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("TAG", "onTaskRemoved");
        Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
        restartServiceIntent.putExtra("After onTaskRemoved", "true");
        restartServiceIntent.setPackage(getPackageName());
        PendingIntent restartServicePendingIntent = PendingIntent.getService(
                                                    getApplicationContext(),
                                                            1,
                                                    restartServiceIntent,
                                                    PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
        alarmManager.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartServicePendingIntent
        );
        super.onTaskRemoved(rootIntent);
    }

    private void setupViews() {
        root_overlaying_view = new RelativeLayout(this);
        int layoutParams;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("TAG", "Higher than Oreo");
            if (Settings.canDrawOverlays(getApplicationContext())) {
                Log.d("TAG", "Can draw overlays");
            }
            layoutParams = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY |
                            WindowManager.LayoutParams.TYPE_PHONE;
        } else {
            Log.d("TAG", "Lower than Oreo");
            layoutParams = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutParams,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.BOTTOM;
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(root_overlaying_view, params);

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View bottom_overlay_layout = layoutInflater.inflate(R.layout.bottom_overlay_layout, root_overlaying_view, false);

        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        root_overlaying_view.addView(bottom_overlay_layout, relativeLayoutParams);

//        LinearLayout linearLayout = root_overlaying_view.findViewById(R.id.linear_layout);
        //TODO: Set OnClickListener for linear layout once API gets configured.

        tv_company_name = root_overlaying_view.findViewById(R.id.tv_company_name);
        tv_authoritative_name = root_overlaying_view.findViewById(R.id.tv_authoritative_name);

        ImageButton ib_btn_close_panel = bottom_overlay_layout.findViewById(R.id.ib_close_panel);
        ib_btn_close_panel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                root_overlaying_view.setVisibility(View.GONE);
                Log.d("TAG", "Button clicked");
            }
        });
    }
}
