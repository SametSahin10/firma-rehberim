package net.dijitalbeyin.firma_rehberim.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import net.dijitalbeyin.firma_rehberim.R;
import net.dijitalbeyin.firma_rehberim.data.CompanyContract.CompanyEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
        Log.d("TAG", "newView: ");
        return LayoutInflater.from(context).inflate(R.layout.item_call_log, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d("TAG", "bindView: ");
        TextView tv_company_name = view.findViewById(R.id.tv_company_name);
        TextView tv_authoritative_name = view.findViewById(R.id.tv_authoritative_name);
        TextView tv_call_status = view.findViewById(R.id.tv_call_status);
        TextView tv_call_date_info = view.findViewById(R.id.tv_call_date_info);

        int idColumnIndex = cursor.getColumnIndex(CompanyEntry._ID);
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
                tv_call_status.setTextColor(context.getResources().getColor(R.color.incoming_call_log_item_color));
                break;
            case CompanyEntry.CALL_STATUS_OUTGOING:
                callStatus = "Giden Arama";
                tv_call_status.setTextColor(context.getResources().getColor(R.color.outgoing_call_log_item_color));
                break;
            case CompanyEntry.CALL_STATUS_MISSED:
                callStatus = "Cevapsız Arama";
                tv_call_status.setTextColor(context.getResources().getColor(R.color.missed_call_log_item_color));
                break;
            default:
                callStatus = "Bilinmeyen arama tipi";
                tv_call_status.setTextColor(context.getResources().getColor(R.color.unknown_call_log_item_color));
                break;
        }

        if (isOnToday(dateInfo)) {
            String difference = calculateDifference(dateInfo);
            Log.d("TAG", "Difference: " + difference);
            tv_call_date_info.setText(difference);
        } else {
            tv_call_date_info.setText(dateInfo);
        }

        tv_company_name.setText(companyName);
        if (authoritativeName.equals("")) {
            tv_authoritative_name.setText("Yetkili adı bulunamadı");
        } else {
            tv_authoritative_name.setText(authoritativeName);
        }
        tv_call_status.setText(callStatus);
    }

    @Override
    public int getCount() {
        if (getCursor() == null) {
            return 0;
        }
        return super.getCount();
    }

    private String formatDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy HH.mm", Locale.getDefault());
        return dateFormat.format(date);
    }

    private String calculateDifference(String date) {
        Date currentDate = Calendar.getInstance().getTime();
        String formattedCurrentDate = formatDate(currentDate);
        String[] currentDateAndTime = formattedCurrentDate.split(" ");
        String[] dateAndTime = date.split(" ");
        String currentTime = currentDateAndTime[1];
        String time = dateAndTime[1]; //13.24
        Log.d("TAG", "currentTime: " + currentTime);
        Log.d("TAG", "time: " + time);
        String[] currentHourAndMin = currentTime.split("\\.");
        String[] hourAndMin = time.split("\\.");
        int currentHour = Integer.parseInt(currentHourAndMin[0]);
        int currentMin = Integer.parseInt(currentHourAndMin[1]);
        int hour = Integer.parseInt(hourAndMin[0]);
        int min = Integer.parseInt(hourAndMin[1]);
        int hourDifference = Math.abs(hour - currentHour);
        int minDifference = Math.abs(min - currentMin);
        Log.d("TAG", "currentHour: " + currentHour);
        Log.d("TAG", "currentMin: " + currentMin);
        Log.d("TAG", "hour: " + hour);
        Log.d("TAG", "min: " + min);
        Log.d("TAG", "hourDifference: " + hourDifference);
        Log.d("TAG", "minDifference: " + minDifference);
        if (hourDifference == 0) {
            if (minDifference == 0) {
                return "Kısa bir süre önce";
            }
            return minDifference + " dakika önce";
        } else {
            if (minDifference == 0) {
                return hourDifference + " saat önce";
            } else {
                return hourDifference + " saat " + minDifference + " dakika önce";
            }
        }
    }

    private boolean isOnToday(String date) {
        Date currentDate = Calendar.getInstance().getTime();
        String formattedCurrentDate = formatDate(currentDate);
        String[] currentDateAndTime = formattedCurrentDate.split(" ");
        String[] dateAndTime = date.split(" ");
        String dateOfToday = currentDateAndTime[0]; //02.03.2008
        String formattedDate = dateAndTime[0];
        if (dateOfToday.equals(formattedDate)) {
            return true;
        }
        return false;
    }
}
