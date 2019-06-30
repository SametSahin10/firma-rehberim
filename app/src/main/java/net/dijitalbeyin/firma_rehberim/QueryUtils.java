package net.dijitalbeyin.firma_rehberim;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class QueryUtils {
    private final static String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    public static ArrayList<City> fetchCityData(String requestUrl) {
        URL url = createURL(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error retrieving the JSON data", e);
        }
        ArrayList<City> cities = extractCitiesFromJson(jsonResponse);
        return cities;
    }

    private static URL createURL(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error creating the url" + exception);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            int httpResponseCode = urlConnection.getResponseCode();
            if (httpResponseCode == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Http response code " + httpResponseCode);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    private static ArrayList<City> extractCitiesFromJson(String citiesJSONResponse) {
        if (TextUtils.isEmpty(citiesJSONResponse)) {
            return null;
        }
        ArrayList<City> cities = new ArrayList<>();
        try {
            JSONArray rootJsonArray = new JSONArray(citiesJSONResponse);
            if (rootJsonArray.length() > 0) {
                for (int i = 0; i < rootJsonArray.length(); i++) {
                    JSONObject cityObject = rootJsonArray.getJSONObject(i);
                    int cityId = cityObject.getInt("ilId");
                    String cityName = cityObject.getString("kategori");
                    City city = new City(cityId, cityName);
                    cities.add(city);
                }
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problems occured while parsing city JSON results");
        }
        return cities;
    }
}
