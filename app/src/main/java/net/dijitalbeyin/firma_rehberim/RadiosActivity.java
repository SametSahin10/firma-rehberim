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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.PopupMenu;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import net.dijitalbeyin.firma_rehberim.data.RadioContract;
import net.dijitalbeyin.firma_rehberim.data.RadioDbHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RadiosActivity extends FragmentActivity implements RadiosFragment.OnEventFromRadiosFragmentListener,
                                                                FavouriteRadiosFragment.OnEventFromFavRadiosFragment,
                                                                RadiosFragment.OnRadioItemClickListener,
                                                                FavouriteRadiosFragment.OnFavRadioItemClickListener,
                                                                PopupMenu.OnMenuItemClickListener,
                                                                AdapterView.OnItemClickListener {
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

    TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory();
    TrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);

    PopupMenu popupMenu;

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
                            Log.d(LOG_TAG, "onPlayerStateChanged: Radio is selected from favourite radios");
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

        ib_player_menu = findViewById(R.id.ib_player_menu);
        ib_player_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                popupMenu = new PopupMenu(v.getContext(), v);
//                popupMenu.setOnMenuItemClickListener(RadiosActivity.this);
//                MenuInflater menuInflater = popupMenu.getMenuInflater();
//                menuInflater.inflate(R.menu.player_menu, popupMenu.getMenu());
//                if (radioCurrentlyPlaying != null) {
//                    if (radioCurrentlyPlaying.isLiked()) {
//                        popupMenu.getMenu().findItem(R.id.menu_item_add_to_fav).setEnabled(false);
//                    } else {
//                        popupMenu.getMenu().findItem(R.id.menu_item_add_to_fav).setEnabled(true);
//                    }
//                }
//                popupMenu.show();
                addItem(R.drawable.ic_share);
                addItem(R.drawable.ic_favourite_empty);
                showPopupMenu(view);
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_share:
                //Share currently playing radio;
                shareRadio(radioCurrentlyPlaying);
                return true;
            case R.id.menu_item_add_to_fav:
                //Add currently playing radio to favourites;
                addToFavourites(radioCurrentlyPlaying);
                favouriteRadiosFragment.updateFavouriteRadiosList();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Implement on popup menu item click.
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

    //ListPopupMenu implementation
    private static final String ICON = "ICON";

    private List<HashMap<String, Object>> data = new ArrayList<>();

    private void addItem(int imageResourceId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put(ICON, imageResourceId);
        data.add(map);
    }

    private void showPopupMenu(View anchor) {
        ListPopupWindow popupWindow = new ListPopupWindow(this);
        ListAdapter listAdapter = new SimpleAdapter(
                                        this,
                                        data,
                                        R.layout.list_popup_window,
                                        new String[] {ICON},
                                        new int[] {R.id.ib_popup_menu_item});
        popupWindow.setAnchorView(anchor);
        popupWindow.setAdapter(listAdapter);
        popupWindow.setWidth(150);
        popupWindow.setHeight(305);
        popupWindow.setOnItemClickListener(this);
        popupWindow.show();
    }

    private void prepareExoPlayer(Uri uri) {
        dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), Util.getUserAgent(getApplicationContext(), "exoPlayerSimple"), BANDWIDTH_METER);
        mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        exoPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector);
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
        radioCurrentlyPlaying = radioClicked;
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
    public void onEventFromRadiosFragment() {
        notifyFavouriteRadiosFragment();
    }

    @Override
    public void onEventFromFavRadiosFragment(int radioId) {
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
