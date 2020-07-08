package com.firmarehberim.canliradyo.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
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

import com.firmarehberim.canliradyo.activities.RadiosActivity;
import com.firmarehberim.canliradyo.receivers.BecomingNoisyReceiver;
import com.firmarehberim.canliradyo.receivers.OnCancelBroadcastReceiver;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import com.firmarehberim.canliradyo.datamodel.PlaybackStatus;
import com.firmarehberim.canliradyo.datamodel.Radio;
import com.firmarehberim.canliradyo.R;

public class PlayRadioService extends Service implements AudioManager.OnAudioFocusChangeListener {
    private static final String LOG_TAG = PlayRadioService.class.getSimpleName();
    private static final int STATE_BUFFERING = 10;
    private static final int STATE_READY = 11;
    private static final int STATE_IDLE = 12;
    private static final int NOTIFICATION_ID = 101;

    public static final String ACTION_PLAY = "com.firmarehberim.canliradyo.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.firmarehberim.canliradyo.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.firmarehberim.canliradyo.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.firmarehberim.canliradyo.ACTION_NEXT";
    public static final String ACTION_STOP = "com.firmarehberim.canliradyo.ACTION_STOP";

    private final IBinder binder = new PlayRadioBinder();
    private ServiceCallbacks serviceCallbacks;

    private Radio currentlyPlayingRadio;

    public AudioManager audioManager;
    private AudioFocusRequest audioFocusRequest;
    public SimpleExoPlayer player;

    TrackSelector trackSelector = new DefaultTrackSelector();

    private MediaSessionManager mediaSessionManager;
    private MediaSessionCompat mediaSession;
    private MediaControllerCompat.TransportControls transportControls;

    private IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
    private BecomingNoisyReceiver becomingNoisyReceiver = new BecomingNoisyReceiver();

    private boolean isFromFavouriteRadiosFragment = false;

    public Radio getCurrentlyPlayingRadio() {
        return currentlyPlayingRadio;
    }

    public void setCurrentlyPlayingRadio(Radio currentlyPlayingRadio) {
        this.currentlyPlayingRadio = currentlyPlayingRadio;
    }

    public void setFromFavouriteRadiosFragment(boolean fromFavouriteRadiosFragment) {
        isFromFavouriteRadiosFragment = fromFavouriteRadiosFragment;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initMediaSession();
        Player.EventListener eventListener = new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case Player.STATE_BUFFERING:
                        if (isFromFavouriteRadiosFragment) {
                            serviceCallbacks.updateFavouriteRadiosFragment(STATE_BUFFERING);
                        } else {
                            serviceCallbacks.updateRadiosFragment(STATE_BUFFERING);
                        }
                        break;
                    case Player.STATE_READY:
                        if (isFromFavouriteRadiosFragment) {
                            serviceCallbacks.updateFavouriteRadiosFragment(STATE_READY);
                        } else {
                            serviceCallbacks.updateRadiosFragment(STATE_READY);
                        }
                        if (isPlaying()) {
                            serviceCallbacks.togglePlayPauseButton(false,
                                    isFromFavouriteRadiosFragment);
                        }
                        break;
                    case Player.STATE_IDLE:
                        if (isFromFavouriteRadiosFragment) {
                            serviceCallbacks.updateFavouriteRadiosFragment(STATE_IDLE);
                        } else {
                            serviceCallbacks.updateRadiosFragment(STATE_IDLE);
                        }
                        break;
                    case Player.STATE_ENDED:
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
        player = new SimpleExoPlayer.Builder(getApplicationContext()).build();
        player.addListener(eventListener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if (intent.getExtras() != null) {
                boolean pauseAudio = intent.getExtras().getBoolean("pauseAudio");
                if (pauseAudio) transportControls.pause();

                if (intent.getExtras().containsKey("showNotification")) {
                    boolean showNotification = intent.getExtras().getBoolean("showNotification");
                    if (showNotification) {
                        if (mediaSessionManager == null) {
                            initMediaSession();
                        }
                        initNotification(PlaybackStatus.PLAYING);
                    } else {
                        if (player != null) {
                            player.setPlayWhenReady(false);
                            player.release();
                        }
                        serviceCallbacks.togglePlayPauseButton(
                            true,
                            isFromFavouriteRadiosFragment
                        );
                        relieveAudioFocus();
                        stopSelf();
                    }
                }
            }
            handleIncomingActions(intent);
        }
        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        relieveAudioFocus();
        stopSelf();
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player.setPlayWhenReady(false);
            player.release();
        }
        unregisterReceiver(becomingNoisyReceiver);
        relieveAudioFocus();
        cancelNotification();
        super.onDestroy();
    }

    private void prepareExoPlayer(Uri uri, boolean isRadioInHLSFormat) {
        String userAgent = Util.getUserAgent(
                getApplicationContext(),
                "com.firmarehberim.canliradyo"
        );
        if (isRadioInHLSFormat) {
            DefaultHttpDataSourceFactory dataSourceFactory =
                    new DefaultHttpDataSourceFactory(userAgent);
            HlsMediaSource hlsMediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(uri);
            player.addAnalyticsListener(new EventLogger((MappingTrackSelector) trackSelector));
            player.prepare(hlsMediaSource);
        } else {
            DefaultDataSourceFactory dataSourceFactory =
                    new DefaultDataSourceFactory(getApplicationContext(), userAgent);
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(uri);
            player.addAnalyticsListener(new EventLogger((MappingTrackSelector) trackSelector));
            player.prepare(mediaSource);
        }
        player.setPlayWhenReady(true);
    }

    public void playRadio(Radio radioClicked) {
        currentlyPlayingRadio = radioClicked;
        initNotification(PlaybackStatus.PLAYING);
        if (player != null) {
            if (isPlaying()) {
                player.setPlayWhenReady(false);
                player.stop(true);
            }
        }
        String streamLink = radioClicked.getStreamLink();
        prepareExoPlayer(Uri.parse(streamLink), radioClicked.isInHLSFormat());
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

        Intent onCancelIntent = new Intent(this, OnCancelBroadcastReceiver.class);
        PendingIntent onCancelPendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            onCancelIntent,
            0
        );

        String channelId = "newChannelId";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = getString(R.string.app_name);
            NotificationChannel notificationChannel = new NotificationChannel(
                channelId,
                channelId,
                NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationChannel.setDescription(channelId);
            notificationChannel.setSound(null, null);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

        Bitmap largeIcon = BitmapFactory.decodeResource(
            getResources(), R.drawable.ic_placeholder_radio
        );

        Intent intentToStartRadiosActivity = new Intent(this, RadiosActivity.class);
        intentToStartRadiosActivity.putExtra("activityStartedFromNotification", true);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this,
                0,
                intentToStartRadiosActivity,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        final NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, channelId);
        builder.setShowWhen(false)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.getSessionToken())
                    .setShowActionsInCompactView(0, 1, 2)
                )
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(currentlyPlayingRadio.getRadioName())
                .setContentText("Radyo çalınıyor")
                .setContentInfo("Firma Rehberim Radyo")
                .setDeleteIntent(onCancelPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(false)
                .addAction(
                    android.R.drawable.ic_media_previous,
                    "previous",
                    generatePlaybackAction(3)
                )
                .addAction(notificationAction, "pause", playPauseAction)
                .addAction(
                    android.R.drawable.ic_media_next,
                    "next",
                    generatePlaybackAction(2)
                );

        builder.setContentIntent(contentIntent);

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                builder.setLargeIcon(bitmap);
                startForeground(112, builder.build());
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.e(LOG_TAG, "Loading Bitmap failed");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };

        if (currentlyPlayingRadio != null) {
            Picasso.get().load(currentlyPlayingRadio.getRadioIconUrl()).into(target);
        }

        startForeground(112, builder.build());
    }

    private void cancelNotification() {
        NotificationManager notificationManager
                = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void initMediaSession() {
        if (mediaSessionManager != null) {
            return;
        }
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
                registerReceiver(becomingNoisyReceiver, intentFilter);
                boolean audioFocusGained = gainAudioFocus();
                if (audioFocusGained) {
                    initNotification(PlaybackStatus.PLAYING);
                    if (currentlyPlayingRadio != null) {
                        String streamLink = currentlyPlayingRadio.getStreamLink();
                        prepareExoPlayer(Uri.parse(streamLink), currentlyPlayingRadio.isInHLSFormat());
                    }
                }
            }

            @Override
            public void onPause() {
                super.onPause();
                unregisterReceiver(becomingNoisyReceiver);
                initNotification(PlaybackStatus.PAUSED);
                pauseRadio();
                serviceCallbacks.togglePlayPauseButton(true, isFromFavouriteRadiosFragment);
                stopForeground(false);
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
                return PendingIntent.getService(
                    this,
                    actionNumber,
                    playbackAction,
                    0
                );
            case 1:
                playbackAction.setAction(ACTION_PAUSE);
                return PendingIntent.getService(
                    this,
                    actionNumber,
                    playbackAction,
                    0
                );
            case 2:
                playbackAction.setAction(ACTION_NEXT);
                return PendingIntent.getService(
                    this,
                    actionNumber,
                    playbackAction,
                    0
                );
            case 3:
                playbackAction.setAction(ACTION_PREVIOUS);
                return PendingIntent.getService(
                    this,
                    actionNumber,
                    playbackAction,0
                );
            default:
                break;
        }
        return null;
    }

    private void handleIncomingActions(Intent playbackAction) {
        if (playbackAction == null || playbackAction.getAction() == null) return;
        String actionString = playbackAction.getAction();
        if (actionString.equalsIgnoreCase(ACTION_PLAY)) {
            transportControls.play();
            serviceCallbacks.togglePlayPauseButton(false, isFromFavouriteRadiosFragment);
        } else if (actionString.equalsIgnoreCase(ACTION_PAUSE)) {
            transportControls.pause();
            serviceCallbacks.togglePlayPauseButton(true, isFromFavouriteRadiosFragment);
        } else if (actionString.equalsIgnoreCase(ACTION_NEXT)) {
            transportControls.skipToNext();
        } else if (actionString.equalsIgnoreCase(ACTION_PREVIOUS)) {
            transportControls.skipToPrevious();
        } else if (actionString.equalsIgnoreCase(ACTION_STOP)) {
            transportControls.stop();
        }
    }

    public boolean isPlaying() {
        if (player != null) {
            return player.getPlaybackState() == Player.STATE_READY && player.getPlayWhenReady();
        } else {
            return false;
        }
    }

    public void pauseRadio() {
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public MediaControllerCompat.TransportControls getTransportControls() {
        return transportControls;
    }

    public void setServiceCallbacks(ServiceCallbacks serviceCallbacks) {
        this.serviceCallbacks = serviceCallbacks;
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        // Will implement this later.
        if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            transportControls.play();
        } else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
            transportControls.pause();
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            transportControls.pause();
        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
            transportControls.pause();
            relieveAudioFocus();
        } else {
        }
    }

    public class PlayRadioBinder extends Binder {
        public PlayRadioService getService() {
            return PlayRadioService.this;
        }
    }

    public interface ServiceCallbacks {
        void updateRadiosFragment(int statusCode);
        void updateFavouriteRadiosFragment(int statusCode);
        void togglePlayPauseButton(boolean isPaused, boolean isFavouriteRadio);
    }

    public boolean gainAudioFocus() {
        boolean playBackNowAuthorized;
        int response;

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            audioFocusRequest = new AudioFocusRequest.Builder(
                AudioManager.AUDIOFOCUS_GAIN
            ).setAudioAttributes(audioAttributes)
             .setAcceptsDelayedFocusGain(false)
             .setOnAudioFocusChangeListener(this)
             .build();
            response = audioManager.requestAudioFocus(audioFocusRequest);
            switch (response) {
                case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                    playBackNowAuthorized = false;
                    return playBackNowAuthorized;
                case AudioManager.AUDIOFOCUS_REQUEST_GRANTED:
                    playBackNowAuthorized = true;
                    return playBackNowAuthorized;
                default:
                    return false;
            }
        }
        // If Build version is lower than 26
        response = audioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        switch (response) {
            case AudioManager.AUDIOFOCUS_REQUEST_FAILED:
                playBackNowAuthorized = false;
                return playBackNowAuthorized;
            case AudioManager.AUDIOFOCUS_REQUEST_GRANTED:
                playBackNowAuthorized = true;
                return playBackNowAuthorized;
            default:
                return false;
        }
    }

    private void relieveAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (audioFocusRequest != null) {
                audioManager.abandonAudioFocusRequest(audioFocusRequest);
            }
        } else {
            audioManager.abandonAudioFocus(this);
        }
    }
}
