package com.firmarehberim.canliradyo.fragments;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import com.firmarehberim.canliradyo.adapters.FavouriteRadioAdapter;
import com.firmarehberim.canliradyo.data.RadioDbHelper;
import com.firmarehberim.canliradyo.helper.QueryUtils;
import com.firmarehberim.canliradyo.R;
import com.firmarehberim.canliradyo.adapters.RadioAdapter;
import com.firmarehberim.canliradyo.datamodel.Radio;
import com.firmarehberim.canliradyo.data.RadioContract.RadioEntry;

import java.util.ArrayList;
import java.util.List;

public class FavouriteRadiosFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Radio>> {
    private static final String LOG_TAG = FavouriteRadiosFragment.class.getSimpleName();
    private static final String FAVOURITE_RADIO_REQUEST_URL = "https://firmarehberim.com/bolumler/radyolar/app-json/radyolar_favori.php";
    private static final int FAVOURITE_RADIO_LOADER_ID = 1;

    private OnFavRadioItemClickListener onFavRadioItemClickListener;

    public void setOnFavRadioItemClickListener(OnFavRadioItemClickListener onFavRadioItemClickListener) {
        this.onFavRadioItemClickListener = onFavRadioItemClickListener;
    }

    private RadioDbHelper dbHelper;

    private ListView lw_radios;
    private Cursor cursor;
    private FavouriteRadioAdapter favouriteRadioAdapter;
    private TextView tv_emptyView;
    private ProgressBar pb_loadingRadios;
    private ProgressBar pb_bufferingRadio;

    private List<Radio> favoriteRadios;

    Radio radioClicked;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_favourite_radios, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
        pb_loadingRadios = view.findViewById(R.id.pb_loadingRadios);
        pb_bufferingRadio = view.findViewById(R.id.pb_buffering_radio);
        lw_radios = view.findViewById(R.id.lw_radios);
        tv_emptyView = view.findViewById(R.id.tv_emptyRadioView);
        lw_radios.setEmptyView(tv_emptyView);

        cursor = queryAllTheRadios(getContext());
        favoriteRadios = new ArrayList<>();
        favoriteRadios = retrieveRadiosFromCursor(cursor);
        favouriteRadioAdapter = new FavouriteRadioAdapter(getContext(),
                                        R.layout.item_radio,
                                        new ArrayList<Radio>(),
                                        null,
                                        null,
                                        null);
        lw_radios.setAdapter(favouriteRadioAdapter);
        if (isConnected) {
            getLoaderManager().initLoader(FAVOURITE_RADIO_LOADER_ID, null, this).forceLoad();
        } else {
            Log.d("TAG", "No Network Connection");
            tv_emptyView.setText(getString(R.string.no_internet_connection_text));
            pb_loadingRadios.setVisibility(View.GONE);
        }

        //////////////////////////////////////////////////////////////////////////////
        lw_radios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (radioClicked != null) {
                    radioClicked.setBeingBuffered(false);
                    radioClicked.setPlaying(false);
                    favouriteRadioAdapter.notifyDataSetChanged();
                }
                radioClicked = (Radio) adapterView.getItemAtPosition(position);
                radioClicked.setBeingBuffered(true);
                favouriteRadioAdapter.notifyDataSetChanged();
                onFavRadioItemClickListener.onFavRadioItemClick(radioClicked);
            }
        });
    }

    @Override
    public Loader<List<Radio>> onCreateLoader(int i, Bundle bundle) {
        return new RadioLoader(getContext(), FAVOURITE_RADIO_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Radio>> loader, List<Radio> radios) {
        Log.d(LOG_TAG, "onLoadFinished");
        favouriteRadioAdapter.clear();
        if (radios != null) {
            favouriteRadioAdapter.addAll(radios);
            favoriteRadios.addAll(radios);
        }
        Cursor cursor = queryAllTheRadios(getContext());
        if (cursor != null) {
            List<Radio> radiosFromCursor = retrieveRadiosFromCursor(cursor);
            Log.d(LOG_TAG, "Adding radios from cursor");
            Log.d(LOG_TAG, "Number of favorite radios: " + radiosFromCursor.size());
            for (Radio radio: radiosFromCursor) {
                Log.d(LOG_TAG, radio.getRadioName() + "\n");
            }
            favouriteRadioAdapter.addAll(radiosFromCursor);
        }
        tv_emptyView.setText(getString(R.string.empty_radios_text));
        pb_loadingRadios.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<Radio>> loader) {
        favouriteRadioAdapter.clear();
    }

    private static class RadioLoader extends AsyncTaskLoader<List<Radio>> {
        private String requestUrl;

        public RadioLoader(Context context, String requestUrl) {
            super(context);
            this.requestUrl = requestUrl;
        }

        @Override
        public List<Radio> loadInBackground() {
            ArrayList<Radio> radios = QueryUtils.fetchFavouriteRadioData(requestUrl);
            return radios;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }

    private Cursor queryAllTheRadios(Context context) {
        dbHelper = new RadioDbHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        String[] projection = {
                RadioEntry._ID,
                RadioEntry.COLUMN_RADIO_ID,
                RadioEntry.COLUMN_CITY_ID,
                RadioEntry.COLUMN_TOWN_ID,
                RadioEntry.COLUMN_NEIGHBOURHOOD_ID,
                RadioEntry.COLUMN_RADIO_ICON_URL,
                RadioEntry.COLUMN_RADIO_SHAREABLE_LINK,
                RadioEntry.COLUMN_RADIO_NAME,
                RadioEntry.COLUMN_RADIO_STREAM_LINK,
                RadioEntry.COLUMN_RADIO_HIT,
                RadioEntry.COLUMN_CATEGORY_ID,
                RadioEntry.COLUMN_USER_ID,
                RadioEntry.COLUMN_RADIO_CATEGORY,
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

    private List<Radio> retrieveRadiosFromCursor(Cursor cursor) {
        int idColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_ID);
        int cityIdColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_CITY_ID);
        int townIdColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_TOWN_ID);
        int neighbourhoodIdColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_NEIGHBOURHOOD_ID);
        int categoryIdColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_CATEGORY_ID);
        int userIdColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_USER_ID);
        int nameColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_NAME);
        int categoryColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_CATEGORY);
        int iconUrlColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_ICON_URL);
        int streamLinkColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_STREAM_LINK);
        int shareableLinkColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_SHAREABLE_LINK);
        int numOfOnlineListenersColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_NUM_OF_ONLINE_LISTENERS);
        int hitColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_HIT);
        int isBeingBufferedColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_IS_BEING_BUFFERED);
        int isLikedColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_IS_LIKED);

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
        List<Radio> radios = favouriteRadioAdapter.getItems();
        for (Radio radio: radios) {
            if (radio.getRadioId() == radioCurrentlyPlaying.getRadioId()) {
                switch (statusCode) {
                    case 10: //STATE_BUFFERING
                        radio.setBeingBuffered(true);
                        radio.setPlaying(false);
                        favouriteRadioAdapter.notifyDataSetChanged();
                        Log.d("TAG", "STATE_BUFFERING");
                        break;
                    case 11: //STATE_READY
                        radio.setBeingBuffered(false);
                        radio.setPlaying(true);
                        favouriteRadioAdapter.notifyDataSetChanged();
                        Log.d("TAG", "STATE_READY");
                        break;
                    case 12: //STATE_IDLE
                        radio.setBeingBuffered(false);
                        radio.setPlaying(false);
                        favouriteRadioAdapter.notifyDataSetChanged();
                        Log.d("TAG", "STATE_IDLE");
                        break;
                    case 13: //STATE_PAUSED - This state is not an exoplayer state.
                        radio.setPlaying(false);
                        favouriteRadioAdapter.notifyDataSetChanged();
                    default:
                        Log.e(LOG_TAG, "Unknown status code: " + statusCode);
                }
            }
        }
    }

    public interface OnFavRadioItemClickListener {
        void onFavRadioItemClick(Radio radioClicked);
    }
}