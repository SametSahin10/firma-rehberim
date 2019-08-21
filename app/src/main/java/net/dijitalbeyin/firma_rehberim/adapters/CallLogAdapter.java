package net.dijitalbeyin.firma_rehberim.adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import net.dijitalbeyin.firma_rehberim.QueryUtils;
import net.dijitalbeyin.firma_rehberim.R;
import net.dijitalbeyin.firma_rehberim.User;

import java.util.ArrayList;
import java.util.List;

public class CallLogAdapter extends CursorAdapter implements LoaderManager.LoaderCallbacks<User> {
    private static String USER_REQUEST_URL = "https://firmarehberim.com/inc/telephone.php?no=";

    private Context context;
    private Cursor cursor;
    private String query;

    public CallLogAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        this.context = context;
        this.cursor = cursor;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_call_log, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        Log.d("TAG", "bindView");
        final TextView tv_company_name = view.findViewById(R.id.tv_company_name);
        final TextView tv_authoritative_name = view.findViewById(R.id.tv_authoritative_name);
        TextView tv_call_type = view.findViewById(R.id.tv_call_type);
        TextView tv_date_time_info = view.findViewById(R.id.tv_call_date_info);

        int numberColumnIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
        int callTypeColumnIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
        int callDateColumnIndex = cursor.getColumnIndex(CallLog.Calls.DATE);

        String number = cursor.getString(numberColumnIndex);
        String callType = cursor.getString(callTypeColumnIndex);
        String callDate = cursor.getString(callDateColumnIndex);

        query = formatNumber(number);
        if (query != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("TAG", "fetching user");
                    final User user = QueryUtils.fetchCallerData(USER_REQUEST_URL + query);
                    if (user != null) {
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_company_name.setText(user.getUserName());
                                tv_authoritative_name.setText(user.getAuthoritativeName());
                            }
                        });
                    }
                }
            }).start();
        }

        tv_call_type.setText(callType);
        tv_date_time_info.setText(callDate);
    }

    private String formatNumber(String number) {
        //+905433723255
        if (number.length() < 13) {
            Log.d("TAG", "Length of number is not long enough");
            return null;
        }
        String zero = number.substring(2, 3);
        String firstPart = "(" + number.substring(3, 6) + ")";
        String secondPart = number.substring(6, 9);
        String thirdPart = number.substring(9, 11);
        String fourthPart = number.substring(11, 13);
        String formattedNumber = zero
                + "+"
                + firstPart + "+"
                + secondPart + "+"
                + thirdPart + "+"
                + fourthPart;
        Log.d("TAG", "Formatted number: " + formattedNumber);
        return formattedNumber;
    }

    @NonNull
    @Override
    public Loader<User> onCreateLoader(int i, @Nullable Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<User> loader, User user) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<User> loader) {

    }

    private static class UserLoader extends AsyncTaskLoader<User> {
        private String requestUrl;
        private String query;

        public UserLoader(@NonNull Context context, String requestUrl, String query) {
            super(context);
            this.requestUrl = requestUrl;
            this.query = query;
        }

        @Nullable
        @Override
        public User loadInBackground() {
            User user = QueryUtils.fetchCallerData(requestUrl + query);
            return user;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }
}
