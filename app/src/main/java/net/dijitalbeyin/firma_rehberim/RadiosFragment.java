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
    private ProgressBar pb_bufferingRadio;
    private ProgressBar pb_loadingRadios;
    String queryFromSearchView;
    RadioAdapter radioAdapter;
    Radio radioClicked;
    private TextView tv_emptyView;

    public interface OnEventFromRadiosFragmentListener {
        void onEventFromRadiosFragment(int i, boolean z);
    }

    public interface OnRadioItemClickListener {
        void onRadioItemClick(Radio radio);
    }

    public interface OnRadioLoadingCompleteListener {
        void onRadioLoadingComplete(boolean z);
    }

    private static class RadioLoader extends AsyncTaskLoader<List<Radio>> {
        private boolean isFilteringRespectToCategoryEnabled;
        private boolean isFilteringRespectToCityEnabled;
        private OnRadioLoadingCompleteListener onRadioLoadingCompleteListener;
        private String requestUrl;

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

    public void setOnRadioItemClickListener(OnRadioItemClickListener onRadioItemClickListener2) {
        this.onRadioItemClickListener = onRadioItemClickListener2;
    }

    public void setOnRadioLoadingCompleteListener(OnRadioLoadingCompleteListener onRadioLoadingCompleteListener2) {
        this.onRadioLoadingCompleteListener = onRadioLoadingCompleteListener2;
    }

    public void setCityToFilter(int i) {
        this.cityIdToFilter = i;
    }

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
        String str = "TAG";
        if (this.isFilteringRespectToCityEnabled) {
            Log.d(str, "Filtering respect to city");
            Context context = getContext();
            StringBuilder sb = new StringBuilder();
            sb.append(RADIO_REQUEST_URL_RESPECT_TO_CITY);
            sb.append(this.cityIdToFilter);
            RadioLoader radioLoader = new RadioLoader(context, sb.toString(), this.onRadioLoadingCompleteListener, true, false);
            return radioLoader;
        }
        String str2 = "Filtering respect to category";
        if (this.isFilteringRespectToCategoryEnabled) {
            Log.d(str, str2);
            Context context2 = getContext();
            StringBuilder sb2 = new StringBuilder();
            sb2.append(RADIO_REQUEST_URL_RESPECT_TO_CATEGORY);
            sb2.append(this.categoryIdToFilter);
            RadioLoader radioLoader2 = new RadioLoader(context2, sb2.toString(), this.onRadioLoadingCompleteListener, false, true);
            return radioLoader2;
        } else if (this.isFilteringThroughSearchViewEnabled) {
            Log.d(str, str2);
            Context context3 = getContext();
            StringBuilder sb3 = new StringBuilder();
            sb3.append(RADIO_REQUEST_URL);
            sb3.append(this.queryFromSearchView);
            RadioLoader radioLoader3 = new RadioLoader(context3, sb3.toString(), this.onRadioLoadingCompleteListener, false, false);
            return radioLoader3;
        } else {
            Log.d(str, "Not applying any filter");
            RadioLoader radioLoader4 = new RadioLoader(getContext(), RADIO_REQUEST_URL, this.onRadioLoadingCompleteListener, false, false);
            return radioLoader4;
        }
    }

    public void onLoadFinished(@NonNull Loader<List<Radio>> loader, List<Radio> list) {
        this.radioAdapter.setPermanentRadiosList(list);
        this.onRadioLoadingCompleteListener.onRadioLoadingComplete(true);
        this.radioAdapter.clear();
        if (list != null) {
            this.radioAdapter.addAll(list);
        }
        this.tv_emptyView.setText(getString(C0662R.string.empty_radios_text));
        this.pb_loadingRadios.setVisibility(8);
    }

    public void onLoaderReset(@NonNull Loader<List<Radio>> loader) {
        this.radioAdapter.clear();
    }

    public void refreshRadiosList(int i) {
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("radioId: ");
        sb.append(i);
        Log.d(str, sb.toString());
        for (Radio radio : this.radioAdapter.getItems()) {
            if (radio.getRadioId() == i) {
                radio.setLiked(false);
            }
        }
        this.radioAdapter.notifyDataSetChanged();
    }

    public void setCurrentRadioStatus(int i, Radio radio) {
        for (Radio radio2 : this.radioAdapter.getItems()) {
            if (radio2.getRadioId() == radio.getRadioId()) {
                switch (i) {
                    case 10:
                        radio2.setBeingBuffered(true);
                        this.radioAdapter.notifyDataSetChanged();
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
}
