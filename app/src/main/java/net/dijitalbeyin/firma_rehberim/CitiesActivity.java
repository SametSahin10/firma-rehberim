package net.dijitalbeyin.firma_rehberim;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CitiesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<City>> {
    private static final String CITIES_REQUEST_URL = "https://firmarehberim.com/sayfalar/radyo/json/iller.php";
    private static final int CITY_LOADER_ID = 1;

    ListView lw_cities;
    CityAdapter cityAdapter;
    TextView tv_emptyView;
    ProgressBar pb_loadingCities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork!= null &&
                              activeNetwork.isConnectedOrConnecting();

        pb_loadingCities = findViewById(R.id.pb_loadingCities);
        lw_cities = findViewById(R.id.lw_cities);
        tv_emptyView = findViewById(R.id.tv_emptyCityView);
        lw_cities.setEmptyView(tv_emptyView);
        cityAdapter = new CityAdapter(this, R.layout.item_city, new ArrayList<City>());
        lw_cities.setAdapter(cityAdapter);
        if (isConnected) {
            getSupportLoaderManager().initLoader(CITY_LOADER_ID, null, this).forceLoad();
        } else {
            pb_loadingCities.setVisibility(View.GONE);
            tv_emptyView.setText(getString(R.string.no_internet_connection_text));
        }
    }

    @NonNull
    @Override
    public Loader<List<City>> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new CityLoader(this, CITIES_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<City>> loader, List<City> cities) {
        cityAdapter.clear();
        if (cities != null) {
            cityAdapter.addAll(cities);
        }
        pb_loadingCities.setVisibility(View.GONE);
        tv_emptyView.setText(getResources().getString(R.string.empty_cities_text));
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<City>> loader) {
        cityAdapter.clear();
    }

    private static class CityLoader extends AsyncTaskLoader<List<City>> {
        private String requestUrl;

        public CityLoader(@NonNull Context context, String requestUrl) {
            super(context);
            this.requestUrl = requestUrl;
        }

        @Override
        public List<City> loadInBackground() {
            if (requestUrl == null) {
                return null;
            }
            ArrayList<City> cities = QueryUtils.fetchCityData(requestUrl);
            return cities;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }
}
