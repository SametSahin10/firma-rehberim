package com.firmarehberim.canliradyo.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firmarehberim.canliradyo.R;

import com.firmarehberim.canliradyo.data.RadioContract;
import com.firmarehberim.canliradyo.data.RadioDbHelper;
import com.firmarehberim.canliradyo.helper.QueryUtils;
import com.firmarehberim.canliradyo.adapters.RadioAdapter;
import com.firmarehberim.canliradyo.datamodel.Radio;

import java.util.ArrayList;
import java.util.List;

public class RadiosFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Radio>>,
        RadioAdapter.OnAddToFavouritesListener,
        RadioAdapter.OnDeleteFromFavouritesListener {
    private static final String LOG_TAG = RadiosFragment.class.getSimpleName();
    private static final String RADIO_REQUEST_URL = "https://firmarehberim.com/bolumler/radyolar/app-json/radyolar_arama.php?q=";
    private static final String RADIO_REQUEST_URL_RESPECT_TO_CITY = "https://firmarehberim.com/bolumler/radyolar/app-json/radyolar_iller.php?q=";
    private static final String RADIO_REQUEST_URL_RESPECT_TO_CATEGORY = "https://firmarehberim.com/bolumler/radyolar/app-json/radyolar_kategori.php?q=";
    private static final int RADIO_LOADER_ID = 1;

    OnEventFromRadiosFragmentListener onEventFromRadiosFragmentListener;
    OnRadioItemClickListener onRadioItemClickListener;

    View.OnClickListener onClickListener;

    public void setOnRadioItemClickListener(OnRadioItemClickListener onRadioItemClickListener) {
        this.onRadioItemClickListener = onRadioItemClickListener;
    }

    String cityToFilter;
    int categoryIdToFilter;
    String queryFromSearchView;
    boolean isFilteringRespectToCityEnabled = false;
    boolean isFilteringRespectToCategoryEnabled = false;
    boolean isFilteringThroughSearchViewEnabled = false;

    public void setCityToFilter(String cityToFilter) {
        this.cityToFilter = cityToFilter;
    }

    public void setCategoryToFilter(int categoryIdToFilter) {
        this.categoryIdToFilter = categoryIdToFilter;
    }

    public void setQueryFromSearchView(String queryFromSearchView) {
        this.queryFromSearchView = queryFromSearchView;
    }

    public void setFilteringRespectToCityEnabled(boolean filteringRespectToCityEnabled) {
        isFilteringRespectToCityEnabled = filteringRespectToCityEnabled;
    }

    public void setFilteringRespectToCategoryEnabled(boolean filteringRespectToCategoryEnabled) {
        isFilteringRespectToCategoryEnabled = filteringRespectToCategoryEnabled;
    }

    public void setFilteringThroughSearchViewEnabled(boolean filteringThroughSearchViewEnabled) {
        isFilteringThroughSearchViewEnabled = filteringThroughSearchViewEnabled;
    }

    public ListView lw_radios;
    RadioAdapter radioAdapter;
    private TextView tv_emptyView;
    public ProgressBar pb_loadingRadios;
    private ProgressBar pb_bufferingRadio;

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
        boolean isConnected = checkConnectivity();

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = (int) view.getTag();
                Log.d("TAG", "" + position);
                List<Radio> radios = radioAdapter.getItems();
                Radio radio = radios.get(position);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(radio.getShareableLink()));
                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                    Log.d("TAG", radio.getShareableLink());
                    startActivity(intent);
                }
            }
        };

        pb_loadingRadios = view.findViewById(R.id.pb_loadingRadios);
        pb_bufferingRadio = view.findViewById(R.id.pb_buffering_radio);
        lw_radios = view.findViewById(R.id.lw_radios);
        tv_emptyView = view.findViewById(R.id.tv_emptyRadioView);
        lw_radios.setEmptyView(tv_emptyView);

        Cursor cursor = queryAllTheRadios(getContext());
        List<Radio> radios = retrieveRadiosFromCursor(cursor);
        radioAdapter = new RadioAdapter(getContext(),
                                        R.layout.item_radio,
                                        new ArrayList<Radio>(),
                                        radios,
                                        this,
                                        this,
                                        onClickListener);
        lw_radios.setAdapter(radioAdapter);
        if (isConnected) {
            getLoaderManager().initLoader(RADIO_LOADER_ID, null, this).forceLoad();
        } else {
            Log.d("TAG", "No Network Connection");
            tv_emptyView.setText(getString(R.string.no_internet_connection_text));
            pb_loadingRadios.setVisibility(View.GONE);
        }

        lw_radios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Radio newRadio = (Radio) adapterView.getItemAtPosition(position);
                if (radioClicked != null) {
                    // A radio had been playing already.
                    if (radioClicked.getRadioId() == newRadio.getRadioId()) {
                        // User clicked on the radio that's been playing already.
                        if (radioClicked.isPlaying()) {
                            radioClicked.setBeingBuffered(false);
                            radioClicked.setPlaying(false);
                            radioAdapter.notifyDataSetChanged();
                            onRadioItemClickListener.onPlayingRadioItemClick(newRadio);
                        } else {
                            // User clicked on the radio that's had been playing and stopped.
                            radioClicked = newRadio;
                            radioClicked.setBeingBuffered(true);
                            radioAdapter.notifyDataSetChanged();
                            onRadioItemClickListener.onRadioItemClick(radioClicked);
                        }
                    } else {
                        // User clicked on a radio which is different
                        // from the currently playing one.
                        radioClicked.setBeingBuffered(false);
                        radioClicked.setPlaying(false);
                        radioAdapter.notifyDataSetChanged();
                        radioClicked = newRadio;
                        onRadioItemClickListener.onRadioItemClick(radioClicked);
                    }
                    // Mark it as not playing and play the new one.
                } else {
                    // No radio had been playing. Play the clicked on from scratch.
                    radioClicked = newRadio;
                    radioClicked.setBeingBuffered(true);
                    radioAdapter.notifyDataSetChanged();
                    onRadioItemClickListener.onRadioItemClick(radioClicked);
                }
            }
        });
    }

    @Override
    public Loader<List<Radio>> onCreateLoader(int i, @Nullable Bundle bundle) {
        if (isFilteringRespectToCityEnabled) {
            if (cityToFilter != null) {
                return new RadioLoader(getContext(), RADIO_REQUEST_URL_RESPECT_TO_CITY + cityToFilter,true, false);
            }
        } else if (isFilteringRespectToCategoryEnabled) {
            return new RadioLoader(getContext(), RADIO_REQUEST_URL_RESPECT_TO_CATEGORY + categoryIdToFilter,false, true);
        } else if (isFilteringThroughSearchViewEnabled) {
            return new RadioLoader(getContext(), RADIO_REQUEST_URL + queryFromSearchView,false, false);
        }
        return new RadioLoader(getContext(), RADIO_REQUEST_URL,false, false);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Radio>> loader, List<Radio> radios) {
        radioAdapter.clear();
        if (radios != null) {
            radioAdapter.addAll(radios);
        }
        tv_emptyView.setText(getString(R.string.empty_radios_text));
        lw_radios.setVisibility(View.VISIBLE);
        pb_loadingRadios.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Radio>> loader) {
        radioAdapter.clear();
    }

    private static class RadioLoader extends AsyncTaskLoader<List<Radio>> {
        private String requestUrl;
        private boolean isFilteringRespectToCityEnabled;
        private boolean isFilteringRespectToCategoryEnabled;

        public RadioLoader(@NonNull Context context,
                           String requestUrl,
                           boolean isFilteringRespectToCityEnabled,
                           boolean isFilteringRespectToCategoryEnabled) {
            super(context);
            this.requestUrl = requestUrl;
            this.isFilteringRespectToCityEnabled = isFilteringRespectToCityEnabled;
            this.isFilteringRespectToCategoryEnabled = isFilteringRespectToCategoryEnabled;
        }

        @Override
        public List<Radio> loadInBackground() {
            ArrayList<Radio> radios;
            if (isFilteringRespectToCityEnabled) {
                radios = QueryUtils.fetchRadioDataThroughCities(requestUrl);
            } else if (isFilteringRespectToCategoryEnabled) {
                radios = QueryUtils.fetchRadioDataThroughCategories(requestUrl);
            } else {
                radios = QueryUtils.fetchRadioData(requestUrl);
            }
            return radios;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }

    public void restartLoader() {
        getLoaderManager().restartLoader(RADIO_LOADER_ID, null, this);
    }

    private Cursor queryAllTheRadios(Context context) {
        RadioDbHelper dbHelper = new RadioDbHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        String[] projection = {
                RadioContract.RadioEntry._ID,
                RadioContract.RadioEntry.COLUMN_ID,
                RadioContract.RadioEntry.COLUMN_CITY_ID,
                RadioContract.RadioEntry.COLUMN_TOWN_ID,
                RadioContract.RadioEntry.COLUMN_NEIGHBOURHOOD_ID,
                RadioContract.RadioEntry.COLUMN_ICON_URL,
                RadioContract.RadioEntry.COLUMN_SHAREABLE_LINK,
                RadioContract.RadioEntry.COLUMN_NAME,
                RadioContract.RadioEntry.COLUMN_STREAM_LINK,
                RadioContract.RadioEntry.COLUMN_HIT,
                RadioContract.RadioEntry.COLUMN_CATEGORY_ID,
                RadioContract.RadioEntry.COLUMN_USER_ID,
                RadioContract.RadioEntry.COLUMN_CATEGORY,
                RadioContract.RadioEntry.COLUMN_NUM_OF_ONLINE_LISTENERS,
                RadioContract.RadioEntry.COLUMN_IS_BEING_BUFFERED,
                RadioContract.RadioEntry.COLUMN_IS_LIKED};
        Cursor cursor = sqLiteDatabase.query(RadioContract.RadioEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
        return cursor;
    }

    private List<Radio> retrieveRadiosFromCursor(Cursor cursor) {
        int idColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_ID);
        int cityIdColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_CITY_ID);
        int townIdColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_TOWN_ID);
        int neighbourhoodIdColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_NEIGHBOURHOOD_ID);
        int categoryIdColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_CATEGORY_ID);
        int userIdColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_USER_ID);
        int nameColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_NAME);
        int categoryColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_CATEGORY);
        int iconUrlColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_ICON_URL);
        int streamLinkColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_STREAM_LINK);
        int isInHLSFormatColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_IS_IN_HLS_FORMAT);
        int shareableLinkColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_SHAREABLE_LINK);
        int numOfOnlineListenersColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_NUM_OF_ONLINE_LISTENERS);
        int hitColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_HIT);
        int isBeingBufferedColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_IS_BEING_BUFFERED);
        int isLikedColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_IS_LIKED);

        List<Radio> radios = new ArrayList<>();

        while (cursor.moveToNext()) {
            int radioId = cursor.getInt(idColumnIndex);
            int cityId = cursor.getInt(cityIdColumnIndex);
            int townId = cursor.getInt(townIdColumnIndex);
            int neighbourhoodId = cursor.getInt(neighbourhoodIdColumnIndex);
            String categoryId = cursor.getString(categoryIdColumnIndex);
            int userId = cursor.getInt(userIdColumnIndex);
            String radioName = cursor.getString(nameColumnIndex);
            String category = cursor.getString(categoryColumnIndex);
            String radioIconUrl = cursor.getString(iconUrlColumnIndex);
            String streamLink = cursor.getString(streamLinkColumnIndex);
            String shareableLink = cursor.getString(shareableLinkColumnIndex);
            int hit = cursor.getInt(hitColumnIndex);
            int numOfOnlineListeners = cursor.getInt(numOfOnlineListenersColumnIndex);
            boolean isInHLSFormat = false;
            if (cursor.getInt(isInHLSFormatColumnIndex) == 1) {
                isInHLSFormat = true;
            }
            boolean isBeingBuffered = false;
            if (cursor.getInt(isBeingBufferedColumnIndex) == 1) {
                isBeingBuffered = true;
            }
            boolean isLiked = false;
            if (cursor.getInt(isLikedColumnIndex) == 1) {
                isLiked = true;
            }
            Radio radio = new Radio(radioId,
                    cityId,
                    townId,
                    neighbourhoodId,
                    radioIconUrl,
                    shareableLink,
                    radioName,
                    streamLink,
                    isInHLSFormat,
                    hit,
                    categoryId,
                    userId,
                    category,
                    numOfOnlineListeners,
                    false,
                    false,
                    false);
            radios.add(radio);
        }
        return radios;
    }

    public void setCurrentRadioStatus(int statusCode, Radio radioCurrentlyPlaying) {
        List<Radio> radios = radioAdapter.getItems();
        //find the currently playing radio from the radio list
        // TODO: Fix NPE here.
        for (Radio radio: radios) {
            if (radio.getRadioId() == radioCurrentlyPlaying.getRadioId()) {
                switch (statusCode) {
                    case 10: //STATE_BUFFERING
                        Log.d(LOG_TAG, "STATE_BUFFERING");
                        radio.setBeingBuffered(true);
                        radio.setPlaying(false);
                        radioAdapter.notifyDataSetChanged();
                        break;
                    case 11: //STATE_READY
                        Log.d(LOG_TAG, "STATE_READY");
                        radio.setBeingBuffered(false);
                        radio.setPlaying(true);
                        radioAdapter.notifyDataSetChanged();
                        break;
                    case 12: //STATE_IDLE
                        Log.d(LOG_TAG, "STATE_IDLE");
                        radio.setBeingBuffered(false);
                        radio.setPlaying(false);
                        radioAdapter.notifyDataSetChanged();
                        break;
                    case 13: //STATE_PAUSED - This state is not an exoplayer state.
                        Log.d(LOG_TAG, "STATE_PAUSED");
                        radio.setPlaying(false);
                        radioAdapter.notifyDataSetChanged();
                    default:
                        Log.e(LOG_TAG, "Unknown status code: " + statusCode);
                }
            }
        }
    }

    private boolean checkConnectivity() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    public void onAddToFavouritesClick(int radioId) {
        onEventFromRadiosFragmentListener.onEventFromRadiosFragment(radioId, true);
    }

    @Override
    public void onDeleteFromFavouritesClick(int radioId) {
        onEventFromRadiosFragmentListener.onEventFromRadiosFragment(radioId, false);
    }

    public interface OnEventFromRadiosFragmentListener {
        void onEventFromRadiosFragment(int radioId, boolean isLiked);
    }

    public interface OnRadioItemClickListener {
        void onRadioItemClick(Radio radioClicked);
        // Callback to be notified when user clicks on the radio that's been playing already.
        void onPlayingRadioItemClick(Radio radioClicked);
    }
}