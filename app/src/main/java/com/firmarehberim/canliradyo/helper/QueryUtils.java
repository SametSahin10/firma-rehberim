package com.firmarehberim.canliradyo.helper;

import android.text.TextUtils;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.SQLOutput;
import java.util.ArrayList;
import com.firmarehberim.canliradyo.data.RadioContract.RadioEntry;
import com.firmarehberim.canliradyo.datamodel.Category;
import com.firmarehberim.canliradyo.datamodel.City;
import com.firmarehberim.canliradyo.datamodel.Radio;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QueryUtils {
    private static final String LOG_TAG = "QueryUtils";

    private QueryUtils() {
    }

    public static ArrayList<Object> fetchCityData(String str) {
        String str2;
        try {
            str2 = makeHttpRequest(createURL(str));
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error retrieving the JSON data", e);
            str2 = null;
        }
        return extractCitiesFromJson(str2);
    }

    public static ArrayList<Object> fetchCategoryData(String str) {
        String str2;
        try {
            str2 = makeHttpRequest(createURL(str));
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error retrieving categories JSON response", e);
            str2 = "";
        }
        return extractCategoriesFromJson(str2);
    }

    public static ArrayList<Radio> fetchRadioData(String str) {
        String str2;
        try {
            str2 = makeHttpRequest(createURL(str));
        } catch (IOException unused) {
            Log.e(LOG_TAG, "Error retrieving radios JSON response");
            str2 = "";
        }
        return extractRadiosFromJson(str2);
    }

    public static ArrayList<Radio> fetchRadioDataThroughCities(String str) {
        String str2;
        try {
            str2 = makeHttpRequest(createURL(str));
        } catch (IOException unused) {
            Log.e(LOG_TAG, "Error retrieving radios JSON response");
            str2 = "";
        }
        return extractRadiosThroughCitiesFromJson(str2);
    }

    public static ArrayList<Radio> fetchRadioDataThroughCategories(String str) {
        String str2;
        try {
            str2 = makeHttpRequest(createURL(str));
        } catch (IOException unused) {
            Log.e(LOG_TAG, "Error retrieving radios JSON response");
            str2 = "";
        }
        return extractRadiosThroughCategoriesFromJson(str2);
    }

    public static ArrayList<Radio> fetchFavouriteRadioData(String str) {
        String str2;
        try {
            str2 = makeHttpRequest(createURL(str));
        } catch (IOException unused) {
            Log.e(LOG_TAG, "Error retrieving radios JSON response");
            str2 = "";
        }
        return extractFavouriteRadiosFromJson(str2);
    }

    private static URL createURL(String stringUrl) {
        URL url = null;
        try {
            return new URL(stringUrl);
        } catch (MalformedURLException e) {
            String str2 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Error creating the url");
            sb.append(e);
            Log.e(str2, sb.toString());
            return null;
        }
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
        StringBuilder sb = new StringBuilder();
        if (inputStream != null) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
            for (String readLine = bufferedReader.readLine(); readLine != null; readLine = bufferedReader.readLine()) {
                sb.append(readLine);
            }
        }
        return sb.toString();
    }

    private static ArrayList<Object> extractCitiesFromJson(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        ArrayList<Object> arrayList = new ArrayList<>();
        try {
            JSONArray jSONArray = new JSONArray(str);
            if (jSONArray.length() > 0) {
                for (int i = 0; i < jSONArray.length(); i++) {
                    JSONObject jSONObject = jSONArray.getJSONObject(i);
                    arrayList.add(new City(jSONObject.getInt("ilId"), jSONObject.getString("kategori")));
                }
            }
        } catch (JSONException unused) {
            Log.e(LOG_TAG, "Problems occured while parsing cities JSON response");
        }
        return arrayList;
    }

    private static ArrayList<Object> extractCategoriesFromJson(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        ArrayList<Object> arrayList = new ArrayList<>();
        try {
            JSONArray jSONArray = new JSONArray(str);
            if (jSONArray.length() > 0) {
                for (int i = 0; i < jSONArray.length(); i++) {
                    JSONObject jSONObject = jSONArray.getJSONObject(i);
            arrayList.add(new Category(Integer.parseInt(jSONObject.getString("katId")), jSONObject.getString("kategori")));
                }
            }
        } catch (JSONException unused) {
            Log.e(LOG_TAG, "Problem occured while parsing categories JSON response");
        }
        return arrayList;
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
                int cityId = Integer.parseInt(radioObject.getString("ilId"));
                int townId;
                if (radioObject.getString("ilceId").equals("null")) {
                    townId = 0;
                } else {
                    townId = Integer.parseInt(radioObject.getString("ilceId"));
                }
                int neighbourhoodId = Integer.parseInt(radioObject.getString("mahalleId"));
                String rawRadioIconLink = radioObject.getString("resim");
                String radioIconLink = "https:" + rawRadioIconLink;
                String shareableLink = radioObject.getString("paylasmaLink");
                String radioName = radioObject.getString("baslik");
                String streamLink = radioObject.getString("link");
                int hit = Integer.parseInt(radioObject.getString("hit"));
                String categoryId = radioObject.getString("katId");
                int userId = Integer.parseInt(radioObject.getString("uyeId"));
                String category = radioObject.getString("kategori");
                int numOfOnlineListeners = Integer.parseInt(radioObject.getString("online"));
                boolean isRadioInHLSFormat = isInHLSFormat(streamLink);
                Radio radio = new Radio(radioId,
                                        cityId,
                                        townId,
                                        neighbourhoodId,
                                        radioIconLink,
                                        shareableLink,
                                        radioName,
                                        streamLink,
                                        isRadioInHLSFormat,
                                        hit,
                                        categoryId,
                                        userId,
                                        category,
                                        numOfOnlineListeners,
                                        false,
                                        false,
                                        false);
                radios.add(radio);
            }
        } catch (JSONException unused) {
            Log.e(LOG_TAG, "Problem occured while parsing radio JSON response ");
        }
        return radios;
    }

    private static ArrayList<Radio> extractFavouriteRadiosFromJson(String radiosJSONResponse) {
        if (TextUtils.isEmpty(radiosJSONResponse)) {
            return null;
        }
        ArrayList<Radio> radios = new ArrayList<>();
        try {
            JSONArray rootJSONArray = new JSONArray(radiosJSONResponse);
            for (int i = 0; i < rootJSONArray.length(); i++) {
                JSONObject radioObject = rootJSONArray.getJSONObject(i);
                int radioId = Integer.parseInt(radioObject.getString("id"));
                String rawRadioIconLink = radioObject.getString("resim");
                String radioIconLink = "https:" + rawRadioIconLink;
                String shareableLink = radioObject.getString("paylasmaLink");
                String radioName = radioObject.getString("baslik");
                String streamLink = radioObject.getString("link");
                int hit = Integer.parseInt(radioObject.getString("hit"));
                int userId = Integer.parseInt(radioObject.getString("uyeid"));
                String category = radioObject.getString("kategori");
                int numOfOnlineListeners = Integer.parseInt(radioObject.getString("online"));
                boolean isRadioInHLSFormat = isInHLSFormat(streamLink);
                Radio radio = new Radio(radioId,
                                        3001,
                                        3001,
                                        3001,
                                        radioIconLink,
                                        shareableLink,
                                        radioName,
                                        streamLink,
                                        isRadioInHLSFormat,
                                        hit,
                                        null,
                                        userId,
                                        category,
                                        numOfOnlineListeners,
                                        false,
                                        false,
                                        false);
                radios.add(radio);
            }
        } catch (JSONException unused) {
            Log.e(LOG_TAG, "Problem occured while parsing favourite radio JSON response ");
        }
        return radios;
    }

    private static ArrayList<Radio> extractRadiosThroughCitiesFromJson(String radiosJSONResponse) {
        if (TextUtils.isEmpty(radiosJSONResponse)) {
            return null;
        }
        ArrayList<Radio> radios = new ArrayList<>();
        try {
            JSONArray JSONArray = new JSONArray(radiosJSONResponse);
            for (int i = 0; i < JSONArray.length(); i++) {
                JSONObject radioObject = JSONArray.getJSONObject(i);
                int radioId = Integer.parseInt(radioObject.getString("id"));
                int cityId = Integer.parseInt(radioObject.getString("ilId"));
                String rawRadioIconLink = radioObject.getString("resim");
                String radioIconLink = "https:" + rawRadioIconLink;
                String shareableLink = radioObject.getString("paylasmaLink");
                String radioName = radioObject.getString("baslik");
                String streamLink = radioObject.getString("link");
                int hit = Integer.parseInt(radioObject.getString("hit"));
                String category = radioObject.getString("kategori");
                int numOfOnlineListeners = Integer.parseInt(radioObject.getString("online"));
                boolean isRadioInHLSFormat = isInHLSFormat(streamLink);
                Radio radio = new Radio(radioId,
                                        cityId,
                                        3001,
                                        3001,
                                        radioIconLink,
                                        shareableLink,
                                        radioName,
                                        streamLink,
                                        isRadioInHLSFormat,
                                        hit,
                                        null,
                                        3001,
                                        category,
                                        numOfOnlineListeners,
                                        false,
                                        false,
                                        false);
                radios.add(radio);
            }
        } catch (JSONException unused) {
            Log.e(LOG_TAG, "Problem occured while parsing radios through cities JSON response ");
        }
        return radios;
    }

    private static ArrayList<Radio> extractRadiosThroughCategoriesFromJson(String radiosJSONResponse) {
        if (TextUtils.isEmpty(radiosJSONResponse)) {
            return null;
        }
        ArrayList<Radio> radios = new ArrayList<>();
        try {
            JSONArray JSONArray = new JSONArray(radiosJSONResponse);
            for (int i = 0; i < JSONArray.length(); i++) {
                JSONObject radioObject = JSONArray.getJSONObject(i);
                int radioId = Integer.parseInt(radioObject.getString("id"));
                String rawRadioIconLink = radioObject.getString("resim");
                String radioIconLink = "https:" + rawRadioIconLink;
                String shareableLink = radioObject.getString("paylasmaLink");
                String radioName = radioObject.getString("baslik");
                String streamLink = radioObject.getString("link");
                int hit = Integer.parseInt(radioObject.getString("hit"));
                String category = radioObject.getString("kategori");
                int numOfOnlineListeners = Integer.parseInt(radioObject.getString("online"));
                boolean isRadioInHLSFormat = isInHLSFormat(streamLink);
                Radio radio = new Radio(radioId,
                                        3001,
                                        3001,
                                        3001,
                                        radioIconLink,
                                        shareableLink,
                                        radioName,
                                        streamLink,
                                        isRadioInHLSFormat,
                                        hit,
                                        null,
                                        3001,
                                        category,
                                        numOfOnlineListeners,
                                        false,
                                        false,
                                        false);
                radios.add(radio);
            }
        } catch (JSONException unused) {
            Log.e(LOG_TAG, "Problem occured while parsing radios through cities JSON response ");
        }
        return radios;
    }

    private static boolean isInHLSFormat(String streamLink) {
        String[] splitStreamLink = streamLink.split("\\.");
        if (splitStreamLink.length == 0) return false;
        String lastSegment = splitStreamLink[splitStreamLink.length - 1];
        if (lastSegment.equals("m3u8")) return true;
        return false;
    }
}
