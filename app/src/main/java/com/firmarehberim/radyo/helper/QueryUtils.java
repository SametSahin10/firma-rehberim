package com.firmarehberim.radyo.helper;

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
import java.util.ArrayList;
import com.firmarehberim.radyo.data.RadioContract.RadioEntry;
import com.firmarehberim.radyo.datamodel.Category;
import com.firmarehberim.radyo.datamodel.City;
import com.firmarehberim.radyo.datamodel.Radio;

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

    /* JADX WARNING: type inference failed for: r1v0 */
    /* JADX WARNING: type inference failed for: r7v1, types: [java.net.HttpURLConnection] */
    /* JADX WARNING: type inference failed for: r1v1, types: [java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r7v2, types: [java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r1v2, types: [java.net.HttpURLConnection] */
    /* JADX WARNING: type inference failed for: r6v0 */
    /* JADX WARNING: type inference failed for: r1v3 */
    /* JADX WARNING: type inference failed for: r7v3 */
    /* JADX WARNING: type inference failed for: r7v4 */
    /* JADX WARNING: type inference failed for: r7v5 */
    /* JADX WARNING: type inference failed for: r1v4 */
    /* JADX WARNING: type inference failed for: r7v8 */
    /* JADX WARNING: type inference failed for: r1v5, types: [java.io.InputStream] */
    /* JADX WARNING: type inference failed for: r1v7 */
    /* JADX WARNING: type inference failed for: r1v8 */
    /* JADX WARNING: type inference failed for: r1v9 */
    /* JADX WARNING: type inference failed for: r1v10 */
    /* JADX WARNING: type inference failed for: r7v9 */
    /* JADX WARNING: type inference failed for: r1v11 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0065  */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x006a  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0074  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0079  */
    /* JADX WARNING: Unknown variable types count: 7 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
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
                Radio radio = new Radio(radioId,
                                        radioName,
                                        cityId,
                                        townId,
                                        neighbourhoodId,
                                        categoryId,
                                        userId,
                                        category,
                                        radioIconLink,
                                        streamLink,
                                        shareableLink,
                                        hit,
                                        numOfOnlineListeners,
                                        false,
                                        false);
                radios.add(radio);
            }
        } catch (JSONException unused) {
            Log.e(LOG_TAG, "Problem occured while parsing radio JSON response ");
        }
        return radios;
    }

    private static ArrayList<Radio> extractFavouriteRadiosFromJson(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        ArrayList<Radio> arrayList = new ArrayList<>();
        try {
            JSONArray jSONArray = new JSONArray(str);
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObject = jSONArray.getJSONObject(i);
                int parseInt = Integer.parseInt(jSONObject.getString("id"));
                String string = jSONObject.getString("resim");
                StringBuilder sb = new StringBuilder();
                sb.append("https:");
                sb.append(string);
                String sb2 = sb.toString();
                String string2 = jSONObject.getString("paylasmaLink");
                String str2 = "30001";
                Radio radio = new Radio(parseInt, jSONObject.getString("baslik"), 30001, 30001, 30001, str2, 30001, jSONObject.getString("kategori"), sb2, jSONObject.getString("link"), string2, Integer.parseInt(jSONObject.getString(RadioEntry.COLUMN_RADIO_HIT)), Integer.parseInt(jSONObject.getString("online")), false, false);
                arrayList.add(radio);
            }
        } catch (JSONException unused) {
            Log.e(LOG_TAG, "Problem occured while parsing favourite radio JSON response ");
        }
        return arrayList;
    }

    private static ArrayList<Radio> extractRadiosThroughCitiesFromJson(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        ArrayList<Radio> arrayList = new ArrayList<>();
        try {
            JSONArray jSONArray = new JSONArray(str);
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObject = jSONArray.getJSONObject(i);
                int parseInt = Integer.parseInt(jSONObject.getString("id"));
                String string = jSONObject.getString("resim");
                StringBuilder sb = new StringBuilder();
                sb.append("https:");
                sb.append(string);
                String sb2 = sb.toString();
                String string2 = jSONObject.getString("paylasmaLink");
                String str2 = "30001";
                Radio radio = new Radio(parseInt, jSONObject.getString("baslik"), 30001, 30001, 30001, str2, 30001, jSONObject.getString("kategori"), sb2, jSONObject.getString("link"), string2, Integer.parseInt(jSONObject.getString(RadioEntry.COLUMN_RADIO_HIT)), Integer.parseInt(jSONObject.getString("online")), false, false);
                arrayList.add(radio);
            }
        } catch (JSONException unused) {
            Log.e(LOG_TAG, "Problem occured while parsing radios through cities JSON response ");
        }
        return arrayList;
    }

    private static ArrayList<Radio> extractRadiosThroughCategoriesFromJson(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        ArrayList<Radio> arrayList = new ArrayList<>();
        try {
            JSONArray jSONArray = new JSONArray(str);
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObject = jSONArray.getJSONObject(i);
                int parseInt = Integer.parseInt(jSONObject.getString("id"));
                String string = jSONObject.getString("resim");
                StringBuilder sb = new StringBuilder();
                sb.append("https:");
                sb.append(string);
                String sb2 = sb.toString();
                String string2 = jSONObject.getString("paylasmaLink");
                String str2 = "30001";
                Radio radio = new Radio(parseInt, jSONObject.getString("baslik"), 30001, 30001, 30001, str2, 30001, jSONObject.getString("kategori"), sb2, jSONObject.getString("link"), string2, Integer.parseInt(jSONObject.getString(RadioEntry.COLUMN_RADIO_HIT)), Integer.parseInt(jSONObject.getString("online")), false, false);
                arrayList.add(radio);
            }
        } catch (JSONException unused) {
            Log.e(LOG_TAG, "Problem occured while parsing radios through cities JSON response ");
        }
        return arrayList;
    }
}
