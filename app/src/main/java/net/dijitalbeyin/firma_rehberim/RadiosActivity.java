package net.dijitalbeyin.firma_rehberim;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Spinner;
import android.widget.TextView;
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

public class RadiosActivity extends AppCompatActivity implements OnEventFromRadiosFragmentListener, OnEventFromFavRadiosFragment, OnRadioItemClickListener, OnRadioLoadingCompleteListener, OnFavRadioItemClickListener, OnFilterRespectToCityListener, OnFilterRespectToCategoryListener, LoaderCallbacks<List<Object>> {
    private static final int CATEGORIES_FRAGMENT_ID = 6;
    private static final int CATEGORY_LOADER_ID = 2;
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
    private static final int STATE_READY = 11;
    private static final int TV_FRAGMENT_ID = 2;
    /* access modifiers changed from: private */
    public int ACTIVE_FRAGMENT_ID;
    private DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    Category allTheCategories;
    City allTheCities;
    private Button btn_nav_categories;
    private Button btn_nav_cities;
    private Button btn_nav_contacts;
    private Button btn_nav_fav_radios;
    private Button btn_nav_global;
    private Button btn_nav_newspaper;
    private Button btn_nav_radios;
    private Button btn_nav_tv;
    private CategoryAdapter categoryAdapter;
    private CityAdapter cityAdapter;
    private DefaultDataSourceFactory dataSourceFactory;
    private EditText et_queryText;
    private EventListener eventListener;
    /* access modifiers changed from: private */
    public SimpleExoPlayer exoPlayer;
    /* access modifiers changed from: private */
    public FavouriteRadiosFragment favouriteRadiosFragment;
    /* access modifiers changed from: private */
    public ImageButton ib_playPauseRadio;
    private ImageButton ib_player_menu;
    boolean isFromFavouriteRadiosFragment = false;
    boolean isRadioLoadingCompleted = false;
    private ImageView iv_radioIcon;
    private ImageView iv_searchIcon;
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

        public SpinnerLoader(@NonNull Context context, String str) {
            super(context);
            this.requestUrl = str;
        }

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
        this.btn_nav_global = (Button) findViewById(C0662R.C0664id.btn_nav_global);
        this.btn_nav_global.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (RadiosActivity.this.ACTIVE_FRAGMENT_ID != 8) {
                    Fragment findFragmentById = RadiosActivity.this.getSupportFragmentManager().findFragmentById(C0662R.C0664id.fragment_container);
                    FragmentManager supportFragmentManager = RadiosActivity.this.getSupportFragmentManager();
                    RadiosActivity.this.getSupportActionBar().setTitle((CharSequence) "Ulusal");
                    RadiosActivity.this.radiosFragment.setFilteringRespectToCityEnabled(false);
                    RadiosActivity.this.radiosFragment.setFilteringRespectToCategoryEnabled(false);
                    RadiosActivity.this.radiosFragment.restartLoader();
                    String str = "TAG";
                    if (RadiosActivity.this.ACTIVE_FRAGMENT_ID == 1) {
                        Log.d(str, "Refreshing radios fragment");
                        supportFragmentManager.beginTransaction().detach(findFragmentById).attach(findFragmentById).commit();
                    } else {
                        Log.d(str, "Not Refreshing radios fragment");
                        supportFragmentManager.beginTransaction().replace(C0662R.C0664id.fragment_container, RadiosActivity.this.radiosFragment).commit();
                    }
                    RadiosActivity.this.ACTIVE_FRAGMENT_ID = 8;
                }
            }
        });
        this.iv_radioIcon = (ImageView) findViewById(C0662R.C0664id.iv_radio_icon);
        this.tv_radioTitle = (TextView) findViewById(C0662R.C0664id.tv_radio_title);
        this.eventListener = new EventListener() {
            public /* synthetic */ void onLoadingChanged(boolean z) {
                CC.$default$onLoadingChanged(this, z);
            }

            public /* synthetic */ void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                CC.$default$onPlaybackParametersChanged(this, playbackParameters);
            }

            public /* synthetic */ void onPositionDiscontinuity(int i) {
                CC.$default$onPositionDiscontinuity(this, i);
            }

            public /* synthetic */ void onRepeatModeChanged(int i) {
                CC.$default$onRepeatModeChanged(this, i);
            }

            public /* synthetic */ void onSeekProcessed() {
                CC.$default$onSeekProcessed(this);
            }

            public /* synthetic */ void onShuffleModeEnabledChanged(boolean z) {
                CC.$default$onShuffleModeEnabledChanged(this, z);
            }

            public /* synthetic */ void onTimelineChanged(Timeline timeline, @Nullable Object obj, int i) {
                CC.$default$onTimelineChanged(this, timeline, obj, i);
            }

            public /* synthetic */ void onTracksChanged(TrackGroupArray trackGroupArray, TrackSelectionArray trackSelectionArray) {
                CC.$default$onTracksChanged(this, trackGroupArray, trackSelectionArray);
            }

            public void onPlayerStateChanged(boolean z, int i) {
                String str = "TAG";
                if (i == 1) {
                    Log.d(str, "STATE_IDLE");
                    RadiosActivity.this.exoPlayer.release();
                    if (RadiosActivity.this.isFromFavouriteRadiosFragment) {
                        RadiosActivity.this.favouriteRadiosFragment.setCurrentRadioStatus(12, RadiosActivity.this.radioCurrentlyPlaying);
                    } else {
                        RadiosActivity.this.radiosFragment.setCurrentRadioStatus(12, RadiosActivity.this.radioCurrentlyPlaying);
                    }
                } else if (i == 2) {
                    Log.d(str, "STATE_BUFFERING");
                    if (RadiosActivity.this.isFromFavouriteRadiosFragment) {
                        RadiosActivity.this.favouriteRadiosFragment.setCurrentRadioStatus(10, RadiosActivity.this.radioCurrentlyPlaying);
                    } else {
                        RadiosActivity.this.radiosFragment.setCurrentRadioStatus(10, RadiosActivity.this.radioCurrentlyPlaying);
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

            public void onPlayerError(ExoPlaybackException exoPlaybackException) {
                Toast.makeText(RadiosActivity.this.getApplicationContext(), C0662R.string.cannot_stream_radio_text, 0).show();
                Log.e(RadiosActivity.LOG_TAG, "onPlayerError: ", exoPlaybackException);
            }
        };
        this.ib_playPauseRadio = (ImageButton) findViewById(C0662R.C0664id.ib_play_radio);
        this.ib_playPauseRadio.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (RadiosActivity.this.isPlaying()) {
                    RadiosActivity.this.exoPlayer.setPlayWhenReady(false);
                    RadiosActivity.this.ib_playPauseRadio.setImageDrawable(RadiosActivity.this.getResources().getDrawable(C0662R.C0663drawable.ic_play_radio));
                } else if (RadiosActivity.this.exoPlayer != null) {
                    RadiosActivity.this.exoPlayer.setPlayWhenReady(true);
                    RadiosActivity.this.ib_playPauseRadio.setImageDrawable(RadiosActivity.this.getResources().getDrawable(C0662R.C0663drawable.ic_pause_radio));
                }
            }
        });
        setupPopupWindow();
        this.ib_player_menu = (ImageButton) findViewById(C0662R.C0664id.ib_player_menu);
        this.ib_player_menu.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                RadiosActivity.this.popupWindow.showAsDropDown(view, 0, -500);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        SimpleExoPlayer simpleExoPlayer = this.exoPlayer;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.setPlayWhenReady(false);
            this.exoPlayer.release();
        }
        super.onPause();
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
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
    }

    private void setupPopupWindow() {
        View inflate = ((LayoutInflater) getApplicationContext().getSystemService("layout_inflater")).inflate(C0662R.layout.list_popup_window, null);
        ((ImageButton) inflate.findViewById(C0662R.C0664id.ib_popup_window_share)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (RadiosActivity.this.radioCurrentlyPlaying != null) {
                    RadiosActivity radiosActivity = RadiosActivity.this;
                    radiosActivity.shareRadio(radiosActivity.radioCurrentlyPlaying);
                }
            }
        });
        ((ImageButton) inflate.findViewById(C0662R.C0664id.ib_popup_window_fav)).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (RadiosActivity.this.radioCurrentlyPlaying != null) {
                    Log.d(RadiosActivity.LOG_TAG, "radio currently playing is not null");
                    RadiosActivity.this.radioCurrentlyPlaying.setLiked(true);
                    RadiosActivity radiosActivity = RadiosActivity.this;
                    radiosActivity.addToFavourites(radiosActivity.radioCurrentlyPlaying);
                    return;
                }
                Log.d(RadiosActivity.LOG_TAG, "radio currently playing is null");
            }
        });
        float f = getApplicationContext().getResources().getDisplayMetrics().density;
        this.popupWindow = new PopupWindow(inflate, (int) ((60.0f * f) + 0.5f), (int) ((f * 120.0f) + 0.5f), true);
        updatePopupWindow();
    }

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

    private void playRadio(Radio radio) {
        StringBuilder sb = new StringBuilder();
        sb.append("Radio stream link: ");
        sb.append(radio.getStreamLink());
        Log.d("TAG", sb.toString());
        this.radioCurrentlyPlaying = radio;
        updatePopupWindow();
        SimpleExoPlayer simpleExoPlayer = this.exoPlayer;
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
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

    public void onEventFromRadiosFragment(int i, boolean z) {
        Radio radio = this.radioCurrentlyPlaying;
        if (radio != null && i == radio.getRadioId()) {
            if (z) {
                this.radioCurrentlyPlaying.setLiked(true);
                updatePopupWindow();
            } else {
                this.radioCurrentlyPlaying.setLiked(false);
                updatePopupWindow();
            }
        }
        notifyFavouriteRadiosFragment();
    }

    public void onEventFromFavRadiosFragment(int i) {
        Radio radio = this.radioCurrentlyPlaying;
        if (radio != null && i == radio.getRadioId()) {
            this.radioCurrentlyPlaying.setLiked(false);
            updatePopupWindow();
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
}
