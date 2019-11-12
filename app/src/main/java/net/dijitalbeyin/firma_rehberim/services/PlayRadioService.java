package net.dijitalbeyin.firma_rehberim.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import net.dijitalbeyin.firma_rehberim.datamodel.PlaybackStatus;
import net.dijitalbeyin.firma_rehberim.datamodel.Radio;
import net.dijitalbeyin.firma_rehberim.R;

import okhttp3.OkHttpClient;

public class PlayRadioService extends Service {
    private static final String LOG_TAG = PlayRadioService.class.getSimpleName();
    private static final int STATE_BUFFERING = 10;
    private static final int STATE_READY = 11;
    private static final int STATE_IDLE = 12;
    private static final int NOTIFICATION_ID = 101;

    public static final String ACTION_PLAY = "net.dijitalbeyin.firma_rehberim.ACTION_PLAY";
    public static final String ACTION_PAUSE = "net.dijitalbeyin.firma_rehberim.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "net.dijitalbeyin.firma_rehberim.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "net.dijitalbeyin.firma_rehberim.ACTION_NEXT";
    public static final String ACTION_STOP = "net.dijitalbeyin.firma_rehberim.ACTION_STOP";

    private final IBinder binder = new PlayRadioBinder();
    private ServiceCallbacks serviceCallbacks;

    Radio radioCurrentlyPlaying;

    public AudioManager audioManager;
    public SimpleExoPlayer exoPlayer;
    private MediaSource mediaSource;
    private DefaultDataSourceFactory dataSourceFactory;
    private ExoPlayer.EventListener eventListener;
    private DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    TrackSelector trackSelector = new DefaultTrackSelector();

    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    boolean isFromFavouriteRadiosFragment = false;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initMediaSession();
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
                            serviceCallbacks.togglePlayPauseButton(false);
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
        if (intent != null) {
            if (intent.getExtras() != null) {
                boolean showNotification = intent.getExtras().getBoolean("showNotification");
                if (showNotification) {
                    if (mediaSessionManager == null) {
                        initMediaSession();
                    }
                    initNotification(PlaybackStatus.PLAYING);
                }
            }
            handleIncomingActions(intent);
        }
        Log.d(LOG_TAG, "onStartCommand()");
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
        radioCurrentlyPlaying = radioClicked;
        initNotification(PlaybackStatus.PLAYING);
        if (exoPlayer != null) {
            if (isPlaying()) {
                exoPlayer.setPlayWhenReady(false);
                exoPlayer.stop(true);
            }
        }
        String streamLink = radioClicked.getStreamLink();
        prepareExoPlayer(Uri.parse(streamLink));
    }

    private void initNotification(PlaybackStatus playbackStatus) {
        PendingIntent playPauseAction = null;
        int notificationAction = android.R.drawable.ic_media_pause;
        if (playbackStatus == PlaybackStatus.PLAYING) {
            notificationAction = android.R.drawable.ic_media_pause;
            playPauseAction = generatePlaybackAction(1);
        } else if (playbackStatus == PlaybackStatus.PAUSED) {
            notificationAction = android.R.drawable.ic_media_play;
            playPauseAction = generatePlaybackAction(0);
        }

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


        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_placeholder_radio);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setShowWhen(false)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                            .setMediaSession(mediaSession.getSessionToken())
                            .setShowActionsInCompactView(0, 1, 2))
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(radioCurrentlyPlaying.getRadioName())
                .setContentText("Radyo çalınıyor")
                .setContentInfo("Firma Rehberim Radyo")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(false)
                .addAction(android.R.drawable.ic_media_previous, "previous", generatePlaybackAction(3))
                .addAction(notificationAction, "pause", playPauseAction)
                .addAction(android.R.drawable.ic_media_next, "next", generatePlaybackAction(2));

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                builder.setLargeIcon(bitmap);
                startForeground(112, builder.build());
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.e(LOG_TAG, "Loading Bitmap failed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        if (radioCurrentlyPlaying != null) {
            Picasso.with(this).load(radioCurrentlyPlaying.getRadioIconUrl()).into(target);
        }

        startForeground(112, builder.build());
    }

    private void cancelNotification() {
        NotificationManager notificationManager
                = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void initMediaSession() {
        if (mediaSessionManager != null) return;
        mediaSessionManager = (MediaSessionManager) getSystemService(MEDIA_SESSION_SERVICE);
        mediaSession = new MediaSessionCompat(this, "RadioPlayer");
        transportControls = mediaSession.getController().getTransportControls();
        mediaSession.setActive(true);
        mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        // updateMetadata
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                initNotification(PlaybackStatus.PLAYING);
                if (exoPlayer != null) {
                    exoPlayer.setPlayWhenReady(true);
                    serviceCallbacks.togglePlayPauseButton(false);
                }
            }

            @Override
            public void onPause() {
                super.onPause();
                initNotification(PlaybackStatus.PAUSED);
                pauseRadio();
                serviceCallbacks.togglePlayPauseButton(true);
                Log.d(LOG_TAG, "initMediaSession() onPause()");
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
            }

            @Override
            public void onStop() {
                super.onStop();
                cancelNotification();
                stopSelf();
            }
        });
    }

    private PendingIntent generatePlaybackAction(int actionNumber) {
        Intent playbackAction = new Intent(this, PlayRadioService.class);
        switch (actionNumber) {
            case 0:
                playbackAction.setAction(ACTION_PLAY);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 1:
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 2:
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            case 3:
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(this, actionNumber, playbackAction, 0);
            default:
                break;
        }
        return null;
    }

    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;
        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            Log.d(LOG_TAG, "handleIncomingActions() ACTION_PLAY");
            transportControls.play();
            serviceCallbacks.togglePlayPauseButton(false);
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            Log.d(LOG_TAG, "handleIncomingActions() ACTION_PAUSE");
            transportControls.pause();
            serviceCallbacks.togglePlayPauseButton(true);
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
            Log.d(LOG_TAG, "handleIncomingActions() ACTION_NEXT");
            transportControls.skipToNext();
        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
            Log.d(LOG_TAG, "handleIncomingActions() ACTION_PREVIOUS");
            transportControls.skipToPrevious();
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            Log.d(LOG_TAG, "handleIncomingActions() ACTION_STOP");
            transportControls.stop();
        }
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
        void togglePlayPauseButton(boolean isPaused);
        void updateVolumeBar(int streamMaxVolume);
    }
}
