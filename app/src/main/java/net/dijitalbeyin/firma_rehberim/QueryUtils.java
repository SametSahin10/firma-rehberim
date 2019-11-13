package net.dijitalbeyin.firma_rehberim;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.exoplayer2.C0514C;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import net.dijitalbeyin.firma_rehberim.data.RadioContract.RadioEntry;
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

    public static User fetchCallerData(String requestUrl) {
        URL url = createURL(requestUrl);
        String jsonResponse =  "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error retrieving caller JSON response");
        }
        User user = extractCallerFromJson(jsonResponse);
        return user;
    }

    public static User fetchUserData(String requestUrl) {
        URL url = createURL(requestUrl);
        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        User user = extractUserFromJson(jsonResponse);
        return user;
    }

    private static URL createURL(String stringUrl) {
        URL url = null;
        try {
            return new URL(str);
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
    private static java.lang.String makeHttpRequest(java.net.URL r7) throws java.io.IOException {
        /*
            java.lang.String r0 = ""
            if (r7 != 0) goto L_0x0005
            return r0
        L_0x0005:
            r1 = 0
            java.net.URLConnection r7 = r7.openConnection()     // Catch:{ IOException -> 0x005a, all -> 0x0057 }
            java.net.HttpURLConnection r7 = (java.net.HttpURLConnection) r7     // Catch:{ IOException -> 0x005a, all -> 0x0057 }
            java.lang.String r2 = "GET"
            r7.setRequestMethod(r2)     // Catch:{ IOException -> 0x0052, all -> 0x0050 }
            r2 = 10000(0x2710, float:1.4013E-41)
            r7.setReadTimeout(r2)     // Catch:{ IOException -> 0x0052, all -> 0x0050 }
            r2 = 15000(0x3a98, float:2.102E-41)
            r7.setConnectTimeout(r2)     // Catch:{ IOException -> 0x0052, all -> 0x0050 }
            r7.connect()     // Catch:{ IOException -> 0x0052, all -> 0x0050 }
            int r2 = r7.getResponseCode()     // Catch:{ IOException -> 0x0052, all -> 0x0050 }
            r3 = 200(0xc8, float:2.8E-43)
            if (r2 != r3) goto L_0x002f
            java.io.InputStream r1 = r7.getInputStream()     // Catch:{ IOException -> 0x0052, all -> 0x0050 }
            java.lang.String r0 = readFromStream(r1)     // Catch:{ IOException -> 0x0052, all -> 0x0050 }
            goto L_0x0045
        L_0x002f:
            java.lang.String r3 = LOG_TAG     // Catch:{ IOException -> 0x0052, all -> 0x0050 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0052, all -> 0x0050 }
            r4.<init>()     // Catch:{ IOException -> 0x0052, all -> 0x0050 }
            java.lang.String r5 = "Http response code "
            r4.append(r5)     // Catch:{ IOException -> 0x0052, all -> 0x0050 }
            r4.append(r2)     // Catch:{ IOException -> 0x0052, all -> 0x0050 }
            java.lang.String r2 = r4.toString()     // Catch:{ IOException -> 0x0052, all -> 0x0050 }
            android.util.Log.e(r3, r2)     // Catch:{ IOException -> 0x0052, all -> 0x0050 }
        L_0x0045:
            if (r7 == 0) goto L_0x004a
            r7.disconnect()
        L_0x004a:
            if (r1 == 0) goto L_0x006d
            r1.close()
            goto L_0x006d
        L_0x0050:
            r0 = move-exception
            goto L_0x0072
        L_0x0052:
            r2 = move-exception
            r6 = r1
            r1 = r7
            r7 = r6
            goto L_0x005c
        L_0x0057:
            r0 = move-exception
            r7 = r1
            goto L_0x0072
        L_0x005a:
            r2 = move-exception
            r7 = r1
        L_0x005c:
            java.lang.String r3 = LOG_TAG     // Catch:{ all -> 0x006e }
            java.lang.String r4 = "Problem retrieving the JSON results"
            android.util.Log.e(r3, r4, r2)     // Catch:{ all -> 0x006e }
            if (r1 == 0) goto L_0x0068
            r1.disconnect()
        L_0x0068:
            if (r7 == 0) goto L_0x006d
            r7.close()
        L_0x006d:
            return r0
        L_0x006e:
            r0 = move-exception
            r6 = r1
            r1 = r7
            r7 = r6
        L_0x0072:
            if (r7 == 0) goto L_0x0077
            r7.disconnect()
        L_0x0077:
            if (r1 == 0) goto L_0x007c
            r1.close()
        L_0x007c:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: net.dijitalbeyin.firma_rehberim.QueryUtils.makeHttpRequest(java.net.URL):java.lang.String");
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (inputStream != null) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName(C0514C.UTF8_NAME)));
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

    private static ArrayList<Radio> extractRadiosFromJson(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        ArrayList<Radio> arrayList = new ArrayList<>();
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
        return arrayList;
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

    private static User extractCallerFromJson(String callerJsonResponse) {
        if (TextUtils.isEmpty(callerJsonResponse)) {
            return null;
        }
        User user = null;
        try {
            JSONObject callerObject = new JSONObject(callerJsonResponse);
            String userWebpageLink = callerObject.getString("seo");
            String userName = callerObject.getString("isim");
            String userPhotoLink = callerObject.getString("resim");
            String authoritativeWebpageLink = callerObject.getString("yetkiliseo");
            String userId = callerObject.getString("id");
            String authoritativeName = callerObject.getString("authoritative");
            //isVerified and match values set as default: false and 211 since there is no information related in the JSON response received.
            user = new User(userWebpageLink, userName, userPhotoLink, false, 211, authoritativeWebpageLink, userId, authoritativeName);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem occured while parsing caller JSON response");
        }
        return user;
    }

    private static User extractUserFromJson(String userJsonResponse) {
        if (TextUtils.isEmpty(userJsonResponse)) {
            return null;
        }
        User user = null;
        try {
            JSONObject userObject = new JSONObject(userJsonResponse);
            String userWebpageLink = userObject.getString("seo");
            String userName = userObject.getString("isim");
            String userPhotoLink = userObject.getString("resim");
            boolean isVerified = userObject.getBoolean("verify");
            int match = userObject.getInt("match");
            String authoritativeWebpageLink = userObject.getString("yetkiliseo");
            String userId = userObject.getString("id");
            String authoritativeName = userObject.getString("authoritative");
            user = new User(userWebpageLink, userName, userPhotoLink, isVerified, match, authoritativeWebpageLink, userId, authoritativeName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }
}
