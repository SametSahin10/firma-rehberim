package net.dijitalbeyin.firma_rehberim.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.NotificationCompat;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;
import net.dijitalbeyin.firma_rehberim.datamodel.Radio;
import net.dijitalbeyin.firma_rehberim.R;

import okhttp3.OkHttpClient;

import static com.google.android.exoplayer2.ExoPlayer.STATE_BUFFERING;
import static com.google.android.exoplayer2.ExoPlayer.STATE_IDLE;
import static com.google.android.exoplayer2.ExoPlayer.STATE_READY;

public class PlayRadioService extends Service {
    private static final String LOG_TAG = PlayRadioService.class.getSimpleName();
    private static final int STATE_BUFFERING = 10;
    private static final int STATE_READY = 11;
    private static final int STATE_IDLE = 12;
    private static final int NOTIFICATION_ID = 1;

    private final IBinder binder = new PlayRadioBinder();
    private ServiceCallbacks serviceCallbacks;

    Radio radioCurrentlyPlaying;
    Radio radioClicked;

    public AudioManager audioManager;
    public SimpleExoPlayer exoPlayer;
    private MediaSource mediaSource;
    private DefaultDataSourceFactory dataSourceFactory;
    private ExoPlayer.EventListener eventListener;
    private DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    TrackSelector trackSelector = new DefaultTrackSelector();

    boolean isFromFavouriteRadiosFragment = false;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
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
                            serviceCallbacks.updateFavouriteRadiosFragment(STATE_BUFFERING);
                        } else {
                            serviceCallbacks.updateRadiosFragment(STATE_BUFFERING);
                        }
                        break;
                    case ExoPlayer.STATE_READY:
                        if (isFromFavouriteRadiosFragment) {
                            serviceCallbacks.updateFavouriteRadiosFragment(STATE_READY);
                        } else {
                            serviceCallbacks.updateRadiosFragment(STATE_READY);
                        }
                        if (isPlaying()) {
                            serviceCallbacks.togglePlayPauseButton();
                        }
                        int streamMaxVolume = audioManager
                                .getStreamMaxVolume(exoPlayer.getAudioStreamType());
                        serviceCallbacks.updateVolumeBar(streamMaxVolume);
                        Log.d("TAG", "STATE_READY");
                        break;
                    case ExoPlayer.STATE_IDLE:
                        Log.d("TAG", "STATE_IDLE");
                        if (isFromFavouriteRadiosFragment) {
                            serviceCallbacks.updateFavouriteRadiosFragment(STATE_IDLE);
                        } else {
                            serviceCallbacks.updateRadiosFragment(STATE_IDLE);
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
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        exoPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector);
        exoPlayer.addListener(eventListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initNotification();
        Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.release();
        }
        cancelNotification();
        super.onDestroy();
    }

    private void prepareExoPlayer(Uri uri) {
        dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), Util.getUserAgent(getApplicationContext(), "exoPlayerSimple"), BANDWIDTH_METER);
        String userAgent = Util.getUserAgent(getApplicationContext(), "exoPlayerSimple");
        mediaSource = new ExtractorMediaSource(uri,
                new OkHttpDataSourceFactory(new OkHttpClient(), userAgent, (TransferListener) null),
                new DefaultExtractorsFactory(),
                null,
                null);
        exoPlayer.addAnalyticsListener(new EventLogger((MappingTrackSelector) trackSelector));
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
    }

    public void playRadio(Radio radioClicked) {
        Log.d("TAG", "Radio stream link: " + radioClicked.getStreamLink());
        radioCurrentlyPlaying = radioClicked;
        if (exoPlayer != null) {
            if (isPlaying()) {
                exoPlayer.setPlayWhenReady(false);
                exoPlayer.stop(true);
            }
        }
        String streamLink = radioClicked.getStreamLink();
        prepareExoPlayer(Uri.parse(streamLink));
//        String iconUrl = radioClicked.getRadioIconUrl();
//        Picasso.with(getApplicationContext()).load(iconUrl)
//                .resize(200, 200)
//                .centerInside()
//                .placeholder(R.drawable.ic_placeholder_radio_black)
//                .error(R.drawable.ic_pause_radio)
//                .into(iv_radioIcon);
//
//        iv_radioIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                Log.d("TAG", "Shareable link: " + radioCurrentlyPlaying.getShareableLink());
//                intent.setData(Uri.parse(radioCurrentlyPlaying.getShareableLink()));
//                if (intent.resolveActivity(getPackageManager()) != null) {
//                    startActivity(intent);
//                }
//            }
//        });
    }

    private void initNotification() {
        String channelId = "newChannelId";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = getString(R.string.app_name);
            NotificationChannel notificationChannel = new NotificationChannel(channelId,
                                                                              channelId,
                                                NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(channelId);
            notificationChannel.setSound(null, null);
            NotificationManager notificationManager
                    = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Firma Rehberim Radio Player")
                .setContentText("Playing radio")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(false);

        startForeground(112, builder.build());
    }

    private void cancelNotification() {
        NotificationManager notificationManager
                = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public boolean isPlaying() {
        if (exoPlayer != null) {
            return exoPlayer.getPlaybackState() == Player.STATE_READY && exoPlayer.getPlayWhenReady();
        } else {
            return false;
        }
    }

    public void pauseRadio() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
        }
    }

    public SimpleExoPlayer getExoPlayer() {
        return exoPlayer;
    }

    public void setServiceCallbacks(ServiceCallbacks serviceCallbacks) {
        this.serviceCallbacks = serviceCallbacks;
    }

    public class PlayRadioBinder extends Binder {
        public PlayRadioService getService() {
            return PlayRadioService.this;
        }
    }

    public interface ServiceCallbacks {
        void updateRadiosFragment(int statusCode);
        void updateFavouriteRadiosFragment(int statusCode);
        void togglePlayPauseButton();
        void updateVolumeBar(int streamMaxVolume);
    }
}
