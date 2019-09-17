package net.dijitalbeyin.firma_rehberim;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.dijitalbeyin.firma_rehberim.adapters.CallLogCursorAdapter;
import net.dijitalbeyin.firma_rehberim.data.CompanyContract.CompanyEntry;
import net.dijitalbeyin.firma_rehberim.data.CompanyDbHelper;

import java.util.ArrayList;

import static net.dijitalbeyin.firma_rehberim.OverlayActivity.SERVICE_RUNNING;
import static net.dijitalbeyin.firma_rehberim.OverlayActivity.SERVICE_STOPPED;
import static net.dijitalbeyin.firma_rehberim.OverlayActivity.serviceState;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.call_logs_menu, menu);
        CheckBox enableCallerDetection = (CheckBox) menu.findItem(R.id.item_toggle_caller_detection).getActionView();
        enableCallerDetection.setText(R.string.arayan_firma_tespiti_call_logs);
        if (serviceState == SERVICE_STOPPED) {
            Log.d("TAG", "checking it false");
            enableCallerDetection.setChecked(false);
        } else if (serviceState == SERVICE_RUNNING) {
            Log.d("TAG", "checking it true");
            enableCallerDetection.setChecked(true);
        } else {
            Log.d("TAG", "Cannot determine service status");
        }

        enableCallerDetection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton checkbox, boolean isChecked) {
                if (!isChecked) {
                    if (serviceState == SERVICE_RUNNING) {
                        Log.d("TAG", "Stopping Service");
                        Intent intent = new Intent(CallLogsActivity.this, OverlayService.class);
                        stopService(intent);
                        serviceState = SERVICE_STOPPED;
                    }
                } else {
                    if (serviceState == SERVICE_STOPPED) {
                        Log.d("TAG", "Starting Service");
                        boolean permissionGranted = ContextCompat.checkSelfPermission(
                                getApplicationContext(),
                                Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
                        if (permissionGranted) {
                            Intent intent = new Intent(getApplicationContext(), OverlayService.class);
                            intent.putExtra("Sender", "Activity Button");
                            Log.d("TAG", "Sending intent");
                            startService(intent);
                            serviceState = SERVICE_RUNNING;
                        } else {
                            ActivityCompat.requestPermissions(CallLogsActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
                        }
                    }
                }
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.item_toggle_caller_detection:
//                if (serviceState == SERVICE_STOPPED) {
//                    Log.d("TAG", "Starting Service");
//                    boolean permissionGranted = ContextCompat.checkSelfPermission(
//                            getApplicationContext(),
//                            Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
//                    if (permissionGranted) {
//                        Intent intent = new Intent(getApplicationContext(), OverlayService.class);
//                        intent.putExtra("Sender", "Activity Button");
//                        startService(intent);
//                        serviceState = SERVICE_RUNNING;
//                        item.setChecked(true);
//                    } else {
//                        ActivityCompat.requestPermissions(CallLogsActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
//                    }
//                } else if (serviceState == SERVICE_RUNNING) {
//                    Log.d("TAG", "Stopping Service");
//                    Intent intent = new Intent(CallLogsActivity.this, OverlayService.class);
//                    stopService(intent);
//                    serviceState = SERVICE_STOPPED;
//                    item.setChecked(false);
//                }
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
        return super.onOptionsItemSelected(item);
    }
}

