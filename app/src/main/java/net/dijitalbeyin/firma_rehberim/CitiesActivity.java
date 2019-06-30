package net.dijitalbeyin.firma_rehberim;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CitiesActivity extends AppCompatActivity {
    private static final String CITIES_REQUEST_URL = "https://firmarehberim.com/sayfalar/radyo/json/iller.php";

    ListView lw_cities;
    CityAdapter cityAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);

        lw_cities = findViewById(R.id.lw_cities);
        cityAdapter = new CityAdapter(this, R.layout.item_city, new ArrayList<City>());
        lw_cities.setAdapter(cityAdapter);
        CityAsyncTask task = new CityAsyncTask();
        task.execute(CITIES_REQUEST_URL);
    }

    private class CityAsyncTask extends AsyncTask<String, Void, List<City>> {
        @Override
        protected List<City> doInBackground(String... requestUrls) {
            if (requestUrls.length < 1 || requestUrls[0] == null) {
                return null;
            }
            ArrayList<City> cities = QueryUtils.fetchCityData(requestUrls[0]);
            return cities;
        }
        @Override
        protected void onPostExecute(List<City> citiesResult) {
            cityAdapter.clear();
            if (citiesResult != null && !citiesResult.isEmpty()) {
                cityAdapter.addAll(citiesResult);
            }
        }
    }
}
