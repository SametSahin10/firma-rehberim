package net.dijitalbeyin.firma_rehberim;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CitiesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<City>> {
    private static final String CITIES_REQUEST_URL = "https://firmarehberim.com/sayfalar/radyo/json/iller.php";
    private static final int CITY_LOADER_ID = 1;

    ListView lw_cities;
    CityAdapter cityAdapter;
    TextView tv_emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);

        lw_cities = findViewById(R.id.lw_cities);
        tv_emptyView = findViewById(R.id.tv_emptyView);
        lw_cities.setEmptyView(tv_emptyView);
        cityAdapter = new CityAdapter(this, R.layout.item_city, new ArrayList<City>());
        lw_cities.setAdapter(cityAdapter);
        getSupportLoaderManager().initLoader(CITY_LOADER_ID, null, this);
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
        tv_emptyView.setText(getResources().getString(R.string.tv_empty_view));
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
