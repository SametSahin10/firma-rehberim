package net.dijitalbeyin.firma_rehberim;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
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

import net.dijitalbeyin.firma_rehberim.adapters.CategoryAdapter;
import net.dijitalbeyin.firma_rehberim.adapters.CityAdapter;
import net.dijitalbeyin.firma_rehberim.data.RadioContract;
import net.dijitalbeyin.firma_rehberim.data.RadioDbHelper;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class RadiosActivity extends AppCompatActivity implements RadiosFragment.OnEventFromRadiosFragmentListener,
                                                                FavouriteRadiosFragment.OnEventFromFavRadiosFragment,
                                                                RadiosFragment.OnRadioItemClickListener,
                                                                RadiosFragment.OnRadioLoadingCompleteListener,
                                                                RadiosFragment.OnRadioLoadingStartListener,
                                                                FavouriteRadiosFragment.OnFavRadioItemClickListener,
                                                                CitiesFragment.OnFilterRespectToCityListener,
                                                                CategoriesFragment.OnFilterRespectToCategoryListener,
                                                                LoaderManager.LoaderCallbacks<List<Object>> {
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
    private int ACTIVE_FRAGMENT_ID;

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

    private Button btn_nav_radios;
    private Button btn_nav_tv;
    private Button btn_nav_contacts;
    private Button btn_nav_newspaper;
    private Button btn_nav_fav_radios;
    private Button btn_nav_categories;
    private Button btn_nav_cities;
    private Button btn_nav_global;

    private ImageButton ib_timer;
    private ImageButton ib_volume_control;
    private SeekBar sb_volume_control;
    private ImageButton ib_playPauseRadio;

    private ImageView iv_radioIcon;
    private TextView  tv_radioTitle;
    private ImageButton ib_share_radio;
    private ImageButton ib_player_add_to_fav;
//    private ImageButton ib_player_menu;

    private AudioManager audioManager;
    private SimpleExoPlayer exoPlayer;
    private MediaSource mediaSource;
    private DefaultDataSourceFactory dataSourceFactory;
    private ExoPlayer.EventListener eventListener;
    private DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    TrackSelector trackSelector = new DefaultTrackSelector();

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
                    radiosFragment.restartLoader();
                    if (ACTIVE_FRAGMENT_ID == RADIOS_FRAGMENT_ID) {
                        fragmentManager.beginTransaction()
                                       .detach(fragment)
                                       .attach(fragment)
                                       .commit();
                    } else {
                        fragmentManager.beginTransaction()
                                       .replace(R.id.fragment_container, radiosFragment)
                                       .commit();
                    }
                    ACTIVE_FRAGMENT_ID = GLOBAL_FRAGMENT_ID;
                }
            }
        });

//        allTheCities = new City(301, getString(R.string.city_spinner_default_value_text));
//        allTheCategories = new Category(301, getString(R.string.category_spinner_default_value_text));

//        setupSearchView();
//        sw_searchForRadios.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String cityToFilter) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                Log.d("TAG", "onQueryTextChange: ");
//                if (isRadioLoadingCompleted) {
//                    radiosFragment.radioAdapter.getFilter().filter(newText);
//                }
//                return false;
//            }
//        });
//        ArrayList<Object> defaultCityList = new ArrayList<>();
//        defaultCityList.add(allTheCities);
//        cityAdapter = new CityAdapter(this, R.layout.item_city, defaultCityList);
//        cityAdapter.setDropDownViewResource(R.layout.item_city);
//        spinner_cities = findViewById(R.id.spinner_cities);
//        setupSpinner(spinner_cities, 300, 170);
//        spinner_cities.setAdapter(cityAdapter);
//        getSupportLoaderManager().initLoader(CITY_LOADER_ID, null, this).forceLoad();
//
//        spinner_cities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (isRadioLoadingCompleted) {
//                    radiosFragment.radioAdapter.setFilteringSelection(FILTER_TYPE_CITY);
//                    City selectedCity = (City) parent.getItemAtPosition(position);
//                    radiosFragment.radioAdapter.getFilter().filter(selectedCity.getCityName());
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        ArrayList<Object> defaultCategoryList = new ArrayList<>();
//        defaultCategoryList.add(allTheCategories);
//        categoryAdapter = new CategoryAdapter(this, R.layout.item_category, defaultCategoryList);
//        categoryAdapter.setDropDownViewResource(R.layout.item_category);
////        spinner_categories = findViewById(R.id.spinner_categories);
////        setupSpinner(spinner_categories, 300, 170);
//        spinner_categories.setAdapter(categoryAdapter);
//        getSupportLoaderManager().initLoader(CATEGORY_LOADER_ID, null, this);
//
//        spinner_categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (isRadioLoadingCompleted) {
//                    radiosFragment.radioAdapter.setFilteringSelection(FILTER_TYPE_CATEGORY);
//                    Category selectedCategory = (Category) parent.getItemAtPosition(position);
//                    radiosFragment.radioAdapter.getFilter().filter(selectedCategory.getCategoryName());
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });

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

        ib_timer = findViewById(R.id.ib_timer);

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
        sb_volume_control.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(exoPlayer.getAudioStreamType(), progress, 0);
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
                if (isPlaying()) {
                    exoPlayer.setPlayWhenReady(false);
                    ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_radio));
                } else {
                    if (exoPlayer != null) {
                        exoPlayer.setPlayWhenReady(true);
                        ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_radio));
                    }
                }
            }
        });

//        setupPopupWindow();

//        ib_player_menu = findViewById(R.id.ib_player_menu);
//        ib_player_menu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                popupWindow.showAsDropDown(view, 0, -500);
//            }
//        });
    }

    @Override
    protected void onPause() {
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.release();
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
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
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            sb_volume_control.setProgress(audioManager.getStreamVolume(exoPlayer.getAudioStreamType()));
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            sb_volume_control.setProgress(audioManager.getStreamVolume(exoPlayer.getAudioStreamType()));
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
    }

//    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
//        public ScreenSlidePagerAdapter(FragmentManager fm) {
//            super(fm);
//        }
//
//        @Override
//        public Fragment getItem(int position) {
//            switch (position) {
//                case 0:
//                    return new RadiosFragment();
//                case 1:
//                    return new FavouriteRadiosFragment();
//                default:
//                    return null;
//            }
//        }
//
//        @NonNull
//        @Override
//        public Object instantiateItem(@NonNull ViewGroup container, int position) {
//            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
//            switch (position) {
//                case 0:
//                    radiosFragment = (RadiosFragment) createdFragment;
//                    break;
//                case 1:
//                    favouriteRadiosFragment = (FavouriteRadiosFragment) createdFragment;
//                    break;
//            }
//            return createdFragment;
//        }
//
//        @Override
//        public int getCount() {
//            return NUM_PAGES;
//        }
//
//        @Nullable
//        @Override
//        public CharSequence getPageTitle(int position) {
//            switch (position) {
//                case 0:
//                    return getString(R.string.all_the_radios_tab_title);
//                case 1:
//                    return getString(R.string.favourite_radios_tab_title);
//                default:
//                    return "Unknown tab title";
//            }
//        }
//    }

//    private void setupSearchView() {
//        sw_searchForRadios = findViewById(R.id.sw_searchForRadios);
//        int searchIconId = sw_searchForRadios
//                .getContext()
//                .getResources()
//                .getIdentifier("android:id/search_mag_icon", null, null);
//        iv_searchIcon = sw_searchForRadios.findViewById(searchIconId);
//        iv_searchIcon.setImageResource(R.drawable.ic_search);
//
//        int queryTextId = sw_searchForRadios
//                .getContext()
//                .getResources()
//                .getIdentifier("android:id/search_src_text", null, null);
//        et_queryText = sw_searchForRadios.findViewById(queryTextId);
//        et_queryText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//
//        Typeface righteous_regular = ResourcesCompat.getFont(this, R.font.righteous_regular_res);
//        et_queryText.setTypeface(righteous_regular);
//        et_queryText.setTextColor(getResources().getColor(R.color.radio_item_background_color));
//        et_queryText.setHintTextColor(getResources().getColor(R.color.radio_item_background_color));
//    }

//    private void setupSpinner(Spinner spinner, int height, int width) {
//        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
//        try {
//            Field popup = Spinner.class.getDeclaredField("mPopup");
//            popup.setAccessible(true);
//            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinner);
//            int heightAsDp = (int) (scale * height + 0.5f);
//            int widthAsDp = (int) (scale * width + 0.5f);
//            popupWindow.setHeight(heightAsDp);
//            popupWindow.setWidth(widthAsDp);
//        } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
//
//        }
//    }

//    private void setupPopupWindow() {
//        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
//        View view = layoutInflater.inflate(R.layout.list_popup_window, null);
//        ImageButton ib_popup_window_share = view.findViewById(R.id.ib_popup_window_share);
//        ib_popup_window_share.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //share currently playing radio
//                if (radioCurrentlyPlaying != null) {
//                    shareRadio(radioCurrentlyPlaying);
//                }
//            }
//        });
//        ImageButton ib_popup_window_fav = view.findViewById(R.id.ib_popup_window_fav);
//        ib_popup_window_fav.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Add currently playing radio to favourites.
//                if (radioCurrentlyPlaying != null) {
//                    Log.d(LOG_TAG, "radio currently playing is not null");
//                    radioCurrentlyPlaying.setLiked(true);
//                    addToFavourites(radioCurrentlyPlaying);
////                    favouriteRadiosFragment.updateFavouriteRadiosList();
//                } else {
//                    Log.d(LOG_TAG, "radio currently playing is null");
//                }
//            }
//        });
//        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
//        int width = (int) (60 * scale + 0.5f);
//        int height = (int) (120 * scale + 0.5f);
//        popupWindow = new PopupWindow(view,
//                width,
//                height,
//                true);
//        updatePopupWindow();
//    }

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
//        updatePopupWindow();
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
        playRadio(radioClicked);
        isFromFavouriteRadiosFragment = false;
    }

    @Override
    public void onFavRadioItemClick(Radio currentFavRadio) {
        playRadio(currentFavRadio);
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
        getSupportActionBar().setTitle("Firma Rehberim Radyo");
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
}
