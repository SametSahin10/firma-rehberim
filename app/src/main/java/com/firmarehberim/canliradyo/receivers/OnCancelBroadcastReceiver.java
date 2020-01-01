package com.firmarehberim.canliradyo.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.firmarehberim.canliradyo.services.PlayRadioService;

public class OnCancelBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("TAG", "Broadcast received");
        Intent cancelNotificationIntent = new Intent(context, PlayRadioService.class);
        cancelNotificationIntent.putExtra("showNotification", false);
        context.startService(cancelNotificationIntent);
    }
}
