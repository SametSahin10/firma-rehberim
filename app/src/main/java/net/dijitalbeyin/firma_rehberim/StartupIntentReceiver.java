package net.dijitalbeyin.firma_rehberim;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static net.dijitalbeyin.firma_rehberim.OverlayActivity.SERVICE_RUNNING;
import static net.dijitalbeyin.firma_rehberim.OverlayActivity.serviceState;

public class StartupIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean permissionGranted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
        if (permissionGranted) {
            Intent startServiceIntent = new Intent(context, OverlayService.class);
            startServiceIntent.putExtra("From Boot", "");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(startServiceIntent);
            } else {
                context.startService(startServiceIntent);
            }
            serviceState = SERVICE_RUNNING;
        } else {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
        }
    }
}
