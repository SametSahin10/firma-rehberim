package net.dijitalbeyin.firma_rehberim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import net.dijitalbeyin.firma_rehberim.adapters.RadioCursorAdapter;
import net.dijitalbeyin.firma_rehberim.data.RadioDbHelper;

import static net.dijitalbeyin.firma_rehberim.data.RadioContract.*;

public class FavouriteRadiosFragment extends Fragment {
    private static final String LOG_TAG = FavouriteRadiosFragment.class.getSimpleName();
    public static final String RADIO_DATASET_CHANGED = "net.dijitalbeyin.firma_rehberim.RADIO_DATASET_CHANGED";

    RadioDbHelper dbHelper;

    private ListView lw_radios;
    private RadioCursorAdapter radioCursorAdapter;
    private TextView tv_emptyView;
    private ProgressBar pb_bufferingRadio;

    private ImageView iv_radioIcon;
    private TextView  tv_radioTitle;
    private ImageButton ib_playPauseRadio;

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
                R.layout.fragment_favourite_radios, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        pb_bufferingRadio = view.findViewById(R.id.pb_buffering_radio);
        lw_radios = view.findViewById(R.id.lw_radios);
        tv_emptyView = view.findViewById(R.id.tv_emptyRadioView);
        lw_radios.setEmptyView(tv_emptyView);
        final Cursor cursor = queryAllTheRadios(getContext());
        radioCursorAdapter = new RadioCursorAdapter(getContext(), cursor);
        lw_radios.setAdapter(radioCursorAdapter);
//        if (isConnected) {
////            getLoaderManager().initLoader(RADIO_LOADER_ID, null, this).forceLoad();
//            //Use Cursor Loader instead
//        } else {
//            tv_emptyView.setText(getString(R.string.no_internet_connection_text));
//            pb_loadingRadios.setVisibility(View.GONE);
//        }
//        Will implement this later.

        //////////////////////////////////////////////////////////////////////////////
        iv_radioIcon = view.findViewById(R.id.iv_radio_icon);
        tv_radioTitle = view.findViewById(R.id.tv_radio_title);
        ib_playPauseRadio = view.findViewById(R.id.ib_play_radio);
        lw_radios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (radioClicked != null) {
                    radioClicked.setBeingBuffered(false);
                    radioCursorAdapter.notifyDataSetChanged();
                }
                Cursor radioCursor = (Cursor) adapterView.getItemAtPosition(position);
                Radio radioFromCursor = retireveRadioFromCursor(radioCursor, position);
                radioClicked = radioFromCursor;
                radioClicked.setBeingBuffered(true);
//                radioCursorAdapter.notifyDataSetChanged();
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
//                        radioAdapter.notifyDataSetChanged();
                        Log.d("TAG", "STATE_BUFFERING");
                        break;
                    case ExoPlayer.STATE_READY:
                        radioClicked.setBeingBuffered(false);
                        radioCursorAdapter.notifyDataSetChanged();
                        if (isPlaying()) {
                            ib_playPauseRadio.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_radio));
                        }
                        Log.d("TAG", "STATE_READY");
                        break;
                    case ExoPlayer.STATE_IDLE:
                        Log.d("TAG", "STATE_IDLE");
                        exoPlayer.release();
                        radioClicked.setBeingBuffered(false);
                        radioCursorAdapter.notifyDataSetChanged();
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

    public ListView getLw_radios() {
        return lw_radios;
    }

    public void setLw_radios(ListView lw_radios) {
        this.lw_radios = lw_radios;
    }

    public RadioCursorAdapter getRadioCursorAdapter() {
        return radioCursorAdapter;
    }

    public void setRadioCursorAdapter(RadioCursorAdapter radioCursorAdapter) {
        this.radioCursorAdapter = radioCursorAdapter;
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

    public Cursor queryAllTheRadios(Context context) {
        dbHelper = new RadioDbHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        String[] projection = {
                RadioEntry._ID,
                RadioEntry.COLUMN_RADIO_ID,
                RadioEntry.COLUMN_RADIO_NAME,
                RadioEntry.COLUMN_RADIO_CATEGORY,
                RadioEntry.COLUMN_RADIO_ICON_URL,
                RadioEntry.COLUMN_RADIO_STREAM_LINK,
                RadioEntry.COLUMN_RADIO_SHAREABLE_LINK,
                RadioEntry.COLUMN_RADIO_HIT,
                RadioEntry.COLUMN_NUM_OF_ONLINE_LISTENERS,
                RadioEntry.COLUMN_RADIO_IS_BEING_BUFFERED,
                RadioEntry.COLUMN_RADIO_IS_LIKED};
        Cursor cursor = sqLiteDatabase.query(RadioEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
        return cursor;
    }

    private Radio retireveRadioFromCursor(Cursor cursor, int position) {
        int idColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_ID);
        int nameColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_NAME);
        int categoryColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_CATEGORY);
        int iconUrlColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_ICON_URL);
        int streamLinkColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_STREAM_LINK);
        int shareableLinkColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_SHAREABLE_LINK);
        int numOfOnlineListenersColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_NUM_OF_ONLINE_LISTENERS);
        int hitColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_HIT);
        int isBeingBufferedColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_IS_BEING_BUFFERED);
        int isLikedColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_IS_LIKED);

        cursor.moveToPosition(position);
        int radioId = cursor.getInt(idColumnIndex);
        String radioName = cursor.getString(nameColumnIndex);
        String category = cursor.getString(categoryColumnIndex);
        String radioIconUrl = cursor.getString(iconUrlColumnIndex);
        String streamLink = cursor.getString(streamLinkColumnIndex);
        String shareableLink = cursor.getString(shareableLinkColumnIndex);
        int hit = cursor.getInt(hitColumnIndex);
        int numOfOnlineListeners = cursor.getInt(numOfOnlineListenersColumnIndex);
        boolean isBeingBuffered = false;
        if (cursor.getInt(isBeingBufferedColumnIndex) == 1) {
            isBeingBuffered = true;
        }
        boolean isLiked = false;
        if (cursor.getInt(isLikedColumnIndex) == 1) {
            isLiked = true;
        }

        Radio radio = new Radio(
                radioId,
                radioName,
                category,
                radioIconUrl,
                streamLink,
                shareableLink,
                hit,
                numOfOnlineListeners,
                isBeingBuffered,
                isLiked);
        return radio;
    }

    protected void updateFavouriteRadiosList() {
        Log.d(LOG_TAG, "updateFavouriteRadiosList: ");
        Cursor cursor = queryAllTheRadios(getContext());
        radioCursorAdapter.swapCursor(cursor);
    }
}
