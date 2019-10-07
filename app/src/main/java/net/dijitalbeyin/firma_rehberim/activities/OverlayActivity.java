package net.dijitalbeyin.firma_rehberim.activities;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.dijitalbeyin.firma_rehberim.services.OverlayService;
import net.dijitalbeyin.firma_rehberim.R;

public class OverlayActivity extends AppCompatActivity {

    Button btn_start_service;
    Button btn_stop_service;
    public static int SERVICE_STOPPED = 0;
    public static int SERVICE_RUNNING = 1;
    public static int serviceState = SERVICE_STOPPED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overlay);

        ActivityCompat.requestPermissions(OverlayActivity.this, new String[]{Manifest.permission.READ_CALL_LOG}, 0);

        if (Build.BRAND.equalsIgnoreCase("xiaomi")) {
            Intent autoStartintent = new Intent();
            autoStartintent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            startActivity(autoStartintent);
        }

        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Lütfen ayarlardan \"Otomatik başlatma\" seçeneğini etkinleştiriniz.", Toast.LENGTH_LONG).show();
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
