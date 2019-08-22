package net.dijitalbeyin.firma_rehberim;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.dijitalbeyin.firma_rehberim.adapters.CallLogCursorAdapter;
import net.dijitalbeyin.firma_rehberim.data.CompanyContract.CompanyEntry;
import net.dijitalbeyin.firma_rehberim.data.CompanyDbHelper;

import java.util.ArrayList;

public class CallLogsActivity extends AppCompatActivity {
    private static String USER_REQUEST_URL = "https://firmarehberim.com/inc/telephone.php?no=";

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
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String[] projection = {CompanyEntry._ID,
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
}

