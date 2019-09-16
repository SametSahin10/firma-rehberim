package net.dijitalbeyin.firma_rehberim;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

public class PlayRadioService extends Service {
    private RadiosFragment radiosFragment;
    private FavouriteRadiosFragment favouriteRadiosFragment;

    private AudioManager audioManager;
    private SimpleExoPlayer exoPlayer;
    private MediaSource mediaSource;
    private DefaultDataSourceFactory dataSourceFactory;
    private ExoPlayer.EventListener eventListener;
    private DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    TrackSelector trackSelector = new DefaultTrackSelector();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        eventListener = new ExoPlayer.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case ExoPlayer.STATE_BUFFERING:
                        Log.d("TAG", "STATE_BUFFERING");
                        if (isFromFavouriteRadiosFragment) {
                            favouriteRadiosFragment.setCurrentRadioStatus(STATE_BUFFERING, radioCurrentlyPlaying);
                        } else {
                            radiosFragment.setCurrentRadioStatus(STATE_BUFFERING, radioCurrentlyPlaying);
                        }
                        break;
                    case ExoPlayer.STATE_READY:
                        if (isFromFavouriteRadiosFragment) {
                            favouriteRadiosFragment.setCurrentRadioStatus(STATE_READY, radioCurrentlyPlaying);
                        } else {
                            radiosFragment.setCurrentRadioStatus(STATE_READY, radioCurrentlyPlaying);
                        }
                        if (isPlaying()) {
                            ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_radio));
                        }
                        sb_volume_control.setMax(audioManager.getStreamMaxVolume(exoPlayer.getAudioStreamType()));
                        sb_volume_control.setProgress(audioManager.getStreamVolume(exoPlayer.getAudioStreamType()));
                        Log.d("TAG", "STATE_READY");
                        break;
                    case ExoPlayer.STATE_IDLE:
                        Log.d("TAG", "STATE_IDLE");
                        exoPlayer.release();
                        if (isFromFavouriteRadiosFragment) {
                            favouriteRadiosFragment.setCurrentRadioStatus(STATE_IDLE, radioCurrentlyPlaying);
                        } else {
                            radiosFragment.setCurrentRadioStatus(STATE_IDLE, radioCurrentlyPlaying);
                        }
                        break;
                    case ExoPlayer.STATE_ENDED:
                        Log.d("TAG", "STATE_ENDED");
                        break;
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Toast.makeText(getApplicationContext(), R.string.cannot_stream_radio_text, Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "onPlayerError: ", error);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
