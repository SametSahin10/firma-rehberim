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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import net.dijitalbeyin.firma_rehberim.adapters.CityAdapter;

public class CitiesFragment extends Fragment implements LoaderCallbacks<List<Object>> {
    private static final String CITIES_REQUEST_URL = "https://firmarehberim.com/sayfalar/radyo/json/iller.php";
    private static final int CITY_LOADER_ID = 1;
    CityAdapter cityAdapter;
    GridView gv_cities;
    OnFilterRespectToCityListener onFilterRespectToCityListener;
    ProgressBar pb_loadingCities;
    TextView tv_emptyView;

    private static class CityLoader extends AsyncTaskLoader<List<Object>> {
        private String requestUrl;

        public CityLoader(@NonNull Context context, String str) {
            super(context);
            this.requestUrl = str;
        }

        public List<Object> loadInBackground() {
            String str = this.requestUrl;
            if (str == null) {
                return null;
            }
            return QueryUtils.fetchCityData(str);
        }

        /* access modifiers changed from: protected */
        public void onStartLoading() {
            forceLoad();
        }
    }

    public interface OnFilterRespectToCityListener {
        void onFilterRespectToCity(int i);
    }

    public void setOnFilterRespectToCityListener(OnFilterRespectToCityListener onFilterRespectToCityListener2) {
        this.onFilterRespectToCityListener = onFilterRespectToCityListener2;
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        return (ViewGroup) layoutInflater.inflate(C0662R.layout.fragment_cities, viewGroup, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        super.onViewCreated(view, bundle);
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) getActivity().getSystemService("connectivity")).getActiveNetworkInfo();
        boolean z = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        this.pb_loadingCities = (ProgressBar) view.findViewById(C0662R.C0664id.pb_loadingCities);
        this.gv_cities = (GridView) view.findViewById(C0662R.C0664id.gv_cities);
        this.tv_emptyView = (TextView) view.findViewById(C0662R.C0664id.tv_emptyCityView);
        this.gv_cities.setEmptyView(this.tv_emptyView);
        this.cityAdapter = new CityAdapter(getContext(), C0662R.layout.item_city, new ArrayList());
        this.gv_cities.setAdapter(this.cityAdapter);
        if (z) {
            getLoaderManager().initLoader(1, null, this).forceLoad();
        } else {
            this.pb_loadingCities.setVisibility(8);
            this.tv_emptyView.setText(getString(C0662R.string.no_internet_connection_text));
        }
        this.gv_cities.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                CitiesFragment.this.onFilterRespectToCityListener.onFilterRespectToCity(((City) adapterView.getItemAtPosition(i)).getCityId());
            }
        });
    }

    @NonNull
    public Loader<List<Object>> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new CityLoader(getContext(), CITIES_REQUEST_URL);
    }

    public void onLoadFinished(@NonNull Loader<List<Object>> loader, List<Object> list) {
        this.cityAdapter.clear();
        if (list != null) {
            this.cityAdapter.addAll(list);
        }
        this.pb_loadingCities.setVisibility(8);
        this.tv_emptyView.setText(getResources().getString(C0662R.string.empty_cities_text));
    }

    public void onLoaderReset(@NonNull Loader<List<Object>> loader) {
        this.cityAdapter.clear();
    }
}
