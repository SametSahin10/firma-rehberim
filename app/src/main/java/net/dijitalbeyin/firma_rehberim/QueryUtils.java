package net.dijitalbeyin.firma_rehberim;

import android.graphics.Bitmap;
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

    public static ArrayList<Category> fetchCategoryData(String requestUrl) {
        URL url = createURL(requestUrl);
        String jsonResponse = "";
        try {
             jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error retrieving categories JSON response", e);
        }
        ArrayList<Category> categories = extractCategoriesFromJson(jsonResponse);
        return categories;
    }

    public static ArrayList<Radio> fetchRadioData(String requestUrl) {
        URL url = createURL(requestUrl);
        String jsonRespoonse = "";
        try {
            jsonRespoonse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error retrieving radios JSON response");
        }
        ArrayList<Radio> radios = extractRadiosFromJson(jsonRespoonse);
        return radios;
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
            Log.e("QueryUtils", "Problems occured while parsing cities JSON response");
        }
        return cities;
    }

    private static ArrayList<Category> extractCategoriesFromJson(String categoriesJSONResponse) {
        if (TextUtils.isEmpty(categoriesJSONResponse)) {
            return null;
        }
        ArrayList<Category> categories = new ArrayList<>();
        try {
            JSONArray rootJSONArray = new JSONArray(categoriesJSONResponse);
            if (rootJSONArray.length() > 0) {
                for (int i = 0; i < rootJSONArray.length(); i++) {
                    JSONObject categoryObject = rootJSONArray.getJSONObject(i);
                    int categoryId = Integer.parseInt(categoryObject.getString("katId"));
                    String categoryName = categoryObject.getString("kategori");
                    Category category = new Category(categoryId, categoryName);
                    categories.add(category);
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem occured while parsing categories JSON response");
        }
        return categories;
    }

    private static ArrayList<Radio> extractRadiosFromJson(String radiosJSONResponse) {
        if (TextUtils.isEmpty(radiosJSONResponse)) {
            return null;
        }
        ArrayList<Radio> radios = new ArrayList<>();
        try {
            JSONArray rootJSONArray = new JSONArray(radiosJSONResponse);
            for (int i = 0; i < rootJSONArray.length(); i++) {
                JSONObject radioObject = rootJSONArray.getJSONObject(i);
                int radioId = Integer.parseInt(radioObject.getString("id"));
                String radioName = radioObject.getString("baslik");
                String category = radioObject.getString("kategori");
                String radioIconLink = radioObject.getString("resim");
                String streamLink = radioObject.getString("link");
                String shareableLink = radioObject.getString("paylasmaLink");
                int hit = Integer.parseInt(radioObject.getString("hit"));
                int numOfOnlineListeners = Integer.parseInt(radioObject.getString("online"));
                Radio radio = new Radio(radioId, radioName, category, radioIconLink, streamLink, shareableLink, hit, numOfOnlineListeners);
                radios.add(radio);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem occured while parsing radio JSON response ");
        }
        return radios;
    }
}
