package net.dijitalbeyin.firma_rehberim.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import net.dijitalbeyin.firma_rehberim.CompanyCallLog;
import net.dijitalbeyin.firma_rehberim.R;

import java.util.ArrayList;

public class CallLogAdapter extends CursorAdapter {

    private Context context;
    private Cursor cursor;

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
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tv_company_name = view.findViewById(R.id.tv_company_name);
        TextView tv_authoritative_name = view.findViewById(R.id.tv_authoritative_name);
        TextView tv_call_type = view.findViewById(R.id.tv_call_type);
        TextView tv_call_date_info = view.findViewById(R.id.tv_call_date_info);

        int companyNameColumnIndex = cursor.getColumnIndex()
    }
}
