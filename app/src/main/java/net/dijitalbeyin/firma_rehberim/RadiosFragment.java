package net.dijitalbeyin.firma_rehberim;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.p000v4.app.Fragment;
import android.support.p000v4.app.LoaderManager.LoaderCallbacks;
import android.support.p000v4.content.AsyncTaskLoader;
import android.support.p000v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import net.dijitalbeyin.firma_rehberim.adapters.RadioAdapter;
import net.dijitalbeyin.firma_rehberim.adapters.RadioAdapter.OnAddToFavouritesListener;
import net.dijitalbeyin.firma_rehberim.adapters.RadioAdapter.OnDeleteFromFavouritesListener;

public class RadiosFragment extends Fragment implements LoaderCallbacks<List<Radio>>, OnAddToFavouritesListener, OnDeleteFromFavouritesListener {
    private static final String LOG_TAG = "RadiosFragment";
    private static final int RADIO_LOADER_ID = 1;
    private static final String RADIO_REQUEST_URL = "https://firmarehberim.com/sayfalar/radyo/json/radyolar_arama.php?q=";
    private static final String RADIO_REQUEST_URL_RESPECT_TO_CATEGORY = "https://firmarehberim.com/sayfalar/radyo/json/radyolar_kategori.php?q=";
    private static final String RADIO_REQUEST_URL_RESPECT_TO_CITY = "https://firmarehberim.com/sayfalar/radyo/json/radyolar_iller.php?q=";
    int categoryIdToFilter;
    int cityIdToFilter;
    boolean isFilteringRespectToCategoryEnabled = false;
    boolean isFilteringRespectToCityEnabled = false;
    boolean isFilteringThroughSearchViewEnabled = false;
    private ListView lw_radios;
    OnEventFromRadiosFragmentListener onEventFromRadiosFragmentListener;
    OnRadioItemClickListener onRadioItemClickListener;
    OnRadioLoadingCompleteListener onRadioLoadingCompleteListener;
    OnRadioLoadingStartListener onRadioLoadingStartListener;

    public interface OnEventFromRadiosFragmentListener {
        void onEventFromRadiosFragment(int i, boolean z);
    }

    public interface OnRadioItemClickListener {
        void onRadioItemClick(Radio radio);
    }

    public interface OnRadioLoadingCompleteListener {
        void onRadioLoadingComplete(boolean z);
    }

    public void setOnRadioLoadingStartListener(OnRadioLoadingStartListener onRadioLoadingStartListener) {
        this.onRadioLoadingStartListener = onRadioLoadingStartListener;
    }

    String cityToFilter;
    int categoryIdToFilter;
    String queryFromSearchView;
    boolean isFilteringRespectToCityEnabled = false;
    boolean isFilteringRespectToCategoryEnabled = false;
    boolean isFilteringThroughSearchViewEnabled = false;

        public RadioLoader(@NonNull Context context, String str, OnRadioLoadingCompleteListener onRadioLoadingCompleteListener2, boolean z, boolean z2) {
            super(context);
            this.requestUrl = str;
            this.onRadioLoadingCompleteListener = onRadioLoadingCompleteListener2;
            this.isFilteringRespectToCityEnabled = z;
            this.isFilteringRespectToCategoryEnabled = z2;
        }

        public List<Radio> loadInBackground() {
            if (this.isFilteringRespectToCityEnabled) {
                return QueryUtils.fetchRadioDataThroughCities(this.requestUrl);
            }
            if (this.isFilteringRespectToCategoryEnabled) {
                return QueryUtils.fetchRadioDataThroughCategories(this.requestUrl);
            }
            Log.d("TAG", "Fetching all the radios");
            return QueryUtils.fetchRadioData(this.requestUrl);
        }

        /* access modifiers changed from: protected */
        public void onStartLoading() {
            this.onRadioLoadingCompleteListener.onRadioLoadingComplete(false);
            forceLoad();
        }
    }

    public void setOnEventFromRadiosFragmentListener(OnEventFromRadiosFragmentListener onEventFromRadiosFragmentListener2) {
        this.onEventFromRadiosFragmentListener = onEventFromRadiosFragmentListener2;
    }

    public void setQueryFromSearchView(String queryFromSearchView) {
        this.queryFromSearchView = queryFromSearchView;
    }

    public void setFilteringRespectToCityEnabled(boolean filteringRespectToCityEnabled) {
        isFilteringRespectToCityEnabled = filteringRespectToCityEnabled;
    }

    public void setOnRadioLoadingCompleteListener(OnRadioLoadingCompleteListener onRadioLoadingCompleteListener2) {
        this.onRadioLoadingCompleteListener = onRadioLoadingCompleteListener2;
    }

    public void setFilteringThroughSearchViewEnabled(boolean filteringThroughSearchViewEnabled) {
        isFilteringThroughSearchViewEnabled = filteringThroughSearchViewEnabled;
    }

    ListView lw_radios;
    RadioAdapter radioAdapter;
    private TextView tv_emptyView;
    ProgressBar pb_loadingRadios;
    private ProgressBar pb_bufferingRadio;

    public void setCategoryToFilter(int i) {
        this.categoryIdToFilter = i;
    }

    public void setQueryFromSearchView(String str) {
        this.queryFromSearchView = str;
    }

    public void setFilteringRespectToCityEnabled(boolean z) {
        this.isFilteringRespectToCityEnabled = z;
    }

    public void setFilteringRespectToCategoryEnabled(boolean z) {
        this.isFilteringRespectToCategoryEnabled = z;
    }

    public void setFilteringThroughSearchViewEnabled(boolean z) {
        this.isFilteringThroughSearchViewEnabled = z;
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        ViewGroup viewGroup2 = (ViewGroup) layoutInflater.inflate(C0662R.layout.fragment_radios, viewGroup, false);
        Log.d("TAG", "onCreateView: RadiosFragment");
        return viewGroup2;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) getActivity().getSystemService("connectivity")).getActiveNetworkInfo();
        boolean z = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        this.pb_loadingRadios = (ProgressBar) view.findViewById(C0662R.C0664id.pb_loadingRadios);
        this.pb_bufferingRadio = (ProgressBar) view.findViewById(C0662R.C0664id.pb_buffering_radio);
        this.lw_radios = (ListView) view.findViewById(C0662R.C0664id.lw_radios);
        this.tv_emptyView = (TextView) view.findViewById(C0662R.C0664id.tv_emptyRadioView);
        this.lw_radios.setEmptyView(this.tv_emptyView);
        RadioAdapter radioAdapter2 = new RadioAdapter(getContext(), C0662R.layout.item_radio, new ArrayList(), this, this);
        this.radioAdapter = radioAdapter2;
        this.lw_radios.setAdapter(this.radioAdapter);
        if (z) {
            getLoaderManager().initLoader(1, null, this).forceLoad();
        } else {
            Log.d("TAG", "No Network Connection");
            this.tv_emptyView.setText(getString(C0662R.string.no_internet_connection_text));
            this.pb_loadingRadios.setVisibility(8);
        }
        this.lw_radios.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (RadiosFragment.this.radioClicked != null) {
                    RadiosFragment.this.radioClicked.setBeingBuffered(false);
                    RadiosFragment.this.radioAdapter.notifyDataSetChanged();
                }
                RadiosFragment.this.radioClicked = (Radio) adapterView.getItemAtPosition(i);
                RadiosFragment.this.radioClicked.setBeingBuffered(true);
                RadiosFragment.this.radioAdapter.notifyDataSetChanged();
                RadiosFragment.this.onRadioItemClickListener.onRadioItemClick(RadiosFragment.this.radioClicked);
            }
        });
    }

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

    public void onLoaderReset(@NonNull Loader<List<Radio>> loader) {
        this.radioAdapter.clear();
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
        this.radioAdapter.notifyDataSetChanged();
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
                    case 11:
                        radio2.setBeingBuffered(false);
                        this.radioAdapter.notifyDataSetChanged();
                        break;
                    case 12:
                        radio2.setBeingBuffered(false);
                        this.radioAdapter.notifyDataSetChanged();
                        break;
                    default:
                        String str = LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Unknown status code: ");
                        sb.append(i);
                        Log.e(str, sb.toString());
                        break;
                }
            }
        }
    }

    public void restartLoader() {
        getLoaderManager().restartLoader(1, null, this);
    }

    public void onAddToFavouritesClick(int i) {
        this.onEventFromRadiosFragmentListener.onEventFromRadiosFragment(i, true);
    }

    public void onDeleteFromFavouritesClick(int i) {
        this.onEventFromRadiosFragmentListener.onEventFromRadiosFragment(i, false);
    }

    public interface OnRadioLoadingStartListener {
        void onRadioLoadingStart();
    }
}
