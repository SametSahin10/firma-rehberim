package net.dijitalbeyin.firma_rehberim.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.dijitalbeyin.firma_rehberim.helper.QueryUtils;
import net.dijitalbeyin.firma_rehberim.R;
import net.dijitalbeyin.firma_rehberim.adapters.CityAdapter;
import net.dijitalbeyin.firma_rehberim.datamodel.City;

import java.util.ArrayList;
import java.util.List;

public class CitiesFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Object>> {
    private static final String CITIES_REQUEST_URL = "https://firmarehberim.com/bolumler/radyolar/app-json/iller.php";
    private static final int CITY_LOADER_ID = 1;

    GridView gv_cities;
    CityAdapter cityAdapter;
    TextView tv_emptyView;
    ProgressBar pb_loadingCities;

    OnFilterRespectToCityListener onFilterRespectToCityListener;

    public void setOnFilterRespectToCityListener(OnFilterRespectToCityListener onFilterRespectToCityListener) {
        this.onFilterRespectToCityListener = onFilterRespectToCityListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_cities, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork!= null &&
                activeNetwork.isConnectedOrConnecting();

        pb_loadingCities = view.findViewById(R.id.pb_loadingCities);
        gv_cities = view.findViewById(R.id.gv_cities);
        tv_emptyView = view.findViewById(R.id.tv_emptyCityView);
        gv_cities.setEmptyView(tv_emptyView);

        cityAdapter = new CityAdapter(getContext(), R.layout.item_city, new ArrayList<>());
        gv_cities.setAdapter(cityAdapter);
        if (isConnected) {
            getLoaderManager().initLoader(CITY_LOADER_ID, null, this).forceLoad();
        } else {
            pb_loadingCities.setVisibility(View.GONE);
            tv_emptyView.setText(getString(R.string.no_internet_connection_text));
        }

        gv_cities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                City currentCity = (City) parent.getItemAtPosition(position);
                onFilterRespectToCityListener.onFilterRespectToCity(currentCity.getCityName());
            }
        });
    }

    @NonNull
    @Override
    public Loader<List<Object>> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new CityLoader(getContext(), CITIES_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Object>> loader, List<Object> cities) {
        cityAdapter.clear();
        if (cities != null) {
            cityAdapter.addAll(cities);
        }
        pb_loadingCities.setVisibility(View.GONE);
        tv_emptyView.setText(getResources().getString(R.string.empty_cities_text));
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Object>> loader) {
        cityAdapter.clear();
    }

    private static class CityLoader extends AsyncTaskLoader<List<Object>> {
        private String requestUrl;

        public CityLoader(@NonNull Context context, String requestUrl) {
            super(context);
            this.requestUrl = requestUrl;
        }

        @Override
        public List<Object> loadInBackground() {
            if (requestUrl == null) {
                return null;
            }
            ArrayList<Object> cities = QueryUtils.fetchCityData(requestUrl);
            return cities;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }

    public interface OnFilterRespectToCityListener {
        void onFilterRespectToCity(String cityToFilter);
    }
}