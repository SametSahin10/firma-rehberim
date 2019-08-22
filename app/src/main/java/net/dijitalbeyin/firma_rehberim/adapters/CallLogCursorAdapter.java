package net.dijitalbeyin.firma_rehberim.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import net.dijitalbeyin.firma_rehberim.R;
import net.dijitalbeyin.firma_rehberim.data.CompanyContract.CompanyEntry;

public class CallLogCursorAdapter extends CursorAdapter {

    private Context context;
    private Cursor cursor;

    public CallLogCursorAdapter(Context context, Cursor cursor) {
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
        TextView tv_call_status = view.findViewById(R.id.tv_call_status);
        TextView tv_call_date_info = view.findViewById(R.id.tv_call_date_info);

        int companyNameColumnIndex = cursor.getColumnIndex(CompanyEntry.COLUMN_COMPANY_NAME);
        int authoritativeNameColumnIndex = cursor.getColumnIndex(CompanyEntry.COLUMN_AUTHORITATIVE_NAME);
        int callSatusColumnIndex = cursor.getColumnIndex(CompanyEntry.COLUMN_CALL_STATUS);
        int dateInfoColumnIndex = cursor.getColumnIndex(CompanyEntry.COLUMN_DATE_INFO);

        String companyName = cursor.getString(companyNameColumnIndex);
        String authoritativeName = cursor.getString(authoritativeNameColumnIndex);
        int callStatusAsInt = cursor.getInt(callSatusColumnIndex);
        String dateInfo = cursor.getString(dateInfoColumnIndex);

        String callStatus;
        switch (callStatusAsInt) {
            case CompanyEntry.CALL_STATUS_INCOMING:
                callStatus = "Gelen Arama";
                break;
            case CompanyEntry.CALL_STATUS_OUTGOING:
                callStatus = "Giden Arama";
                break;
            case CompanyEntry.CALL_STATUS_MISSED:
                callStatus = "Cevapsız Arama";
                break;
            default:
                callStatus = "Bilinmeyen arama tipi";
                break;
        }

        tv_company_name.setText(companyName);
        tv_authoritative_name.setText(authoritativeName);
        tv_call_status.setText(callStatus);
        tv_call_date_info.setText(dateInfo);
    }
}
