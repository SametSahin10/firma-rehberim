package com.firmarehberim.canliradyo.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import com.firmarehberim.canliradyo.services.PlayRadioService;

public class BecomingNoisyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
                Intent pauseAudioIntent = new Intent(context, PlayRadioService.class);
                pauseAudioIntent.putExtra("pauseAudio", true);
                context.startService(pauseAudioIntent);
            }
        }
    }
}
