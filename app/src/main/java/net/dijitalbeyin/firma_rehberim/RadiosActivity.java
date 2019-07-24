package net.dijitalbeyin.firma_rehberim;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import net.dijitalbeyin.firma_rehberim.data.RadioContract;
import net.dijitalbeyin.firma_rehberim.data.RadioDbHelper;

import okhttp3.OkHttpClient;

public class RadiosActivity extends FragmentActivity implements RadiosFragment.OnEventFromRadiosFragmentListener,
                                                                FavouriteRadiosFragment.OnEventFromFavRadiosFragment,
                                                                RadiosFragment.OnRadioItemClickListener,
                                                                FavouriteRadiosFragment.OnFavRadioItemClickListener {
    private static final String LOG_TAG = RadiosActivity.class.getSimpleName();
    private static final int STATE_BUFFERING = 10;
    private static final int STATE_READY = 11;
    private static final int STATE_IDLE = 12;
    private static final int NUM_PAGES = 2;

    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    private RadiosFragment radiosFragment;
    private FavouriteRadiosFragment favouriteRadiosFragment;

    private ImageView iv_radioIcon;
    private TextView  tv_radioTitle;
    private ImageButton ib_playPauseRadio;
    private ImageButton ib_player_menu;

    private SimpleExoPlayer exoPlayer;
    private MediaSource mediaSource;
    private DefaultDataSourceFactory dataSourceFactory;
    private ExoPlayer.EventListener eventListener;
    private DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    TrackSelector trackSelector = new DefaultTrackSelector();

    PopupWindow popupWindow;

    Radio radioCurrentlyPlaying;
    boolean isFromFavouriteRadiosFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);

        radiosFragment = new RadiosFragment();
        favouriteRadiosFragment = new FavouriteRadiosFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, radiosFragment).commit();

        viewPager = findViewById(R.id.view_pager);
        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/righteous_regular.ttf");
        int numOfTabs = tabLayout.getTabCount();
        for (int i = 0; i < numOfTabs; i++) {
            TextView tv_tab_title = (TextView) LayoutInflater.from(this)
                                    .inflate(R.layout.custom_textview_for_tab_titles, null);
            tv_tab_title.setTypeface(typeface);
            tabLayout.getTabAt(i).setCustomView(tv_tab_title);
        }

        iv_radioIcon = findViewById(R.id.iv_radio_icon);
        tv_radioTitle = findViewById(R.id.tv_radio_title);

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
                        radiosFragment.setCurrentRadioStatus(STATE_READY, radioCurrentlyPlaying);
                        if (isPlaying()) {
                            ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_radio));
                        }
                        Log.d("TAG", "STATE_READY");
                        break;
                    case ExoPlayer.STATE_IDLE:
                        Log.d("TAG", "STATE_IDLE");
                        exoPlayer.release();
                        radiosFragment.setCurrentRadioStatus(STATE_IDLE, radioCurrentlyPlaying);
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

        ib_playPauseRadio = findViewById(R.id.ib_play_radio);
        ib_playPauseRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying()) {
                    exoPlayer.setPlayWhenReady(false);
                    ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_radio));
                } else {
                    exoPlayer.setPlayWhenReady(true);
                    ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_radio));
                }
            }
        });

        setupPopupWindow();

        ib_player_menu = findViewById(R.id.ib_player_menu);
        ib_player_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.showAsDropDown(view, 0, -500);
            }
        });
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof RadiosFragment) {
            RadiosFragment radiosFragment = (RadiosFragment) fragment;
            radiosFragment.setOnEventFromRadiosFragmentListener(this);
            radiosFragment.setOnRadioItemClickListener(this);
        }

        if (fragment instanceof FavouriteRadiosFragment) {
            FavouriteRadiosFragment favouriteRadiosFragment = (FavouriteRadiosFragment) fragment;
            favouriteRadiosFragment.setOnEventFromFavRadiosFragment(this);
            favouriteRadiosFragment.setOnFavRadioItemClickListener(this);
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new RadiosFragment();
                case 1:
                    return new FavouriteRadiosFragment();
                default:
                    return null;
            }
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            switch (position) {
                case 0:
                    radiosFragment = (RadiosFragment) createdFragment;
                    break;
                case 1:
                    favouriteRadiosFragment = (FavouriteRadiosFragment) createdFragment;
                    break;
            }
            return createdFragment;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.all_the_radios_tab_title);
                case 1:
                    return getString(R.string.favourite_radios_tab_title);
                default:
                    return "Unknown tab title";
            }
        }
    }

    private void setupPopupWindow() {
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.list_popup_window, null);
        ImageButton ib_popup_window_share = view.findViewById(R.id.ib_popup_window_share);
        ib_popup_window_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //share currently playing radio
                if (radioCurrentlyPlaying != null) {
                    shareRadio(radioCurrentlyPlaying);
                }
            }
        });
        ImageButton ib_popup_window_fav = view.findViewById(R.id.ib_popup_window_fav);
        ib_popup_window_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add currently playing radio to favourites.
                if (radioCurrentlyPlaying != null) {
                    Log.d(LOG_TAG, "radio currently playing is not null");
                    radioCurrentlyPlaying.setLiked(true);
                    addToFavourites(radioCurrentlyPlaying);
                    favouriteRadiosFragment.updateFavouriteRadiosList();
                } else {
                    Log.d(LOG_TAG, "radio currently playing is null");
                }
            }
        });
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        int width = (int) (60 * scale + 0.5f);
        int height = (int) (120 * scale + 0.5f);
        popupWindow = new PopupWindow(view,
                                    width,
                                    height,
                                    true);
        updatePopupWindow();
    }

    private void prepareExoPlayer(Uri uri) {
        dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), Util.getUserAgent(getApplicationContext(), "exoPlayerSimple"), BANDWIDTH_METER);
        String userAgent = Util.getUserAgent(getApplicationContext(), "exoPlayerSimple");
        mediaSource = new ExtractorMediaSource(uri,
                new OkHttpDataSourceFactory(new OkHttpClient(), userAgent, (TransferListener) null),
                new DefaultExtractorsFactory(),
                null,
                null);
        exoPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector);
        exoPlayer.addAnalyticsListener(new EventLogger((MappingTrackSelector) trackSelector));
        exoPlayer.addListener(eventListener);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
    }

    private boolean isPlaying() {
        if (exoPlayer != null) {
            return exoPlayer.getPlaybackState() == Player.STATE_READY && exoPlayer.getPlayWhenReady();
        } else {
            return false;
        }
    }

    private void playRadio(Radio radioClicked) {
        Log.d("TAG", "Radio stream link: " + radioClicked.getStreamLink());
        radioCurrentlyPlaying = radioClicked;
        updatePopupWindow();
        if (exoPlayer != null) {
            exoPlayer.release();
            if (isPlaying()) {
                exoPlayer.setPlayWhenReady(false);
                exoPlayer.stop(true);
            }
        }
        String streamLink = radioClicked.getStreamLink();
        prepareExoPlayer(Uri.parse(streamLink));
        tv_radioTitle.setText(radioClicked.getRadioName());
        String iconUrl = radioClicked.getRadioIconUrl();
        Picasso.with(getApplicationContext()).load(iconUrl)
                .resize(200, 200)
                .centerInside()
                .placeholder(R.drawable.ic_placeholder_radio_black)
                .error(R.drawable.ic_pause_radio)
                .into(iv_radioIcon);
    }

    private void updatePopupWindow() {
        if (radioCurrentlyPlaying != null) {
            ImageButton ib_popup_window_fav = popupWindow.getContentView().findViewById(R.id.ib_popup_window_fav);
            if (radioCurrentlyPlaying.isLiked()) {
                ib_popup_window_fav.setImageDrawable(getDrawable(R.drawable.ic_favourite_checked));
            } else {
                ib_popup_window_fav.setImageDrawable(getDrawable(R.drawable.ic_favourite_empty));
            }
        }
    }

    private void shareRadio(Radio radio) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        String extraText = "I'm listening to " + radio.getRadioName() + " on " + "Firma Rehberim Radyo";
        intent.putExtra(Intent.EXTRA_TEXT, extraText);
        intent.setType("text/plain");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void addToFavourites(Radio radio) {
        RadioDbHelper dbHelper = new RadioDbHelper(this);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RadioContract.RadioEntry.COLUMN_RADIO_ID, radio.getRadioId());
        contentValues.put(RadioContract.RadioEntry.COLUMN_RADIO_NAME, radio.getRadioName());
        contentValues.put(RadioContract.RadioEntry.COLUMN_RADIO_CATEGORY, radio.getCategory());
        contentValues.put(RadioContract.RadioEntry.COLUMN_RADIO_ICON_URL, radio.getRadioIconUrl());
        contentValues.put(RadioContract.RadioEntry.COLUMN_RADIO_STREAM_LINK, radio.getStreamLink());
        contentValues.put(RadioContract.RadioEntry.COLUMN_RADIO_SHAREABLE_LINK, radio.getShareableLink());
        contentValues.put(RadioContract.RadioEntry.COLUMN_RADIO_HIT, radio.getHit());
        contentValues.put(RadioContract.RadioEntry.COLUMN_NUM_OF_ONLINE_LISTENERS, radio.getNumOfOnlineListeners());
        contentValues.put(RadioContract.RadioEntry.COLUMN_RADIO_IS_BEING_BUFFERED, radio.isBeingBuffered());
        contentValues.put(RadioContract.RadioEntry.COLUMN_RADIO_IS_LIKED, radio.isLiked());
        long newRowId = sqLiteDatabase.insert(RadioContract.RadioEntry.TABLE_NAME, null, contentValues);
        Log.d(LOG_TAG, "newRowId: " + newRowId);
    }

    public void notifyFavouriteRadiosFragment() {
        if (favouriteRadiosFragment != null) {
            favouriteRadiosFragment.updateFavouriteRadiosList();
        }
    }

    @Override
    public void onEventFromRadiosFragment(int radioId, boolean isLiked) {
        if (radioCurrentlyPlaying != null) {
            if (radioId == radioCurrentlyPlaying.getRadioId()) {
                if (isLiked) {
                    radioCurrentlyPlaying.setLiked(true);
                    updatePopupWindow();
                } else {
                    radioCurrentlyPlaying.setLiked(false);
                    updatePopupWindow();
                }
            }
        }
        notifyFavouriteRadiosFragment();
    }

    @Override
    public void onEventFromFavRadiosFragment(int radioId) {
        if (radioCurrentlyPlaying != null) {
            if (radioId == radioCurrentlyPlaying.getRadioId()) {
                radioCurrentlyPlaying.setLiked(false);
                updatePopupWindow();
            }
        }
        radiosFragment.refreshRadiosList(radioId);
    }

    @Override
    public void onRadioItemClick(Radio radioClicked) {
        playRadio(radioClicked);
        isFromFavouriteRadiosFragment = false;
    }

    @Override
    public void onFavRadioItemClick(Radio currentFavRadio) {
        playRadio(currentFavRadio);
        //Can make some adjustments. For example disabling the "Add to favourites" menu option.
        isFromFavouriteRadiosFragment = true;
    }
}
