package net.dijitalbeyin.firma_rehberim;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.p000v4.app.Fragment;
import android.support.p000v4.app.FragmentManager;
import android.support.p000v4.app.LoaderManager.LoaderCallbacks;
import android.support.p000v4.content.AsyncTaskLoader;
import android.support.p000v4.content.Loader;
import android.support.p000v4.view.PagerAdapter;
import android.support.p000v4.view.ViewPager;
import android.support.p003v7.app.AppCompatActivity;
import android.support.p003v7.widget.Toolbar;
import android.support.p003v7.widget.helper.ItemTouchHelper.Callback;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer.EventListener;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player.EventListener.CC;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import net.dijitalbeyin.firma_rehberim.CategoriesFragment.OnFilterRespectToCategoryListener;
import net.dijitalbeyin.firma_rehberim.CitiesFragment.OnFilterRespectToCityListener;
import net.dijitalbeyin.firma_rehberim.FavouriteRadiosFragment.OnEventFromFavRadiosFragment;
import net.dijitalbeyin.firma_rehberim.FavouriteRadiosFragment.OnFavRadioItemClickListener;
import net.dijitalbeyin.firma_rehberim.RadiosFragment.OnEventFromRadiosFragmentListener;
import net.dijitalbeyin.firma_rehberim.RadiosFragment.OnRadioItemClickListener;
import net.dijitalbeyin.firma_rehberim.RadiosFragment.OnRadioLoadingCompleteListener;
import net.dijitalbeyin.firma_rehberim.adapters.CategoryAdapter;
import net.dijitalbeyin.firma_rehberim.adapters.CityAdapter;
import net.dijitalbeyin.firma_rehberim.data.RadioContract.RadioEntry;
import net.dijitalbeyin.firma_rehberim.data.RadioDbHelper;
import okhttp3.Call.Factory;
import okhttp3.OkHttpClient;

public class RadiosActivity extends AppCompatActivity implements RadiosFragment.OnEventFromRadiosFragmentListener,
                                                                FavouriteRadiosFragment.OnEventFromFavRadiosFragment,
                                                                RadiosFragment.OnRadioItemClickListener,
                                                                RadiosFragment.OnRadioLoadingCompleteListener,
                                                                RadiosFragment.OnRadioLoadingStartListener,
                                                                FavouriteRadiosFragment.OnFavRadioItemClickListener,
                                                                CitiesFragment.OnFilterRespectToCityListener,
                                                                CategoriesFragment.OnFilterRespectToCategoryListener,
                                                                TimerFragment.OnCountdownFinishedListener,
                                                                LoaderManager.LoaderCallbacks<List<Object>> {
    private static final String LOG_TAG = RadiosActivity.class.getSimpleName();
    private static final String CITIES_REQUEST_URL = "https://firmarehberim.com/sayfalar/radyo/json/iller.php";
    private static final String CATEGORY_REQUEST_URL = "https://firmarehberim.com/sayfalar/radyo/json/kategoriler.php";
    private static final int CITIES_FRAGMENT_ID = 7;
    private static final String CITIES_REQUEST_URL = "https://firmarehberim.com/sayfalar/radyo/json/iller.php";
    private static final int CITY_LOADER_ID = 1;
    private static final int CONTACTS_FRAGMENT_ID = 3;
    private static final int FAV_RADIOS_FRAGMENT_ID = 5;
    private static final int FILTER_TYPE_CATEGORY = 3;
    private static final int FILTER_TYPE_CITY = 2;
    private static final int FILTER_TYPE_RADIO = 1;
    private static final int GLOBAL_FRAGMENT_ID = 8;
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "RadiosActivity";
    private static final int NEWSPAPER_FRAGMENT_ID = 4;
    private static final int NUM_PAGES = 2;
    private static final int RADIOS_FRAGMENT_ID = 1;
    private static final int RADIO_LOADER_ID = 1;
    private static final String RADIO_REQUEST_URL = "https://firmarehberim.com/sayfalar/radyo/json/radyolar_arama.php?q=";
    private static final int STATE_BUFFERING = 10;
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
    private Button btn_nav_contacts;
    private Button btn_nav_fav_radios;
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
    private PagerAdapter pagerAdapter;
    PopupWindow popupWindow;
    Radio radioCurrentlyPlaying;
    /* access modifiers changed from: private */
    public RadiosFragment radiosFragment;
    private Spinner spinner_categories;
    private Spinner spinner_cities;
    private SearchView sw_searchForRadios;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    TrackSelector trackSelector = new DefaultTrackSelector();
    private TextView tv_radioTitle;
    private ViewPager viewPager;

    private static class SpinnerLoader extends AsyncTaskLoader<List<Object>> {
        private String requestUrl;

    Radio radioCurrentlyPlaying;
    boolean isFromFavouriteRadiosFragment = false;
    boolean isRadioLoadingCompleted = false;
    boolean isAudioStreamMuted = false;


        public List<Object> loadInBackground() {
            String str = this.requestUrl;
            ArrayList arrayList = null;
            if (str == null) {
                return null;
            }
            char c = 65535;
            int hashCode = str.hashCode();
            if (hashCode != 204295816) {
                if (hashCode == 2074041067 && str.equals(RadiosActivity.CITIES_REQUEST_URL)) {
                    c = 0;
                }
            } else if (str.equals(RadiosActivity.CATEGORY_REQUEST_URL)) {
                c = 1;
            }
            if (c == 0) {
                arrayList = QueryUtils.fetchCityData(this.requestUrl);
            } else if (c == 1) {
                arrayList = QueryUtils.fetchCategoryData(this.requestUrl);
            }
            return arrayList;
        }

        /* access modifiers changed from: protected */
        public void onStartLoading() {
            forceLoad();
        }
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) C0662R.layout.activity_radio);
        this.ACTIVE_FRAGMENT_ID = 1;
        this.radiosFragment = new RadiosFragment();
        getSupportFragmentManager().beginTransaction().replace(C0662R.C0664id.fragment_container, this.radiosFragment).commit();
        this.btn_nav_radios = (Button) findViewById(C0662R.C0664id.btn_nav_radios);
        this.btn_nav_radios.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (RadiosActivity.this.ACTIVE_FRAGMENT_ID != 1) {
                    RadiosActivity.this.getSupportActionBar().setTitle((CharSequence) "Firma Rehberim Radyo");
                    RadiosActivity.this.getSupportFragmentManager().beginTransaction().replace(C0662R.C0664id.fragment_container, RadiosActivity.this.radiosFragment).commit();
                    RadiosActivity.this.ACTIVE_FRAGMENT_ID = 1;
                }
            }
        });
        this.btn_nav_tv = (Button) findViewById(C0662R.C0664id.btn_nav_tv);
        this.btn_nav_tv.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (RadiosActivity.this.ACTIVE_FRAGMENT_ID != 2) {
                    RadiosActivity.this.getSupportActionBar().setTitle((CharSequence) "TV");
                    RadiosActivity.this.getSupportFragmentManager().beginTransaction().replace(C0662R.C0664id.fragment_container, new TvFragment()).commit();
                    RadiosActivity.this.ACTIVE_FRAGMENT_ID = 2;
                }
            }
        });
        this.btn_nav_contacts = (Button) findViewById(C0662R.C0664id.btn_nav_contacts);
        this.btn_nav_contacts.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (RadiosActivity.this.ACTIVE_FRAGMENT_ID != 3) {
                    RadiosActivity.this.getSupportActionBar().setTitle((CharSequence) "Rehber");
                    RadiosActivity.this.getSupportFragmentManager().beginTransaction().replace(C0662R.C0664id.fragment_container, new ContactsFragment()).commit();
                    RadiosActivity.this.ACTIVE_FRAGMENT_ID = 3;
                }
            }
        });
        this.btn_nav_newspaper = (Button) findViewById(C0662R.C0664id.btn_nav_newspaper);
        this.btn_nav_newspaper.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (RadiosActivity.this.ACTIVE_FRAGMENT_ID != 4) {
                    RadiosActivity.this.getSupportActionBar().setTitle((CharSequence) "Gazete");
                    RadiosActivity.this.getSupportFragmentManager().beginTransaction().replace(C0662R.C0664id.fragment_container, new NewspaperFragment()).commit();
                    RadiosActivity.this.ACTIVE_FRAGMENT_ID = 4;
                }
            }
        });
        this.btn_nav_fav_radios = (Button) findViewById(C0662R.C0664id.btn_nav_fav_radios);
        this.btn_nav_fav_radios.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (RadiosActivity.this.ACTIVE_FRAGMENT_ID != 5) {
                    RadiosActivity.this.getSupportActionBar().setTitle((CharSequence) "Favori Radyolar");
                    RadiosActivity.this.favouriteRadiosFragment = new FavouriteRadiosFragment();
                    RadiosActivity.this.getSupportFragmentManager().beginTransaction().replace(C0662R.C0664id.fragment_container, RadiosActivity.this.favouriteRadiosFragment).commit();
                    RadiosActivity.this.ACTIVE_FRAGMENT_ID = 5;
                }
            }
        });
        this.btn_nav_categories = (Button) findViewById(C0662R.C0664id.btn_nav_categories);
        this.btn_nav_categories.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (RadiosActivity.this.ACTIVE_FRAGMENT_ID != 6) {
                    RadiosActivity.this.getSupportActionBar().setTitle((CharSequence) "Kategoriler");
                    RadiosActivity.this.getSupportFragmentManager().beginTransaction().replace(C0662R.C0664id.fragment_container, new CategoriesFragment()).commit();
                    RadiosActivity.this.ACTIVE_FRAGMENT_ID = 6;
                }
            }
        });
        this.btn_nav_cities = (Button) findViewById(C0662R.C0664id.btn_nav_cities);
        this.btn_nav_cities.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (RadiosActivity.this.ACTIVE_FRAGMENT_ID != 7) {
                    RadiosActivity.this.getSupportActionBar().setTitle((CharSequence) "Şehirler");
                    RadiosActivity.this.getSupportFragmentManager().beginTransaction().replace(C0662R.C0664id.fragment_container, new CitiesFragment()).commit();
                    RadiosActivity.this.ACTIVE_FRAGMENT_ID = 7;
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
        this.iv_radioIcon = (ImageView) findViewById(C0662R.C0664id.iv_radio_icon);
        this.tv_radioTitle = (TextView) findViewById(C0662R.C0664id.tv_radio_title);
        this.eventListener = new EventListener() {
            public /* synthetic */ void onLoadingChanged(boolean z) {
                CC.$default$onLoadingChanged(this, z);
            }

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

            public /* synthetic */ void onPositionDiscontinuity(int i) {
                CC.$default$onPositionDiscontinuity(this, i);
            }

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
        sb_volume_control.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (exoPlayer != null) {
                    audioManager.setStreamVolume(exoPlayer.getAudioStreamType(), progress, 0);
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
                if (isPlaying()) {
                    exoPlayer.setPlayWhenReady(false);
                    ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_radio));
                } else {
                    if (exoPlayer != null) {
                        exoPlayer.setPlayWhenReady(true);
                        ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_radio));
                    }
                } else if (i == 3) {
                    if (RadiosActivity.this.isFromFavouriteRadiosFragment) {
                        RadiosActivity.this.favouriteRadiosFragment.setCurrentRadioStatus(11, RadiosActivity.this.radioCurrentlyPlaying);
                    } else {
                        RadiosActivity.this.radiosFragment.setCurrentRadioStatus(11, RadiosActivity.this.radioCurrentlyPlaying);
                    }
                    if (RadiosActivity.this.isPlaying()) {
                        RadiosActivity.this.ib_playPauseRadio.setImageDrawable(RadiosActivity.this.getResources().getDrawable(C0662R.C0663drawable.ic_pause_radio));
                    }
                    Log.d(str, "STATE_READY");
                } else if (i == 4) {
                    Log.d(str, "STATE_ENDED");
                }
            }

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_notifications:
                return true;
            case R.id.item_timer:
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
            case R.id.item_caller_detection:
                Intent intent = new Intent(this, OverlayActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (exoPlayer != null) {
                sb_volume_control.setProgress(audioManager.getStreamVolume(exoPlayer.getAudioStreamType()));
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
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

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(C0662R.C0666menu.appbar_menu, menu);
        ((SearchView) menu.findItem(C0662R.C0664id.action_search).getActionView()).setOnQueryTextListener(new OnQueryTextListener() {
            public boolean onQueryTextSubmit(String str) {
                return false;
            }

            public boolean onQueryTextChange(String str) {
                if (RadiosActivity.this.ACTIVE_FRAGMENT_ID == 1 || RadiosActivity.this.ACTIVE_FRAGMENT_ID == 8) {
                    RadiosActivity.this.radiosFragment.setQueryFromSearchView(str);
                    RadiosActivity.this.radiosFragment.setFilteringThroughSearchViewEnabled(true);
                    RadiosActivity.this.radiosFragment.restartLoader();
                } else {
                    Toast.makeText(RadiosActivity.this.getApplicationContext(), "Arama yapmak için Radyolar sekmesine geçiniz.", 0).show();
                }
                return false;
            }
        });
        return true;
    }

    @NonNull
    public Loader<List<Object>> onCreateLoader(int i, @Nullable Bundle bundle) {
        if (i == 1) {
            return new SpinnerLoader(this, CITIES_REQUEST_URL);
        }
        if (i != 2) {
            return null;
        }
        return new SpinnerLoader(this, CATEGORY_REQUEST_URL);
    }

    public void onLoadFinished(@NonNull Loader<List<Object>> loader, List<Object> list) {
        int id = loader.getId();
        if (id == 1) {
            this.cityAdapter.clear();
            if (list != null) {
                this.cityAdapter.add(this.allTheCities);
                this.cityAdapter.addAll(list);
            }
        } else if (id == 2) {
            this.categoryAdapter.clear();
            if (list != null) {
                this.categoryAdapter.add(this.allTheCategories);
                this.categoryAdapter.addAll(list);
            }
        }
    }

    public void onLoaderReset(@NonNull Loader<List<Object>> loader) {
        int id = loader.getId();
        if (id == 1) {
            this.cityAdapter.clear();
        } else if (id == 2) {
            this.categoryAdapter.clear();
        }
    }

    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof RadiosFragment) {
            RadiosFragment radiosFragment2 = (RadiosFragment) fragment;
            radiosFragment2.setOnEventFromRadiosFragmentListener(this);
            radiosFragment2.setOnRadioItemClickListener(this);
            radiosFragment2.setOnRadioLoadingCompleteListener(this);
        }
        if (fragment instanceof FavouriteRadiosFragment) {
            FavouriteRadiosFragment favouriteRadiosFragment2 = (FavouriteRadiosFragment) fragment;
            favouriteRadiosFragment2.setOnEventFromFavRadiosFragment(this);
            favouriteRadiosFragment2.setOnFavRadioItemClickListener(this);
        }
        if (fragment instanceof CitiesFragment) {
            ((CitiesFragment) fragment).setOnFilterRespectToCityListener(this);
        }
        if (fragment instanceof CategoriesFragment) {
            ((CategoriesFragment) fragment).setOnFilterRespectToCategoryListener(this);
        }
        if (fragment instanceof TimerFragment) {
            TimerFragment timerFragment = (TimerFragment) fragment;
            timerFragment.setOnCountdownFinishedListener(this);
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
        String str = "exoPlayerSimple";
        this.dataSourceFactory = new DefaultDataSourceFactory(getApplicationContext(), Util.getUserAgent(getApplicationContext(), str), (TransferListener) this.BANDWIDTH_METER);
        Uri uri2 = uri;
        ExtractorMediaSource extractorMediaSource = new ExtractorMediaSource(uri2, new OkHttpDataSourceFactory((Factory) new OkHttpClient(), Util.getUserAgent(getApplicationContext(), str), (TransferListener) null), new DefaultExtractorsFactory(), null, null);
        this.mediaSource = extractorMediaSource;
        this.exoPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), this.trackSelector);
        this.exoPlayer.addAnalyticsListener(new EventLogger((MappingTrackSelector) this.trackSelector));
        this.exoPlayer.addListener(this.eventListener);
        this.exoPlayer.prepare(this.mediaSource);
        this.exoPlayer.setPlayWhenReady(true);
    }

    /* access modifiers changed from: private */
    public boolean isPlaying() {
        SimpleExoPlayer simpleExoPlayer = this.exoPlayer;
        if (simpleExoPlayer == null || simpleExoPlayer.getPlaybackState() != 3 || !this.exoPlayer.getPlayWhenReady()) {
            return false;
        }
        return true;
    }

    private void playRadio(Radio radioClicked) {
        Log.d("TAG", "Radio stream link: " + radioClicked.getStreamLink());
        radioCurrentlyPlaying = radioClicked;
//        updatePopupWindow();
        if (exoPlayer != null) {
            exoPlayer.release();
            if (isPlaying()) {
                this.exoPlayer.setPlayWhenReady(false);
                this.exoPlayer.stop(true);
            }
        }
        prepareExoPlayer(Uri.parse(radio.getStreamLink()));
        this.tv_radioTitle.setText(radio.getRadioName());
        Picasso.with(getApplicationContext()).load(radio.getRadioIconUrl()).resize(Callback.DEFAULT_DRAG_ANIMATION_DURATION, Callback.DEFAULT_DRAG_ANIMATION_DURATION).centerInside().placeholder((int) C0662R.C0663drawable.ic_placeholder_radio_black).error((int) C0662R.C0663drawable.ic_pause_radio).into(this.iv_radioIcon);
    }

    private void updatePopupWindow() {
        if (this.radioCurrentlyPlaying != null) {
            ImageButton imageButton = (ImageButton) this.popupWindow.getContentView().findViewById(C0662R.C0664id.ib_popup_window_fav);
            if (this.radioCurrentlyPlaying.isLiked()) {
                imageButton.setImageDrawable(getDrawable(C0662R.C0663drawable.ic_favourite_checked));
            } else {
                imageButton.setImageDrawable(getDrawable(C0662R.C0663drawable.ic_favourite_empty));
            }
        }
    }

    /* access modifiers changed from: private */
    public void shareRadio(Radio radio) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        StringBuilder sb = new StringBuilder();
        sb.append("I'm listening to ");
        sb.append(radio.getRadioName());
        sb.append(" on Firma Rehberim Radyo");
        intent.putExtra("android.intent.extra.TEXT", sb.toString());
        intent.setType("text/plain");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    /* access modifiers changed from: private */
    public void addToFavourites(Radio radio) {
        SQLiteDatabase writableDatabase = new RadioDbHelper(this).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", Integer.valueOf(radio.getRadioId()));
        contentValues.put(RadioEntry.COLUMN_RADIO_NAME, radio.getRadioName());
        contentValues.put(RadioEntry.COLUMN_RADIO_CATEGORY, radio.getCategory());
        contentValues.put(RadioEntry.COLUMN_RADIO_ICON_URL, radio.getRadioIconUrl());
        contentValues.put(RadioEntry.COLUMN_RADIO_STREAM_LINK, radio.getStreamLink());
        contentValues.put(RadioEntry.COLUMN_RADIO_SHAREABLE_LINK, radio.getShareableLink());
        contentValues.put(RadioEntry.COLUMN_RADIO_HIT, Integer.valueOf(radio.getHit()));
        contentValues.put(RadioEntry.COLUMN_NUM_OF_ONLINE_LISTENERS, Integer.valueOf(radio.getNumOfOnlineListeners()));
        contentValues.put(RadioEntry.COLUMN_RADIO_IS_BEING_BUFFERED, Boolean.valueOf(radio.isBeingBuffered()));
        contentValues.put(RadioEntry.COLUMN_RADIO_IS_LIKED, Boolean.valueOf(radio.isLiked()));
        long insert = writableDatabase.insert(RadioEntry.TABLE_NAME, null, contentValues);
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("newRowId: ");
        sb.append(insert);
        Log.d(str, sb.toString());
    }

    public void notifyFavouriteRadiosFragment() {
        FavouriteRadiosFragment favouriteRadiosFragment2 = this.favouriteRadiosFragment;
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
        this.radiosFragment.refreshRadiosList(i);
    }

    public void onRadioItemClick(Radio radio) {
        playRadio(radio);
        this.isFromFavouriteRadiosFragment = false;
    }

    public void onFavRadioItemClick(Radio radio) {
        playRadio(radio);
        this.isFromFavouriteRadiosFragment = true;
    }

    public void onRadioLoadingComplete(boolean z) {
        this.isRadioLoadingCompleted = z;
    }

    public void onFilterRespectToCity(int i) {
        getSupportActionBar().setTitle((CharSequence) "Firma Rehberim Radyo");
        getSupportFragmentManager().beginTransaction().replace(C0662R.C0664id.fragment_container, this.radiosFragment).commit();
        this.ACTIVE_FRAGMENT_ID = 1;
        this.radiosFragment.setFilteringRespectToCityEnabled(true);
        this.radiosFragment.setFilteringRespectToCategoryEnabled(false);
        this.radiosFragment.setCityToFilter(i);
    }

    public void OnFilterRespectToCategory(int i) {
        getSupportActionBar().setTitle((CharSequence) "Firma Rehberim Radyo");
        getSupportFragmentManager().beginTransaction().replace(C0662R.C0664id.fragment_container, this.radiosFragment).commit();
        this.ACTIVE_FRAGMENT_ID = 1;
        this.radiosFragment.setFilteringRespectToCityEnabled(false);
        this.radiosFragment.setFilteringRespectToCategoryEnabled(true);
        this.radiosFragment.setCategoryToFilter(i);
    }

    @Override
    public void onRadioLoadingStart() {
        radiosFragment.pb_loadingRadios.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCountDownFinished() {
        if (isPlaying()) {
            if (exoPlayer != null) {
                exoPlayer.setPlayWhenReady(false);
                ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_radio));
                Toast.makeText(this, "Radyo durduruldu", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
