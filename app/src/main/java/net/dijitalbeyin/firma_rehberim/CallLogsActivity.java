package net.dijitalbeyin.firma_rehberim;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.dijitalbeyin.firma_rehberim.adapters.CallLogCursorAdapter;
import net.dijitalbeyin.firma_rehberim.data.CompanyContract.CompanyEntry;
import net.dijitalbeyin.firma_rehberim.data.CompanyDbHelper;

import java.util.ArrayList;

public class CallLogsActivity extends AppCompatActivity {
    SwipeRefreshLayout swipeRefreshLayout;
    ListView lw_call_log;
    ProgressBar pb_loading_call_logs;
    TextView tv_cannot_find_logs_text;
    CallLogCursorAdapter callLogCursorAdapter;
    ArrayList<CompanyCallLog> companyCallLogs;
    private String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_logs);

        CompanyDbHelper dbHelper = new CompanyDbHelper(this);
        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        final String[] projection = {CompanyEntry._ID,
                                CompanyEntry.COLUMN_WEBPAGE_LINK,
                                CompanyEntry.COLUMN_COMPANY_NAME,
                                CompanyEntry.COLUMN_AUTHORITATIVE_NAME,
                                CompanyEntry.COLUMN_CALL_STATUS,
                                CompanyEntry.COLUMN_DATE_INFO};
        Cursor cursor = database.query(CompanyEntry.TABLE_NAME,
                                        projection,
                                        null,
                                        null,
                                        null,
                                        null,
                                CompanyEntry._ID + " DESC",
                                        null);
        callLogCursorAdapter = new CallLogCursorAdapter(this, cursor);
        lw_call_log = findViewById(R.id.lw_call_log);
        tv_cannot_find_logs_text = findViewById(R.id.tv_cannot_find_logs_text);
        lw_call_log.setEmptyView(tv_cannot_find_logs_text);
        lw_call_log.setAdapter(callLogCursorAdapter);
        lw_call_log.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = callLogCursorAdapter.getCursor();
                if (cursor.moveToPosition(position)) {
                    int webpageLinkColumnIndex = cursor.getColumnIndex(CompanyEntry.COLUMN_WEBPAGE_LINK);
                    String webpageLink = cursor.getString(webpageLinkColumnIndex);
                    Uri webPageUri = Uri.parse("https://firmarehberim.com/" + webpageLink);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(webPageUri);
                    startActivity(intent);
                }
            }
        });
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Cursor cursor = database.query(CompanyEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        CompanyEntry._ID + " DESC",
                        null);
                callLogCursorAdapter.swapCursor(cursor);
                swipeRefreshLayout.setRefreshing(false);
            }
        });


//        boolean permissionGranted = ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED;
//        if (permissionGranted) {
//            pb_loading_call_logs = findViewById(R.id.pb_loading_call_logs);
//            tv_cannot_find_logs_text = findViewById(R.id.tv_cannot_find_logs_text);
//            companyCallLogs = new ArrayList<>();
//            lw_call_log = findViewById(R.id.lw_call_log);
//            callLogAdapter = new CallLogCursorAdapter(this, R.layout.item_call_log, companyCallLogs);
//            lw_call_log.setAdapter(callLogAdapter);
//            Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
//            if (cursor != null) {
//                Log.d("TAG", "Cursor is not null");
//                int numberColumnIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER);
//                int callTypeColumnIndex = cursor.getColumnIndex(CallLog.Calls.TYPE);
//                int dateInfoColumnIndex = cursor.getColumnIndex(CallLog.Calls.DATE);
//                while (cursor.moveToNext()) {
//                    String number = cursor.getString(numberColumnIndex);
//                    final String callType = cursor.getString(callTypeColumnIndex);
//                    final String dateInfo = cursor.getString(dateInfoColumnIndex);
//                    Log.d("TAG", "number: " + number);
//                    Log.d("TAG", "callType: " + callType);
//                    Log.d("TAG", "dateInfo: " + dateInfo);
//                    query = formatNumber(number);
//                    if (query != null) {
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                User user = QueryUtils.fetchCallerData(USER_REQUEST_URL + query);
//                                if (user != null) {
//                                    String companyName = user.getUserName();
//                                    String authoritativeName = user.getAuthoritativeName();
//                                    CompanyCallLog callLog = new CompanyCallLog(
//                                                                    companyName,
//                                                                    authoritativeName,
//                                                                    callType,
//                                                                    dateInfo);
//                                    companyCallLogs.add(callLog);
//                                    runOnUiThread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            callLogAdapter.notifyDataSetChanged();
//                                        }
//                                    });
//                                    try {
//                                        Thread.sleep(1000);
//                                    } catch (InterruptedException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
//                        }).start();
//
//                    }
//                }
//                if (companyCallLogs.isEmpty()) {
//                    tv_cannot_find_logs_text.setVisibility(View.VISIBLE);
//                } else {
//                    pb_loading_call_logs.setVisibility(View.INVISIBLE);
//                }
//
//            } else {
//                Log.d("TAG", "Cursor is null");
//            }
//        } else {
//            Log.d("TAG", "permission not granted");
//            ActivityCompat.requestPermissions(CallLogsActivity.this, new String[]{Manifest.permission.READ_CALL_LOG}, 0);
//        }
    }
}

