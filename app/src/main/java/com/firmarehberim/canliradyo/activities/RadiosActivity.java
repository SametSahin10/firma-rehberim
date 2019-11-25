package com.firmarehberim.canliradyo.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.firmarehberim.canliradyo.R;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.squareup.picasso.Picasso;

import com.firmarehberim.canliradyo.fragments.CategoriesFragment;
import com.firmarehberim.canliradyo.fragments.CitiesFragment;
import com.firmarehberim.canliradyo.fragments.ContactsFragment;
import com.firmarehberim.canliradyo.fragments.FavouriteRadiosFragment;
import com.firmarehberim.canliradyo.fragments.NewspaperFragment;
import com.firmarehberim.canliradyo.helper.QueryUtils;
import com.firmarehberim.canliradyo.fragments.RadiosFragment;
import com.firmarehberim.canliradyo.fragments.TimerFragment;
import com.firmarehberim.canliradyo.fragments.TvFragment;
import com.firmarehberim.canliradyo.adapters.CategoryAdapter;
import com.firmarehberim.canliradyo.adapters.CityAdapter;
import com.firmarehberim.canliradyo.data.RadioContract;
import com.firmarehberim.canliradyo.data.RadioDbHelper;
import com.firmarehberim.canliradyo.datamodel.Category;
import com.firmarehberim.canliradyo.datamodel.City;
import com.firmarehberim.canliradyo.datamodel.Radio;
import com.firmarehberim.canliradyo.services.PlayRadioService;
import java.util.ArrayList;
import java.util.List;
import com.firmarehberim.canliradyo.services.PlayRadioService.PlayRadioBinder;

public class RadiosActivity extends AppCompatActivity implements RadiosFragment.OnEventFromRadiosFragmentListener,
        FavouriteRadiosFragment.OnEventFromFavRadiosFragment,
        RadiosFragment.OnRadioItemClickListener,
        RadiosFragment.OnRadioLoadingCompleteListener,
        RadiosFragment.OnRadioLoadingStartListener,
        FavouriteRadiosFragment.OnFavRadioItemClickListener,
        CitiesFragment.OnFilterRespectToCityListener,
        CategoriesFragment.OnFilterRespectToCategoryListener,
        TimerFragment.OnCountdownFinishedListener,
        LoaderManager.LoaderCallbacks<List<Object>>,
        PlayRadioService.ServiceCallbacks {
    private static final String LOG_TAG = RadiosActivity.class.getSimpleName();
    private static final String CITIES_REQUEST_URL = "https://firmarehberim.com/sayfalar/radyo/json/iller.php";
    private static final String CATEGORY_REQUEST_URL = "https://firmarehberim.com/sayfalar/radyo/json/kategoriler.php";
    private static final int CITY_LOADER_ID = 1;
    private static final int CATEGORY_LOADER_ID = 2;
    private static final int STATE_BUFFERING = 10;
    private static final int STATE_READY = 11;
    private static final int STATE_IDLE = 12;
    private static final int NUM_PAGES = 2;
    private final static int FILTER_TYPE_RADIO = 1;
    private final static int FILTER_TYPE_CITY = 2;
    private final static int FILTER_TYPE_CATEGORY = 3;

    private final static int RADIOS_FRAGMENT_ID = 1;
    private final static int TV_FRAGMENT_ID = 2;
    private final static int CONTACTS_FRAGMENT_ID = 3;
    private final static int NEWSPAPER_FRAGMENT_ID = 4;
    private final static int FAV_RADIOS_FRAGMENT_ID = 5;
    private final static int CATEGORIES_FRAGMENT_ID = 6;
    private final static int CITIES_FRAGMENT_ID = 7;
    private final static int GLOBAL_FRAGMENT_ID = 8;
    private final static int TIMER_FRAGMENT_ID = 9;
    private int ACTIVE_FRAGMENT_ID;


    PlayRadioService playRadioService;
    boolean serviceBound = false;

    private Toolbar toolbar;
    private SearchView sw_searchForRadios;
    private ImageView iv_searchIcon;
    private EditText et_queryText;
    private Spinner spinner_cities;
    private Spinner spinner_categories;
    private CityAdapter cityAdapter;
    private CategoryAdapter categoryAdapter;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    private RadiosFragment radiosFragment;
    private FavouriteRadiosFragment favouriteRadiosFragment;
    private TimerFragment timerFragment;

    private Button btn_nav_radios;
    private Button btn_nav_tv;
    private Button btn_nav_contacts;
    private Button btn_nav_newspaper;
    private Button btn_nav_fav_radios;
    private Button btn_nav_categories;
    private Button btn_nav_cities;
    private Button btn_nav_global;
    private ImageButton ib_search_for_radios;

    private MenuItem action_search;
    private SearchView searchView;

    private ImageButton ib_timer;
    private ImageButton ib_volume_control;
    private SeekBar sb_volume_control;
    private ImageButton ib_playPauseRadio;

    private ImageView iv_radioIcon;
    private TextView  tv_radioTitle;
    private ImageButton ib_share_radio;
    private ImageButton ib_player_add_to_fav;

    private AudioManager audioManager;

    PopupWindow popupWindow;

    Radio radioCurrentlyPlaying;
    boolean isFromFavouriteRadiosFragment = false;
    boolean isRadioLoadingCompleted = false;
    boolean isAudioStreamMuted = false;

    City allTheCities;
    Category allTheCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);


        ACTIVE_FRAGMENT_ID = RADIOS_FRAGMENT_ID;
        radiosFragment = new RadiosFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, radiosFragment).commit();

        btn_nav_radios = findViewById(R.id.btn_nav_radios);
        btn_nav_radios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ACTIVE_FRAGMENT_ID != RADIOS_FRAGMENT_ID) {
                    getSupportActionBar().setTitle("Firma Rehberim Radyo");
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, radiosFragment).commit();
                    ACTIVE_FRAGMENT_ID = RADIOS_FRAGMENT_ID;
                }
            }
        });

        btn_nav_tv = findViewById(R.id.btn_nav_tv);
        btn_nav_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ACTIVE_FRAGMENT_ID != TV_FRAGMENT_ID) {
                    getSupportActionBar().setTitle("TV");
                    TvFragment tvFragment = new TvFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, tvFragment).commit();
                    ACTIVE_FRAGMENT_ID = TV_FRAGMENT_ID;
                }
            }
        });

        btn_nav_contacts = findViewById(R.id.btn_nav_contacts);
        btn_nav_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ACTIVE_FRAGMENT_ID != CONTACTS_FRAGMENT_ID) {
                    getSupportActionBar().setTitle("Rehber");
                    ContactsFragment contactsFragment = new ContactsFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, contactsFragment).commit();
                    ACTIVE_FRAGMENT_ID = CONTACTS_FRAGMENT_ID;
                }
            }
        });

        btn_nav_newspaper = findViewById(R.id.btn_nav_newspaper);
        btn_nav_newspaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ACTIVE_FRAGMENT_ID != NEWSPAPER_FRAGMENT_ID) {
                    getSupportActionBar().setTitle("Gazete");
                    NewspaperFragment newspaperFragment = new NewspaperFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, newspaperFragment).commit();
                    ACTIVE_FRAGMENT_ID = NEWSPAPER_FRAGMENT_ID;
                }
            }
        });

        btn_nav_fav_radios = findViewById(R.id.btn_nav_fav_radios);
        btn_nav_fav_radios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ACTIVE_FRAGMENT_ID != FAV_RADIOS_FRAGMENT_ID) {
                    getSupportActionBar().setTitle("Favori Radyolar");
                    favouriteRadiosFragment = new FavouriteRadiosFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, favouriteRadiosFragment).commit();
                    ACTIVE_FRAGMENT_ID = FAV_RADIOS_FRAGMENT_ID;
                }
            }
        });

        btn_nav_categories = findViewById(R.id.btn_nav_categories);
        btn_nav_categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ACTIVE_FRAGMENT_ID != CATEGORIES_FRAGMENT_ID) {
                    getSupportActionBar().setTitle("Kategoriler");
                    CategoriesFragment categoriesFragment = new CategoriesFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, categoriesFragment).commit();
                    ACTIVE_FRAGMENT_ID = CATEGORIES_FRAGMENT_ID;
                }
            }
        });

        btn_nav_cities = findViewById(R.id.btn_nav_cities);
        btn_nav_cities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ACTIVE_FRAGMENT_ID != CITIES_FRAGMENT_ID) {
                    getSupportActionBar().setTitle("Şehirler");
                    CitiesFragment citiesFragment = new CitiesFragment();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, citiesFragment).commit();
                    ACTIVE_FRAGMENT_ID = CITIES_FRAGMENT_ID;
                }
            }
        });

        btn_nav_global = findViewById(R.id.btn_nav_global);
        btn_nav_global.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ACTIVE_FRAGMENT_ID != GLOBAL_FRAGMENT_ID) {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    getSupportActionBar().setTitle("Ulusal");
                    radiosFragment.setFilteringRespectToCityEnabled(false);
                    radiosFragment.setFilteringRespectToCategoryEnabled(false);
                    if (ACTIVE_FRAGMENT_ID == RADIOS_FRAGMENT_ID) {
                        fragmentManager.beginTransaction()
                                .detach(fragment)
                                .attach(radiosFragment)
                                .commit();
                        radiosFragment.restartLoader();
                    } else {
                        fragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, radiosFragment)
                                .commit();
                    }
                    ACTIVE_FRAGMENT_ID = GLOBAL_FRAGMENT_ID;
                }
            }
        });

        ib_search_for_radios = findViewById(R.id.ib_search_for_radios);
        ib_search_for_radios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TAG", "Requesting focus");
                action_search.setVisible(true);
                searchView.requestFocus();
                searchView.setIconified(false);
            }
        });

        iv_radioIcon = findViewById(R.id.iv_radio_icon);
        tv_radioTitle = findViewById(R.id.tv_radio_title);

        timerFragment = new TimerFragment();
        ib_timer = findViewById(R.id.ib_timer);
        ib_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ACTIVE_FRAGMENT_ID != TIMER_FRAGMENT_ID) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, timerFragment).commit();
                    getSupportActionBar().setTitle("Zamanlayıcı");
                    ACTIVE_FRAGMENT_ID = TIMER_FRAGMENT_ID;
                }
            }
        });

        ib_volume_control = findViewById(R.id.ib_volume_control);
        ib_volume_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAudioStreamMuted) {
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                    ib_volume_control.setImageDrawable(getDrawable(R.drawable.ic_volume_control));
                    isAudioStreamMuted = false;
                } else {
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                    ib_volume_control.setImageDrawable(getDrawable(R.drawable.ic_volume_control_muted));
                    isAudioStreamMuted = true;
                }
            }
        });

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        sb_volume_control = findViewById(R.id.sb_volume_control_bar);
        sb_volume_control.setProgress(7);
        sb_volume_control.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                SimpleExoPlayer exoPlayer = playRadioService.getExoPlayer();
                if (exoPlayer != null) {
                    Log.d("TAG", "Progress changed");
                    playRadioService.audioManager
                            .setStreamVolume(exoPlayer.getAudioStreamType(), progress, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ib_playPauseRadio = findViewById(R.id.ib_play_radio);
        ib_playPauseRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isConnected = checkConnectivity();
                if (isConnected) {
                    if (playRadioService.isPlaying()) {
                        playRadioService.getTransportControls().pause();
                    } else {
                        playRadioService.getTransportControls().play();
                    }
                } else {
                    Toast.makeText(RadiosActivity.this,
                            "Lütfen internete bağlı olduğunuzdan emin olun",
                                 Toast.LENGTH_SHORT)
                                 .show();
                    ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_radio));
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, PlayRadioService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceBound) {
            unbindService(serviceConnection);
            playRadioService.stopSelf();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.appbar_menu, menu);

        action_search = menu.findItem(R.id.action_search);

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (ACTIVE_FRAGMENT_ID == 1 || ACTIVE_FRAGMENT_ID == 8) {
                    radiosFragment.lw_radios.setVisibility(View.INVISIBLE);
                    radiosFragment.pb_loadingRadios.setVisibility(View.VISIBLE);
                    radiosFragment.setQueryFromSearchView(newText);
                    radiosFragment.setFilteringThroughSearchViewEnabled(true);
                    radiosFragment.restartLoader();
                } else {
                    Toast.makeText(RadiosActivity.this.getApplicationContext(), "Arama yapmak için Radyolar sekmesine geçiniz.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                action_search.setVisible(false);
                hideKeyboard(RadiosActivity.this);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (item.getItemId()) {
            case R.id.item_notifications:
                return true;
            case R.id.item_timer:
                if (ACTIVE_FRAGMENT_ID != TIMER_FRAGMENT_ID) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, timerFragment).commit();
                    getSupportActionBar().setTitle("Zamanlayıcı");
                    ACTIVE_FRAGMENT_ID = TIMER_FRAGMENT_ID;
                }
                return true;
            case R.id.item_add_your_company:
                return true;
            case R.id.item_go_to_website:
                return true;
            case R.id.item_radios:
                return true;
            case R.id.item_add_your_radio:
                return true;
            case R.id.item_contact:
                return true;
            case R.id.item_about:
                Intent privacyPolicyIntent = new Intent(this, AboutActivity.class);
                startActivity(privacyPolicyIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            SimpleExoPlayer exoPlayer = playRadioService.getExoPlayer();
            if (exoPlayer != null) {
                sb_volume_control.setProgress(audioManager.getStreamVolume(exoPlayer.getAudioStreamType()));
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            SimpleExoPlayer exoPlayer = playRadioService.getExoPlayer();
            if (exoPlayer != null) {
                sb_volume_control.setProgress(audioManager.getStreamVolume(exoPlayer.getAudioStreamType()));
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @NonNull
    @Override
    public Loader<List<Object>> onCreateLoader(int loaderId, @Nullable Bundle bundle) {
        SpinnerLoader spinnerLoader = null;
        switch (loaderId) {
            case 1:
                spinnerLoader = new SpinnerLoader(this, CITIES_REQUEST_URL);
                break;
            case 2:
                spinnerLoader = new SpinnerLoader(this, CATEGORY_REQUEST_URL);
                break;
        }
        return spinnerLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Object>> loader, List<Object> objects) {
        int loaderId = loader.getId();
        switch (loaderId) {
            case 1:
                cityAdapter.clear();
                if (objects != null) {
                    cityAdapter.add(allTheCities);
                    cityAdapter.addAll(objects);
                }
                break;
            case 2:
                categoryAdapter.clear();
                if (objects != null) {
                    categoryAdapter.add(allTheCategories);
                    categoryAdapter.addAll(objects);
                }
                break;
        }
//        pb_loadingCities.setVisibility(View.GONE);
//        tv_emptyView.setText(getResources().getString(R.string.empty_cities_text));
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Object>> loader) {
        int loaderId = loader.getId();
        switch (loaderId) {
            case 1:
                cityAdapter.clear();
                break;
            case 2:
                categoryAdapter.clear();
                break;
        }
    }

    private static class SpinnerLoader extends AsyncTaskLoader<List<Object>> {
        private String requestUrl;

        public SpinnerLoader(@NonNull Context context, String requestUrl) {
            super(context);
            this.requestUrl = requestUrl;
        }

        @Override
        public List<Object> loadInBackground() {
            if (requestUrl == null) {
                return null;
            }
            ArrayList<Object> objects = null;
            switch (requestUrl) {
                case CITIES_REQUEST_URL:
                    objects = QueryUtils.fetchCityData(requestUrl);
                    break;
                case CATEGORY_REQUEST_URL:
                    objects = QueryUtils.fetchCategoryData(requestUrl);
                    break;
            }
            return objects;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof RadiosFragment) {
            RadiosFragment radiosFragment = (RadiosFragment) fragment;
            radiosFragment.setOnEventFromRadiosFragmentListener(this);
            radiosFragment.setOnRadioItemClickListener(this);
            radiosFragment.setOnRadioLoadingCompleteListener(this);
        }
        if (fragment instanceof FavouriteRadiosFragment) {
            FavouriteRadiosFragment favouriteRadiosFragment = (FavouriteRadiosFragment) fragment;
            favouriteRadiosFragment.setOnEventFromFavRadiosFragment(this);
            favouriteRadiosFragment.setOnFavRadioItemClickListener(this);
        }
        if (fragment instanceof CitiesFragment) {
            CitiesFragment citiesFragment = (CitiesFragment) fragment;
            citiesFragment.setOnFilterRespectToCityListener(this);
        }
        if (fragment instanceof CategoriesFragment) {
            CategoriesFragment categoriesFragment = (CategoriesFragment) fragment;
            categoriesFragment.setOnFilterRespectToCategoryListener(this);
        }
        if (fragment instanceof TimerFragment) {
            TimerFragment timerFragment = (TimerFragment) fragment;
            timerFragment.setOnCountdownFinishedListener(this);
        }
    }

//

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
//            favouriteRadiosFragment.updateFavouriteRadiosList();
        }
    }

    @Override
    public void onEventFromRadiosFragment(int radioId, boolean isLiked) {
        if (radioCurrentlyPlaying != null) {
            if (radioId == radioCurrentlyPlaying.getRadioId()) {
                if (isLiked) {
                    radioCurrentlyPlaying.setLiked(true);
//                    updatePopupWindow();
                } else {
                    radioCurrentlyPlaying.setLiked(false);
//                    updatePopupWindow();
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
//                updatePopupWindow();
            }
        }
        radiosFragment.refreshRadiosList(radioId);
    }

    @Override
    public void onRadioItemClick(Radio radioClicked) {
        boolean isConnected = checkConnectivity();
        if (isConnected) {
            if (!serviceBound) {
                Intent intent = new Intent(this, PlayRadioService.class);
                startService(intent);
                bindService(intent, serviceConnection, BIND_AUTO_CREATE);
            } else {
                radioCurrentlyPlaying = radioClicked;
                playRadioService.playRadio(radioClicked);
                tv_radioTitle.setText(radioCurrentlyPlaying.getRadioName());
                String iconUrl = radioCurrentlyPlaying.getRadioIconUrl();
                updateRadioIcon(iconUrl);
                iv_radioIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Log.d("TAG", "Shareable link: " + radioCurrentlyPlaying.getShareableLink());
                        intent.setData(Uri.parse(radioCurrentlyPlaying.getShareableLink()));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                });
                isFromFavouriteRadiosFragment = false;
            }
        } else {
            Toast.makeText(RadiosActivity.this,
                    "Lütfen internete bağlı olduğunuzdan emin olun",
                    Toast.LENGTH_SHORT)
                    .show();
            ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_radio));
        }
    }

    @Override
    public void onFavRadioItemClick(Radio currentFavRadio) {
        playRadioService.playRadio(currentFavRadio);
        //Can make some adjustments. For example disabling the "Add to favourites" menu option.
        isFromFavouriteRadiosFragment = true;
    }

    @Override
    public void onRadioLoadingComplete(boolean isRadioLoadingCompleted) {
        this.isRadioLoadingCompleted = isRadioLoadingCompleted;
    }

    @Override
    public void onFilterRespectToCity(String cityToFilter) {
        //Filter respect to city.
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, radiosFragment).commit();
        ACTIVE_FRAGMENT_ID = RADIOS_FRAGMENT_ID;
        radiosFragment.setFilteringRespectToCityEnabled(true);
        radiosFragment.setFilteringRespectToCategoryEnabled(false);
        radiosFragment.setCityToFilter(cityToFilter);
    }

    @Override
    public void OnFilterRespectToCategory(int categoryIdToFilter) {
        getSupportActionBar().setTitle("Firma Rehberim Radyo");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, radiosFragment).commit();
        ACTIVE_FRAGMENT_ID = RADIOS_FRAGMENT_ID;
        radiosFragment.setFilteringRespectToCityEnabled(false);
        radiosFragment.setFilteringRespectToCategoryEnabled(true);
        radiosFragment.setCategoryToFilter(categoryIdToFilter);
    }

    @Override
    public void onRadioLoadingStart() {
        radiosFragment.pb_loadingRadios.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCountDownFinished() {
        if (playRadioService.isPlaying()) {
            playRadioService.pauseRadio();
            ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_radio));
            Toast.makeText(this, "Radyo durduruldu", Toast.LENGTH_SHORT).show();
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean checkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PlayRadioBinder binder = (PlayRadioBinder) service;
            playRadioService = binder.getService();
            serviceBound = true;
            playRadioService.setServiceCallbacks(RadiosActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBound = false;
        }
    };

    @Override
    public void updateRadiosFragment(int statusCode) {
        radiosFragment.setCurrentRadioStatus(statusCode, radioCurrentlyPlaying);
    }

    @Override
    public void updateFavouriteRadiosFragment(int statusCode) {
        favouriteRadiosFragment.setCurrentRadioStatus(statusCode, radioCurrentlyPlaying);
    }

    @Override
    public void togglePlayPauseButton(boolean isPaused) {
        if (isPaused) {
            ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_radio));
        } else {
            ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_radio));
        }
    }

    @Override
    public void updateVolumeBar(int streamMaxVolume) {
        sb_volume_control.setMax(streamMaxVolume);
        // Set manually for now. Should use system stream sound instead.
//        sb_volume_control.setProgress(7);
    }

    void updateRadioIcon(String iconUrl) {
        Picasso.with(getApplicationContext()).load(iconUrl)
                .resize(200, 200)
                .centerInside()
                .placeholder(R.drawable.ic_placeholder_radio_black)
                .error(R.drawable.ic_pause_radio)
                .into(iv_radioIcon);
    }
}