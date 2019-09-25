package net.dijitalbeyin.firma_rehberim;

import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.dynamic.IFragmentWrapper;

import net.dijitalbeyin.firma_rehberim.adapters.RadioAdapter;

import java.util.ArrayList;
import java.util.List;

public class RadiosFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Radio>>,
        RadioAdapter.OnAddToFavouritesListener,
        RadioAdapter.OnDeleteFromFavouritesListener {
    private static final String LOG_TAG = RadiosFragment.class.getSimpleName();
    private static final String RADIO_REQUEST_URL = "https://firmarehberim.com/bolumler/radyolar/app-json/radyolar_arama.php?q=";
    private static final String RADIO_REQUEST_URL_RESPECT_TO_CITY = "https://firmarehberim.com/sayfalar/radyo/json/radyolar_iller.php?q=";
    private static final String RADIO_REQUEST_URL_RESPECT_TO_CATEGORY = "https://firmarehberim.com/sayfalar/radyo/json/radyolar_kategori.php?q=";
    private static final int RADIO_LOADER_ID = 1;

    OnEventFromRadiosFragmentListener onEventFromRadiosFragmentListener;
    OnRadioItemClickListener onRadioItemClickListener;
    OnRadioLoadingCompleteListener onRadioLoadingCompleteListener;
    OnRadioLoadingStartListener onRadioLoadingStartListener;
    View.OnClickListener onClickListener;
//    RadioAdapter.OnRadioIconClickListener onRadioIconClickListener;

    public void setOnEventFromRadiosFragmentListener(OnEventFromRadiosFragmentListener onEventFromRadiosFragmentListener) {
        this.onEventFromRadiosFragmentListener = onEventFromRadiosFragmentListener;
    }

    public void setOnRadioItemClickListener(OnRadioItemClickListener onRadioItemClickListener) {
        this.onRadioItemClickListener = onRadioItemClickListener;
    }

    public void setOnRadioLoadingCompleteListener(OnRadioLoadingCompleteListener onRadioLoadingCompleteListener) {
        this.onRadioLoadingCompleteListener = onRadioLoadingCompleteListener;
    }

    public void setOnRadioLoadingStartListener(OnRadioLoadingStartListener onRadioLoadingStartListener) {
        this.onRadioLoadingStartListener = onRadioLoadingStartListener;
    }

//    public void setOnRadioIconClickListener(RadioAdapter.OnRadioIconClickListener onRadioIconClickListener) {
//        this.onRadioIconClickListener = onRadioIconClickListener;
//    }



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

    ListView lw_radios;
    RadioAdapter radioAdapter;
    private TextView tv_emptyView;
    ProgressBar pb_loadingRadios;
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
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

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
        radioAdapter = new RadioAdapter(getContext(),
                R.layout.item_radio,
                new ArrayList<Radio>(),
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

        //////////////////////////////////////////////////////////////////////////////
        lw_radios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (radioClicked != null) {
                    radioClicked.setBeingBuffered(false);
                    radioAdapter.notifyDataSetChanged();
                }
                radioClicked = (Radio) adapterView.getItemAtPosition(position);
                radioClicked.setBeingBuffered(true);
                radioAdapter.notifyDataSetChanged();
                onRadioItemClickListener.onRadioItemClick(radioClicked);
            }
        });
    }

    @Override
    public Loader<List<Radio>> onCreateLoader(int i, @Nullable Bundle bundle) {
        if (isFilteringRespectToCityEnabled) {
            if (cityToFilter != null) {
                return new RadioLoader(getContext(), RADIO_REQUEST_URL_RESPECT_TO_CITY + cityToFilter, onRadioLoadingCompleteListener, onRadioLoadingStartListener, true, false);
            }
        } else if (isFilteringRespectToCategoryEnabled) {
            return new RadioLoader(getContext(), RADIO_REQUEST_URL_RESPECT_TO_CATEGORY + categoryIdToFilter, onRadioLoadingCompleteListener, onRadioLoadingStartListener, false, true);
        } else if (isFilteringThroughSearchViewEnabled) {
            return new RadioLoader(getContext(), RADIO_REQUEST_URL + queryFromSearchView, onRadioLoadingCompleteListener, onRadioLoadingStartListener, false, false);
        }
        return new RadioLoader(getContext(), RADIO_REQUEST_URL, onRadioLoadingCompleteListener, onRadioLoadingStartListener, false, false);
    }



    @Override
    public void onLoadFinished(@NonNull Loader<List<Radio>> loader, List<Radio> radios) {
        radioAdapter.setPermanentRadiosList(radios);
        onRadioLoadingCompleteListener.onRadioLoadingComplete(true);
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
        private OnRadioLoadingCompleteListener onRadioLoadingCompleteListener;
        private OnRadioLoadingStartListener onRadioLoadingStartListener;
        private boolean isFilteringRespectToCityEnabled;
        private boolean isFilteringRespectToCategoryEnabled;

        public RadioLoader(@NonNull Context context,
                           String requestUrl,
                           OnRadioLoadingCompleteListener onRadioLoadingCompleteListener,
                           OnRadioLoadingStartListener onRadioLoadingStartListener,
                           boolean isFilteringRespectToCityEnabled,
                           boolean isFilteringRespectToCategoryEnabled) {
            super(context);
            this.requestUrl = requestUrl;
            this.onRadioLoadingCompleteListener = onRadioLoadingCompleteListener;
            this.onRadioLoadingStartListener = onRadioLoadingStartListener;
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
            onRadioLoadingCompleteListener.onRadioLoadingComplete(false);
//            onRadioLoadingStartListener.onRadioLoadingStart();
            forceLoad();
        }
    }

    public void restartLoader() {
        getLoaderManager().restartLoader(RADIO_LOADER_ID, null, this);
    }

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
        List<Radio> radios = radioAdapter.getItems();
        //find the currently playing radio from the radio list
        for (Radio radio: radios) {
            if (radio.getRadioId() == radioCurrentlyPlaying.getRadioId()) {
                switch (statusCode) {
                    case 10: //STATE_BUFFERING
                        radio.setBeingBuffered(true);
                        radioAdapter.notifyDataSetChanged();
                        break;
                    case 11: //STATE_READY
                        radio.setBeingBuffered(false);
                        radioAdapter.notifyDataSetChanged();
                        break;
                    case 12: //STATE_IDLE
                        radio.setBeingBuffered(false);
                        radioAdapter.notifyDataSetChanged();
                        break;
                    default:
                        Log.e(LOG_TAG, "Unknown status code: " + statusCode);
                }
            }
        }
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
        void onRadioItemClick(Radio currentRadio);
    }

    public interface OnRadioLoadingCompleteListener {
        void onRadioLoadingComplete(boolean isRadioLoadingComplete);
    }

    public interface OnRadioLoadingStartListener {
        void onRadioLoadingStart();
    }
}