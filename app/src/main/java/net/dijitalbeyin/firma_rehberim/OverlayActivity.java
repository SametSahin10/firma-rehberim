package net.dijitalbeyin.firma_rehberim;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class OverlayActivity extends AppCompatActivity {

    Button btn_start_service;
    Button btn_stop_service;
    private static int SERVICE_STOPPED = 0;
    private static int SERVICE_RUNNING = 1;
    int serviceState = SERVICE_STOPPED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overlay);

        if (Build.BRAND.equalsIgnoreCase("xiaomi")) {
            Intent autoStartintent = new Intent();
            autoStartintent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            startActivity(autoStartintent);
        }

        if (Build.VERSION.SDK_INT >= 23) {
            Intent intent;
            if (!Settings.canDrawOverlays(this)) {
                if (Build.BRAND.equalsIgnoreCase("xiaomi")) {
                    Toast.makeText(this, "Lütfen ayarlardan \"Otomatik başlatma\" seçeneğini etkinleştiriniz.", Toast.LENGTH_LONG).show();
                } else {
                    intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package" + getApplicationContext().getPackageName()));
                    startActivity(intent);
                }
            }
        }


        btn_start_service = findViewById(R.id.btn_start_service);
        btn_start_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serviceState == SERVICE_STOPPED) {
                    boolean permissionGranted = ContextCompat.checkSelfPermission(
                            getApplicationContext(),
                            Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
                    if (permissionGranted) {
                        Intent intent = new Intent(getApplicationContext(), OverlayService.class);
                        intent.putExtra("Sender", "Activity Button");
                        startService(intent);
                        serviceState = SERVICE_RUNNING;
                        btn_start_service.setVisibility(View.INVISIBLE);
                        btn_stop_service.setVisibility(View.VISIBLE);
                    } else {
                        ActivityCompat.requestPermissions(OverlayActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
                    }
                }
            }
        });

        btn_stop_service = findViewById(R.id.btn_stop_service);
        btn_stop_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serviceState == SERVICE_RUNNING) {
                    Intent intent = new Intent(getApplicationContext(), OverlayService.class);
                    stopService(intent);
                    serviceState = SERVICE_STOPPED;
                    btn_stop_service.setVisibility(View.INVISIBLE);
                    btn_start_service.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
