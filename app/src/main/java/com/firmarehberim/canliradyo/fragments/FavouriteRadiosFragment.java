package com.firmarehberim.canliradyo.fragments;

import android.content.Context;
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

import com.firmarehberim.canliradyo.helper.QueryUtils;
import com.firmarehberim.canliradyo.R;
import com.firmarehberim.canliradyo.adapters.RadioAdapter;
import com.firmarehberim.canliradyo.datamodel.Radio;

import java.util.ArrayList;
import java.util.List;

public class FavouriteRadiosFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Radio>> {
    private static final String LOG_TAG = FavouriteRadiosFragment.class.getSimpleName();
    private static final String FAVOURITE_RADIO_REQUEST_URL = "https://firmarehberim.com/sayfalar/radyo/json/radyolar_favori.php";
    private static final int FAVOURITE_RADIO_LOADER_ID = 1;

    OnEventFromFavRadiosFragment onEventFromFavRadiosFragment;
    OnFavRadioItemClickListener onFavRadioItemClickListener;

    public void setOnEventFromFavRadiosFragment(OnEventFromFavRadiosFragment onEventFromFavRadiosFragment) {
        this.onEventFromFavRadiosFragment = onEventFromFavRadiosFragment;
    }

    public void setOnFavRadioItemClickListener(OnFavRadioItemClickListener onFavRadioItemClickListener) {
        this.onFavRadioItemClickListener = onFavRadioItemClickListener;
    }

//    RadioDbHelper dbHelper;

    private ListView lw_radios;
    //    private RadioCursorAdapter radioCursorAdapter;
    RadioAdapter radioAdapter;
    private TextView tv_emptyView;
    private ProgressBar pb_loadingRadios;
    private ProgressBar pb_bufferingRadio;

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
        radioAdapter = new RadioAdapter(getContext(),
                R.layout.item_radio,
                new ArrayList<Radio>(),
                null,
                null,
                        null);
        lw_radios.setAdapter(radioAdapter);
        if (isConnected) {
            getLoaderManager().initLoader(FAVOURITE_RADIO_LOADER_ID, null, this).forceLoad();
        } else {
            Log.d("TAG", "No Network Connection");
            tv_emptyView.setText(getString(R.string.no_internet_connection_text));
            pb_loadingRadios.setVisibility(View.GONE);
        }
//        final Cursor cursor = queryAllTheRadios(getContext());
//        radioCursorAdapter = new RadioCursorAdapter(getContext(), cursor, this);
        lw_radios.setAdapter(radioAdapter);
        if (isConnected) {
            getLoaderManager().initLoader(FAVOURITE_RADIO_LOADER_ID, null, this).forceLoad();
        } else {
            tv_emptyView.setText(getString(R.string.no_internet_connection_text));
            pb_loadingRadios.setVisibility(View.GONE);
        }

        //////////////////////////////////////////////////////////////////////////////
        lw_radios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (radioClicked != null) {
                    onFavRadioItemClickListener.onFavRadioItemClick(radioClicked);
                    radioClicked.setBeingBuffered(false);
//                    radioCursorAdapter.notifyDataSetChanged();
                    radioAdapter.notifyDataSetChanged();
                }
//                Cursor radioCursor = (Cursor) adapterView.getItemAtPosition(position);
//                Radio radioFromCursor = retireveRadioFromCursor(radioCursor, position);
//                radioClicked = radioFromCursor;
                radioClicked = (Radio) adapterView.getItemAtPosition(position);
                radioClicked.setBeingBuffered(true);
                radioAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public Loader<List<Radio>> onCreateLoader(int i, Bundle bundle) {
        return new RadioLoader(getContext(), FAVOURITE_RADIO_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<Radio>> loader, List<Radio> radios) {
        radioAdapter.setPermanentRadiosList(radios);
        radioAdapter.clear();
        if (radios != null) {
            radioAdapter.addAll(radios);
        }
        tv_emptyView.setText(getString(R.string.empty_radios_text));
        pb_loadingRadios.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<Radio>> loader) {
        radioAdapter.clear();
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

//    public Cursor queryAllTheRadios(Context context) {
//        dbHelper = new RadioDbHelper(context);
//        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
//        String[] projection = {
//                RadioEntry._ID,
//                RadioEntry.COLUMN_RADIO_ID,
//                RadioEntry.COLUMN_RADIO_NAME,
//                RadioEntry.COLUMN_RADIO_CATEGORY,
//                RadioEntry.COLUMN_RADIO_ICON_URL,
//                RadioEntry.COLUMN_RADIO_STREAM_LINK,
//                RadioEntry.COLUMN_RADIO_SHAREABLE_LINK,
//                RadioEntry.COLUMN_RADIO_HIT,
//                RadioEntry.COLUMN_NUM_OF_ONLINE_LISTENERS,
//                RadioEntry.COLUMN_RADIO_IS_BEING_BUFFERED,
//                RadioEntry.COLUMN_RADIO_IS_LIKED};
//        Cursor cursor = sqLiteDatabase.cityToFilter(RadioEntry.TABLE_NAME,
//                projection,
//                null,
//                null,
//                null,
//                null,
//                null);
//        return cursor;
//    }

//    private Radio retireveRadioFromCursor(Cursor cursor, int position) {
//        int idColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_ID);
//        int cityIdColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_CITY_ID);
//        int townIdColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_TOWN_ID);
//        int neighbourhoodIdColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_NEIGHBOURHOOD_ID);
//        int categoryIdColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_CATEGORY_ID);
//        int userIdColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_USER_ID);
//        int nameColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_NAME);
//        int categoryColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_CATEGORY);
//        int iconUrlColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_ICON_URL);
//        int streamLinkColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_STREAM_LINK);
//        int shareableLinkColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_SHAREABLE_LINK);
//        int numOfOnlineListenersColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_NUM_OF_ONLINE_LISTENERS);
//        int hitColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_HIT);
//        int isBeingBufferedColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_IS_BEING_BUFFERED);
//        int isLikedColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_IS_LIKED);
//
//        cursor.moveToPosition(position);
//        int radioId = cursor.getInt(idColumnIndex);
//        int cityId = cursor.getInt(cityIdColumnIndex);
//        int townId = cursor.getInt(townIdColumnIndex);
//        int neighbourhoodId = cursor.getInt(neighbourhoodIdColumnIndex);
//        String categoryId = cursor.getString(categoryIdColumnIndex);
//        int userId = cursor.getInt(userIdColumnIndex);
//        String radioName = cursor.getString(nameColumnIndex);
//        String category = cursor.getString(categoryColumnIndex);
//        String radioIconUrl = cursor.getString(iconUrlColumnIndex);
//        String streamLink = cursor.getString(streamLinkColumnIndex);
//        String shareableLink = cursor.getString(shareableLinkColumnIndex);
//        int hit = cursor.getInt(hitColumnIndex);
//        int numOfOnlineListeners = cursor.getInt(numOfOnlineListenersColumnIndex);
//        boolean isBeingBuffered = false;
//        if (cursor.getInt(isBeingBufferedColumnIndex) == 1) {
//            isBeingBuffered = true;
//        }
//        boolean isLiked = false;
//        if (cursor.getInt(isLikedColumnIndex) == 1) {
//            isLiked = true;
//        }
//
//        Radio radio = new Radio(radioId,
//                                radioName,
//                                cityId,
//                                townId,
//                                neighbourhoodId,
//                                categoryId,
//                                userId,
//                                category,
//                                radioIconUrl,
//                                streamLink,
//                                shareableLink,
//                                hit,
//                                numOfOnlineListeners,
//                                false,
//                                false);
//        return radio;
//    }

//    protected void updateFavouriteRadiosList() {
//        Cursor cursor = queryAllTheRadios(getContext());
//        radioCursorAdapter.swapCursor(cursor);
//    }

    public void setCurrentRadioStatus(int statusCode, Radio radioCurrentlyPlaying) {
//        Cursor cursor = queryAllTheRadios(getContext());
//        int idColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_ID);
//        int cityIdColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_CITY_ID);
//        int townIdColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_TOWN_ID);
//        int neighbourhoodIdColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_NEIGHBOURHOOD_ID);
//        int categoryIdColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_CATEGORY_ID);
//        int userIdColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_USER_ID);
//        int nameColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_NAME);
//        int categoryColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_CATEGORY);
//        int iconUrlColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_ICON_URL);
//        int streamLinkColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_STREAM_LINK);
//        int shareableLinkColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_SHAREABLE_LINK);
//        int numOfOnlineListenersColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_NUM_OF_ONLINE_LISTENERS);
//        int hitColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_HIT);
//        int isBeingBufferedColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_IS_BEING_BUFFERED);
//        int isLikedColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_IS_LIKED);

//        while (cursor.moveToNext()) {
//            int radioId = cursor.getInt(idColumnIndex);
//            int cityId = cursor.getInt(cityIdColumnIndex);
//            int townId = cursor.getInt(townIdColumnIndex);
//            int neighbourhoodId = cursor.getInt(neighbourhoodIdColumnIndex);
//            String categoryId = cursor.getString(categoryIdColumnIndex);
//            int userId = cursor.getInt(userIdColumnIndex);
//            String radioName = cursor.getString(nameColumnIndex);
//            String category = cursor.getString(categoryColumnIndex);
//            String radioIconUrl = cursor.getString(iconUrlColumnIndex);
//            String streamLink = cursor.getString(streamLinkColumnIndex);
//            String shareableLink = cursor.getString(shareableLinkColumnIndex);
//            int hit = cursor.getInt(hitColumnIndex);
//            int numOfOnlineListeners = cursor.getInt(numOfOnlineListenersColumnIndex);
//            boolean isBeingBuffered = false;
//            if (cursor.getInt(isBeingBufferedColumnIndex) == 1) {
//                isBeingBuffered = true;
//            }
//            boolean isLiked = false;
//
//            if (cursor.getInt(isLikedColumnIndex) == 1) {
//                isLiked = true;
//            }
//
//            Radio radio = new Radio(radioId,
//                    radioName,
//                    cityId,
//                    townId,
//                    neighbourhoodId,
//                    categoryId,
//                    userId,
//                    category,
//                    radioIconUrl,
//                    streamLink,
//                    shareableLink,
//                    hit,
//                    numOfOnlineListeners,
//                    false,
//                    false);
//            if (radio.getRadioId() == radioCurrentlyPlaying.getRadioId()) {
//                radioClicked = radio;
//            }
//        }
//        radioCursorAdapter.notifyDataSetChanged();

        for (Radio radio: radioAdapter.getItems()) {
            if (radio.getRadioId() == radioCurrentlyPlaying.getRadioId()) {
                switch (statusCode) {
                    case 10: //STATE_BUFFERING
                        radio.setBeingBuffered(true);
                        radioAdapter.notifyDataSetChanged();
                        Log.d("TAG", "STATE_BUFFERING");
                        break;
                    case 11: //STATE_READY
                        radio.setBeingBuffered(false);
                        radioAdapter.notifyDataSetChanged();
                        Log.d("TAG", "STATE_READY");
                        break;
                    case 12: //STATE_IDLE
                        radio.setBeingBuffered(false);
                        radioAdapter.notifyDataSetChanged();
                        Log.d("TAG", "STATE_IDLE");
                        break;
                    default:
                        Log.e(LOG_TAG, "Unknown status code: " + statusCode);
                }
            }
        }


    }

//    @Override
//    public void onRadioDelete(int radioId) {
//        updateFavouriteRadiosList();
//        onEventFromFavRadiosFragment.onEventFromFavRadiosFragment(radioId);
//    }

    public interface OnEventFromFavRadiosFragment {
        void onEventFromFavRadiosFragment(int radioId);
    }

    public interface OnFavRadioItemClickListener {
        void onFavRadioItemClick(Radio currentFavRadio);
    }
}