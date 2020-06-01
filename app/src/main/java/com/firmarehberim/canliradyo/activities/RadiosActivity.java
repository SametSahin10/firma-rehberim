package com.firmarehberim.canliradyo.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.AudioManager;

import com.firmarehberim.canliradyo.R;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.SimpleExoPlayer;
import com.squareup.picasso.Picasso;

import com.firmarehberim.canliradyo.fragments.CategoriesFragment;
import com.firmarehberim.canliradyo.fragments.CitiesFragment;
import com.firmarehberim.canliradyo.fragments.ContactsFragment;
import com.firmarehberim.canliradyo.fragments.FavouriteRadiosFragment;
import com.firmarehberim.canliradyo.fragments.NewspaperFragment;
import com.firmarehberim.canliradyo.fragments.RadiosFragment;
import com.firmarehberim.canliradyo.fragments.TimerFragment;
import com.firmarehberim.canliradyo.fragments.TvFragment;
import com.firmarehberim.canliradyo.datamodel.Radio;
import com.firmarehberim.canliradyo.services.PlayRadioService;
import com.firmarehberim.canliradyo.services.PlayRadioService.PlayRadioBinder;

public class RadiosActivity extends AppCompatActivity implements RadiosFragment.OnRadioItemClickListener,
                                                                 RadiosFragment.OnLoadingRadiosFinishedListener,
                                                                 FavouriteRadiosFragment.OnFavRadioItemClickListener,
                                                                 CitiesFragment.OnFilterRespectToCityListener,
                                                                 CategoriesFragment.OnFilterRespectToCategoryListener,
                                                                 TimerFragment.OnCountdownFinishedListener,
                                                                 PlayRadioService.ServiceCallbacks {
    private static final String LOG_TAG = RadiosActivity.class.getSimpleName();

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

    private RadiosFragment radiosFragment;
    private FavouriteRadiosFragment favouriteRadiosFragment;
    private TimerFragment timerFragment;

    private MenuItem action_search;
    private SearchView searchView;

    private ImageButton ib_volume_control;
    private SeekBar sb_volume_control;
    private ImageButton ib_playPauseRadio;

    private ImageView iv_radioIcon;
    private TextView  tv_radioTitle;

    private AudioManager audioManager;

    boolean isAudioStreamMuted = false;

    Radio radioCurrentlyPlaying;
    Radio firstRadioOnList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);

        ACTIVE_FRAGMENT_ID = RADIOS_FRAGMENT_ID;
        radiosFragment = new RadiosFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, radiosFragment).commit();

        Button btn_nav_radios = findViewById(R.id.btn_nav_radios);
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

        Button btn_nav_tv = findViewById(R.id.btn_nav_tv);
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

        Button btn_nav_contacts = findViewById(R.id.btn_nav_contacts);
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

        Button btn_nav_newspaper = findViewById(R.id.btn_nav_newspaper);
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

        Button btn_nav_fav_radios = findViewById(R.id.btn_nav_fav_radios);
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

        Button btn_nav_categories = findViewById(R.id.btn_nav_categories);
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

        Button btn_nav_cities = findViewById(R.id.btn_nav_cities);
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

        Button btn_nav_global = findViewById(R.id.btn_nav_global);
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

        ImageButton ib_search_for_radios = findViewById(R.id.ib_search_for_radios);
        ib_search_for_radios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action_search.setVisible(true);
                searchView.requestFocus();
                searchView.setIconified(false);
            }
        });

        iv_radioIcon = findViewById(R.id.iv_radio_icon);
        tv_radioTitle = findViewById(R.id.tv_radio_title);

        timerFragment = new TimerFragment();
        ImageButton ib_timer = findViewById(R.id.ib_timer);
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
                if (playRadioService != null) {
                    SimpleExoPlayer exoPlayer = playRadioService.getPlayer();
                    if (exoPlayer != null) {
                        playRadioService.audioManager
                                .setStreamVolume(exoPlayer.getAudioStreamType(), progress, 0);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ImageButton ib_share_radio = findViewById(R.id.ib_player_share_radio);
        ib_share_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioCurrentlyPlaying != null) {
                    shareRadio(radioCurrentlyPlaying);
                } else {
                    Toast.makeText(RadiosActivity.this,
                            "Paylaşmak için bir radyoyu çalmaya başlayın",
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        ib_playPauseRadio = findViewById(R.id.ib_play_radio);
        ib_playPauseRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isConnected = checkConnectivity();
                if (isConnected) {
                    if (radioCurrentlyPlaying == null) {
                        // Play/pause button pressed before any selection has been from the list.
                        // Playing the first radio on list.
                        // Performing actions as if the first radio on list was clicked.

                        // Beginning of operations normally performed on RadiosFragment
                        radiosFragment.setRadioClicked(firstRadioOnList);
                        radiosFragment.getRadioClicked().setBeingBuffered(true);
                        radiosFragment.getRadioAdapter().notifyDataSetChanged();
                        // End of operations normally performed on RadiosFragment

                        // Beginning of operations normally performed on RadiosActivity
                        if (!serviceBound) {
                            Intent intent = new Intent(getApplicationContext(), PlayRadioService.class);
                            startService(intent);
                            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
                        } else {
                            radioCurrentlyPlaying = firstRadioOnList;
                            playRadioService.playRadio(firstRadioOnList);
                            tv_radioTitle.setText(radioCurrentlyPlaying.getRadioName());
                            String iconUrl = radioCurrentlyPlaying.getRadioIconUrl();
                            updateRadioIcon(iconUrl);
                            iv_radioIcon.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(radioCurrentlyPlaying.getShareableLink()));
                                    if (intent.resolveActivity(getPackageManager()) != null) {
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                        playRadioService.setFromFavouriteRadiosFragment(false);
                        // End of operations normally performed on RadiosActivity
                    } else {
                        if (playRadioService.isPlaying()) {
                            playRadioService.getTransportControls().pause();
                        } else {
                            playRadioService.getTransportControls().play();
                        }
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
        }
    }

    @Override
    public void onBackPressed() {
        playRadioService.stopForeground(false);
        moveTaskToBack(true);
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
            if (playRadioService != null) {
                SimpleExoPlayer exoPlayer = playRadioService.getPlayer();
                if (exoPlayer != null) {
                    sb_volume_control.setProgress(audioManager.getStreamVolume(exoPlayer.getAudioStreamType()));
                }
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            if (playRadioService != null) {
                SimpleExoPlayer exoPlayer = playRadioService.getPlayer();
                if (exoPlayer != null) {
                    sb_volume_control.setProgress(audioManager.getStreamVolume(exoPlayer.getAudioStreamType()));
                }
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof RadiosFragment) {
            RadiosFragment radiosFragment = (RadiosFragment) fragment;
            radiosFragment.setOnRadioItemClickListener(this);
            radiosFragment.setOnLoadingRadiosFinishedListener(this);
        }
        if (fragment instanceof FavouriteRadiosFragment) {
            FavouriteRadiosFragment favouriteRadiosFragment = (FavouriteRadiosFragment) fragment;
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

    private void shareRadio(Radio radio) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        String extraText = radio.getShareableLink();
        intent.putExtra(Intent.EXTRA_TEXT, extraText);
        intent.setType("text/plain");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
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
                        intent.setData(Uri.parse(radioCurrentlyPlaying.getShareableLink()));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                });
            }
            playRadioService.setFromFavouriteRadiosFragment(false);
        } else {
            Toast.makeText(RadiosActivity.this,
                    "Lütfen internete bağlı olduğunuzdan emin olun",
                    Toast.LENGTH_SHORT)
                    .show();
            ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_radio));
        }
    }

    @Override
    public void onPlayingRadioItemClick(Radio radioClicked) {
        if (playRadioService.isPlaying()) playRadioService.getTransportControls().pause();
        radiosFragment.setCurrentRadioStatus(13, radioClicked);
    }

    @Override
    public void onLoadingRadiosFinished(final Radio firstRadioOnList) {
        this.firstRadioOnList = firstRadioOnList;
        tv_radioTitle.setText(firstRadioOnList.getRadioName());
        String iconUrl = firstRadioOnList.getRadioIconUrl();
        updateRadioIcon(iconUrl);
        iv_radioIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(firstRadioOnList.getShareableLink()));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onFavRadioItemClick(Radio radioClicked) {
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
                        intent.setData(Uri.parse(radioCurrentlyPlaying.getShareableLink()));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                });
            }
            playRadioService.setFromFavouriteRadiosFragment(true);
        } else {
            Toast.makeText(RadiosActivity.this,
                    "Lütfen internete bağlı olduğunuzdan emin olun",
                    Toast.LENGTH_SHORT)
                    .show();
            ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_radio));
        }
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
    public void togglePlayPauseButton(boolean isPaused, boolean isFavouriteRadio) {
        if (isPaused) {
            if (isFavouriteRadio) {
                ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_radio));
                favouriteRadiosFragment.setCurrentRadioStatus(13, radioCurrentlyPlaying);
            } else {
                ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_radio));
                radiosFragment.setCurrentRadioStatus(13, radioCurrentlyPlaying);
            }
        } else {
            if (isFavouriteRadio) {
                ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_radio));
                favouriteRadiosFragment.setCurrentRadioStatus(11, radioCurrentlyPlaying);
            } else {
                ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_radio));
                radiosFragment.setCurrentRadioStatus(11, radioCurrentlyPlaying);
            }
        }
    }

    @Override
    public void updateVolumeBar(int streamMaxVolume) {
        sb_volume_control.setMax(streamMaxVolume);
        // Set manually for now. Should use system stream sound instead.
//        sb_volume_control.setProgress(7);
    }

    void updateRadioIcon(String iconUrl) {
        Picasso.get().load(iconUrl)
                .resize(200, 200)
                .centerInside()
                .placeholder(R.drawable.ic_placeholder_radio_black)
                .error(R.drawable.ic_pause_radio)
                .into(iv_radioIcon);
    }
}