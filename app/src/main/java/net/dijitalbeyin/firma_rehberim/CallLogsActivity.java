package net.dijitalbeyin.firma_rehberim;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.method.LinkMovementMethod;
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
import android.widget.Toast;

import net.dijitalbeyin.firma_rehberim.adapters.CallLogCursorAdapter;
import net.dijitalbeyin.firma_rehberim.data.CompanyContract.CompanyEntry;
import net.dijitalbeyin.firma_rehberim.data.CompanyDbHelper;

import java.util.ArrayList;

public class CallLogsActivity extends AppCompatActivity {
    static int SERVICE_STOPPED = 0;
    static int SERVICE_RUNNING = 1;
    private final static String rootURL = "https://firmarehberim.com/";

    CompanyDbHelper dbHelper;
    SQLiteDatabase database;

    Toolbar toolbar;
    TextView tv_toolbar_title;
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

        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String userName = sharedPreferences.getString("username", "Kullanıcı adı bulunamadı");

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final String webpageLink = sharedPreferences.getString("webpageLink", "firmarehberim.com");

        tv_toolbar_title = toolbar.findViewById(R.id.tv_toolbar_title);
        tv_toolbar_title.setTextColor(Color.WHITE);
        tv_toolbar_title.setText(userName);
        tv_toolbar_title.setMovementMethod(LinkMovementMethod.getInstance());

        tv_toolbar_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webpageLink.equals("firmarehberim.com")) {
                    Toast.makeText(CallLogsActivity.this, "Kullanıcı bulunamadı", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(rootURL + webpageLink));
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
            }
        });

        dbHelper = new CompanyDbHelper(this);
        database = dbHelper.getWritableDatabase();
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
    protected void onResume() {
        super.onResume();
        refreshCallLogs();
    }

    @Override
    protected void onStart() {
        super.onStart();
        refreshCallLogs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.call_logs_menu, menu);

        final SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final int serviceState = sharedPreferences.getInt("serviceState", SERVICE_STOPPED);

        CheckBox enableCallerDetection = (CheckBox) menu.findItem(R.id.item_call_logs_caller_detection).getActionView();
        enableCallerDetection.setText("");
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
                    Log.d("TAG", "Stopping Service");
                    Intent intent = new Intent(CallLogsActivity.this, OverlayService.class);
                    stopService(intent);
                    editor.putInt("serviceState", SERVICE_STOPPED);
                } else {
                    Log.d("TAG", "Starting Service");
                    boolean permissionGranted = ContextCompat.checkSelfPermission(
                            getApplicationContext(),
                            Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
                    if (permissionGranted) {
                        Intent intent = new Intent(getApplicationContext(), OverlayService.class);
                        intent.putExtra("Sender", "Activity Button");
                        Log.d("TAG", "Sending intent");
                        startService(intent);
                        editor.putInt("serviceState", SERVICE_RUNNING);
                    } else {
                        ActivityCompat.requestPermissions(CallLogsActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
                    }
                }
                editor.apply();
            }
        });

        String userName = sharedPreferences.getString("username", "Kullanıcı adı bulunamadı");
        if (userName.equals("Kullanıcı adı bulunamadı")) {
            //User is not logged in.
            Log.d("TAG", "setting title as Giris yap");
            menu.findItem(R.id.item_call_logs_login).setTitle("Giriş yap");
        } else {
            // User is logged in.
            Log.d("TAG", "setting title as Cikis yap");
            menu.findItem(R.id.item_call_logs_login).setTitle("Çıkış yap");
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (item.getItemId()) {
            case R.id.item_call_logs_login:
                String userName = sharedPreferences.getString("username", "Kullanıcı adı bulunamadı");
                if (userName.equals("Kullanıcı adı bulunamadı")) {
                    // User is not logged in.
                    Intent loginIntent = new Intent(CallLogsActivity.this, SignInActivity.class);
                    startActivity(loginIntent);
                } else {
                    // User is logged in.
                    editor.putString("username", "Kullanıcı adı bulunamadı");
                    editor.apply();
                    Toast.makeText(getApplicationContext(), "Başarıyla çıkış yapıldı", Toast.LENGTH_SHORT).show();
                    recreate();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshCallLogs() {
        swipeRefreshLayout.setRefreshing(true);
        String[] projection = {CompanyEntry._ID,
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
        callLogCursorAdapter.swapCursor(cursor);
        swipeRefreshLayout.setRefreshing(false);
    }
}

