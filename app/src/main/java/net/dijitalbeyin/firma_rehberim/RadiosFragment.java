package net.dijitalbeyin.firma_rehberim;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
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

public class RadiosFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Radio>>, RadioAdapter.OnAddToFavouriteListener {
    private static final String LOG_TAG = RadiosFragment.class.getSimpleName();
    private static final String RADIO_REQUEST_URL = "https://firmarehberim.com/sayfalar/radyo/json/radyolar_arama.php?q=";
    private static final int RADIO_LOADER_ID = 1;

    OnEventFromFragmentListener onEventFromFragmentListener;

    public void setOnEventFromFragmentListener(OnEventFromFragmentListener onEventFromFragmentListener) {
        this.onEventFromFragmentListener = onEventFromFragmentListener;
    }

    ListView lw_radios;
    RadioAdapter radioAdapter;
    TextView tv_emptyView;
    ProgressBar pb_loadingRadios;
    ProgressBar pb_bufferingRadio;

    ImageView iv_radioIcon;
    TextView  tv_radioTitle;
    ImageButton ib_playPauseRadio;

//    ArrayList<Radio> favouriteRadios = new ArrayList<>();

    private SimpleExoPlayer exoPlayer;
    private MediaSource mediaSource;
    private DefaultDataSourceFactory dataSourceFactory;
    private ExoPlayer.EventListener eventListener;
    private DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();

    TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory();
    TrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);

    Radio radioClicked;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_radios, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
        iv_radioIcon = view.findViewById(R.id.iv_radio_icon);
        tv_radioTitle = view.findViewById(R.id.tv_radio_title);
        ib_playPauseRadio = view.findViewById(R.id.ib_play_radio);
        lw_radios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (radioClicked != null) {
                    radioClicked.setBeingBuffered(false);
                    radioAdapter.notifyDataSetChanged();
                }
                radioClicked = (Radio) adapterView.getItemAtPosition(position);
                radioClicked.setBeingBuffered(true);
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
                ImageView iv_item_radio_icon = view.findViewById(R.id.iv_item_radio_icon);
                iv_radioIcon.setImageDrawable(iv_item_radio_icon.getDrawable());
            }
        });

        eventListener = new ExoPlayer.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case ExoPlayer.STATE_BUFFERING:
                        radioClicked.setBeingBuffered(true);
                        radioAdapter.notifyDataSetChanged();
                        Log.d("TAG", "STATE_BUFFERING");
                        break;
                    case ExoPlayer.STATE_READY:
                        radioClicked.setBeingBuffered(false);
                        radioAdapter.notifyDataSetChanged();
                        if (isPlaying()) {
                            ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_radio));
                        }
                        Log.d("TAG", "STATE_READY");
                        break;
                    case ExoPlayer.STATE_IDLE:
                        Log.d("TAG", "STATE_IDLE");
                        exoPlayer.release();
                        radioClicked.setBeingBuffered(false);
                        radioAdapter.notifyDataSetChanged();
                        break;
                    case ExoPlayer.STATE_ENDED:
                        Log.d("TAG", "STATE_ENDED");
                        break;
                }
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Toast.makeText(getContext(), R.string.cannot_stream_radio_text, Toast.LENGTH_SHORT).show();
                Log.e(LOG_TAG, "onPlayerError: ", error);
            }
        };

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

    private void prepareExoPlayer(Uri uri) {
        dataSourceFactory = new DefaultDataSourceFactory(getContext(), Util.getUserAgent(getContext(), "exoPlayerSimple"), BANDWIDTH_METER);
        mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
        exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector);
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

    @Override
    public void onAddToFavouriteClick() {
        onEventFromFragmentListener.onEventFromFragment();
    }

    public interface OnEventFromFragmentListener {
        void onEventFromFragment();
    }
}
