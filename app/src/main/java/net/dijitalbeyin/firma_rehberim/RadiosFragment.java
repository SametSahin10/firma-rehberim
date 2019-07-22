package net.dijitalbeyin.firma_rehberim;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
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

import net.dijitalbeyin.firma_rehberim.adapters.RadioAdapter;

import java.util.ArrayList;
import java.util.List;

public class RadiosFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Radio>>,
                                                        RadioAdapter.OnAddToFavouriteListener {
    private static final String LOG_TAG = RadiosFragment.class.getSimpleName();
    private static final String RADIO_REQUEST_URL = "https://firmarehberim.com/sayfalar/radyo/json/radyolar_arama.php?q=";
    private static final int RADIO_LOADER_ID = 1;

    OnEventFromRadiosFragmentListener onEventFromRadiosFragmentListener;
    OnRadioItemClickListener onRadioItemClickListener;

    public void setOnEventFromRadiosFragmentListener(OnEventFromRadiosFragmentListener onEventFromRadiosFragmentListener) {
        this.onEventFromRadiosFragmentListener = onEventFromRadiosFragmentListener;
    }

    public void setOnRadioItemClickListener(OnRadioItemClickListener onRadioItemClickListener) {
        this.onRadioItemClickListener = onRadioItemClickListener;
    }

    private ListView lw_radios;
    private RadioAdapter radioAdapter;
    private TextView tv_emptyView;
    private ProgressBar pb_loadingRadios;
    private ProgressBar pb_bufferingRadio;

//    ImageView iv_radioIcon;
//    TextView  tv_radioTitle;
//    ImageButton ib_playPauseRadio;

//    ArrayList<Radio> favouriteRadios = new ArrayList<>();

//    private SimpleExoPlayer exoPlayer;
//    private MediaSource mediaSource;
//    private DefaultDataSourceFactory dataSourceFactory;
//    private ExoPlayer.EventListener eventListener;
//    private DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

//    TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory();
//    TrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);

    Radio radioClicked;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_radios, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        pb_loadingRadios = view.findViewById(R.id.pb_loadingRadios);
        pb_bufferingRadio = view.findViewById(R.id.pb_buffering_radio);
        lw_radios = view.findViewById(R.id.lw_radios);
        tv_emptyView = view.findViewById(R.id.tv_emptyRadioView);
        lw_radios.setEmptyView(tv_emptyView);
        radioAdapter = new RadioAdapter(getContext(), R.layout.item_radio, new ArrayList<Radio>(), this);
        lw_radios.setAdapter(radioAdapter);
        if (isConnected) {
            getLoaderManager().initLoader(RADIO_LOADER_ID, null, this).forceLoad();
        } else {
            tv_emptyView.setText(getString(R.string.no_internet_connection_text));
            pb_loadingRadios.setVisibility(View.GONE);
        }

        //////////////////////////////////////////////////////////////////////////////
        lw_radios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (radioClicked != null) {
                    radioClicked.setBeingBuffered(false);
                    radioAdapter.notifyDataSetChanged();
                }
                radioClicked = (Radio) adapterView.getItemAtPosition(position);
                if (radioClicked == null) {
                    Log.d(LOG_TAG, "onItemClick: radioClicked is null");
                } else {
                    Log.d(LOG_TAG, "onItemClick: radioClicked is not null");
                }
                radioClicked.setBeingBuffered(true);
                radioAdapter.notifyDataSetChanged();
                onRadioItemClickListener.onRadioItemClick(radioClicked);
//                if (exoPlayer != null) {
//                    exoPlayer.release();
//                    if (isPlaying()) {
//                        exoPlayer.setPlayWhenReady(false);
//                        exoPlayer.stop(true);
//                    }
//                }
//                String streamLink = radioClicked.getStreamLink();
//                prepareExoPlayer(Uri.parse(streamLink));
//                tv_radioTitle.setText(radioClicked.getRadioName());
//                ImageView iv_item_radio_icon = view.findViewById(R.id.iv_item_radio_icon);
//                iv_radioIcon.setImageDrawable(iv_item_radio_icon.getDrawable());
            }
        });

//        eventListener = new ExoPlayer.EventListener() {
//            @Override
//            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
//                switch (playbackState) {
//                    case ExoPlayer.STATE_BUFFERING:
//                        radioClicked.setBeingBuffered(true);
//                        radioAdapter.notifyDataSetChanged();
//                        Log.d("TAG", "STATE_BUFFERING");
//                        break;
//                    case ExoPlayer.STATE_READY:
//                        radioClicked.setBeingBuffered(false);
//                        radioAdapter.notifyDataSetChanged();
//                        if (isPlaying()) {
//                            ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_radio));
//                        }
//                        Log.d("TAG", "STATE_READY");
//                        break;
//                    case ExoPlayer.STATE_IDLE:
//                        Log.d("TAG", "STATE_IDLE");
//                        exoPlayer.release();
//                        radioClicked.setBeingBuffered(false);
//                        radioAdapter.notifyDataSetChanged();
//                        break;
//                    case ExoPlayer.STATE_ENDED:
//                        Log.d("TAG", "STATE_ENDED");
//                        break;
//                }
//            }
//
//            @Override
//            public void onPlayerError(ExoPlaybackException error) {
//                Toast.makeText(getContext(), R.string.cannot_stream_radio_text, Toast.LENGTH_SHORT).show();
//                Log.e(LOG_TAG, "onPlayerError: ", error);
//            }
//        };

//        ib_playPauseRadio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isPlaying()) {
//                    exoPlayer.setPlayWhenReady(false);
//                    ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_radio));
//                } else {
//                    exoPlayer.setPlayWhenReady(true);
//                    ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_radio));
//                }
//            }
//        });
    }

    @Override
    public Loader<List<Radio>> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new RadioLoader(getContext(), RADIO_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Radio>> loader, List<Radio> radios) {
        radioAdapter.clear();
        if (radios != null) {
            radioAdapter.addAll(radios);
        }
        tv_emptyView.setText(getString(R.string.empty_radios_text));
        pb_loadingRadios.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Radio>> loader) {
        radioAdapter.clear();
    }

    private static class RadioLoader extends AsyncTaskLoader<List<Radio>> {
        private String requestUrl;

        public RadioLoader(@NonNull Context context, String requestUrl) {
            super(context);
            this.requestUrl = requestUrl;
        }

        @Override
        public List<Radio> loadInBackground() {
            ArrayList<Radio> radios = QueryUtils.fetchRadioData(requestUrl);
            return radios;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }

//    private void prepareExoPlayer(Uri uri) {
//        dataSourceFactory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), "exoPlayerSimple"), BANDWIDTH_METER);
//        mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
//        exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
//        exoPlayer.addListener(eventListener);
//        exoPlayer.prepare(mediaSource);
//        exoPlayer.setPlayWhenReady(true);
//    }
//
//    private boolean isPlaying() {
//        if (exoPlayer != null) {
//            return exoPlayer.getPlaybackState() == Player.STATE_READY && exoPlayer.getPlayWhenReady();
//        } else {
//            return false;
//        }
//    }

    public void refreshRadiosList(int radioId) {
        Log.d(LOG_TAG, "radioId: " + radioId);
        List<Radio> radios = radioAdapter.getItems();
        for (Radio radio: radios) {
            if (radio.getRadioId() == radioId) {
                radio.setLiked(false);
            }
        }
        radioAdapter.notifyDataSetChanged();
    }

    public void setCurrentRadioStatus(int statusCode, Radio radioCurrentlyPlaying) {
//        if (radioClicked == null) {
//            Log.d(LOG_TAG, "radioClicked is null");
//        } else {
//            Log.d(LOG_TAG, "radioClicked is not null");
//        }
//        Log.d(LOG_TAG, "radioId: " + radioCurrentlyPlaying.getRadioId());
//        Log.d(LOG_TAG, "radioName: " + radioCurrentlyPlaying.getRadioName());
        List<Radio> radios = radioAdapter.getItems();
        //find the currently playing radio from the radio list
        for (Radio radio: radios) {
            if (radio.getRadioId() == radioCurrentlyPlaying.getRadioId()) {
                switch (statusCode) {
                    case 10: //STATE_BUFFERING
                        radio.setBeingBuffered(true);
                        Log.d("TAG", "Radio set as being buffered");
                        radioAdapter.notifyDataSetChanged();
                        Log.d("TAG", "STATE_BUFFERING");
                        break;
                    case 11: //STATE_READY
                        radio.setBeingBuffered(false);
                        radioAdapter.notifyDataSetChanged();
                        Log.d("TAG", "Radio is ready to play");
                        Log.d("TAG", "STATE_READY");
                        break;
                    case 12: //STATE_IDLE
                        radio.setBeingBuffered(false);
                        radioAdapter.notifyDataSetChanged();
                        Log.d("TAG", "Radio is idle");
                        Log.d("TAG", "STATE_IDLE");
                        break;
                    default:
                        Log.e(LOG_TAG, "Unknown status code: " + statusCode);
                }
            }
        }
//        if (radioClicked == null) {
//            Log.d(LOG_TAG, "radioClicked is null");
//        } else {
//            Log.d(LOG_TAG, "radioClicked is not null");
//        }
    }

    @Override
    public void onAddToFavouriteClick() {
        onEventFromRadiosFragmentListener.onEventFromRadiosFragment();
    }

    public interface OnEventFromRadiosFragmentListener {
        void onEventFromRadiosFragment();
    }

    public interface OnRadioItemClickListener {
        void onRadioItemClick(Radio currentRadio);
    }
}
