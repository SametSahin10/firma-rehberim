package net.dijitalbeyin.firma_rehberim;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import net.dijitalbeyin.firma_rehberim.adapters.CallLogAdapter;

import java.util.ArrayList;
import java.util.List;

public class CallLogsActivity extends AppCompatActivity {

    ListView lw_call_log;
    CallLogAdapter callLogAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_logs);

        boolean permissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED;
        if (permissionGranted) {
            Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
            lw_call_log = findViewById(R.id.lw_call_log);
            callLogAdapter = new CallLogAdapter(this, cursor);
            lw_call_log.setAdapter(callLogAdapter);
        } else {
            Log.d("TAG", "permission not granted");
            ActivityCompat.requestPermissions(CallLogsActivity.this, new String[]{Manifest.permission.READ_CALL_LOG}, 0);
        }
    }
}