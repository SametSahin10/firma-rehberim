package net.dijitalbeyin.firma_rehberim;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import net.dijitalbeyin.firma_rehberim.data.CompanyContract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CallReceiver extends PhoneCallReceiver {
    private static String USER_REQUEST_URL = "https://firmarehberim.com/inc/telephone.php?no=";
    private String query;
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d("TAG", "onReceived");
        this.context = context;

    }

    @Override
    protected void onIncomingCallReceived(final Context context, String number, final Date callStartTime) {
        Toast.makeText(context, "Arayan numara: " + number, Toast.LENGTH_SHORT).show();
        Log.d("TAG", "Incoming call received number: " + number);
        query = formatNumber(number);
        if (query != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    User user = QueryUtils.fetchCallerData(USER_REQUEST_URL + query);
                    if (user != null) {
                        Log.d("TAG", "Username: " + user.getUserName());
                        Intent intent = new Intent(context, OverlayService.class);
                        Bundle extras = new Bundle();
                        extras.putBoolean("newEntryAvailable", false);
                        extras.putBoolean("showOverlay", true);
                        extras.putString("userWebpageLink", user.getUserWebpageLink());
                        extras.putString("userName", user.getUserName());
                        extras.putString("authoritativeName", user.getAuthoritativeName());
                        extras.putString("authoritativeWebpageLink", user.getAuthoritativeWebpageLink());
                        extras.putInt("callStatus", CompanyContract.CompanyEntry.CALL_STATUS_INCOMING);
                        String formattedDate = formatDate(callStartTime);
                        extras.putString("dateInfo", formattedDate);
                        intent.putExtras(extras);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(intent);
                        } else {
                            context.startService(intent);
                        }
                    }
                }
            }).start();
        }
    }

    @Override
    protected void onIncomingCallAnswered(Context context, String number, Date callStartTime) {
    }

    @Override
    protected void onIncomingCallEnded(final Context context, String number, final Date callStartTime, Date callEndTime) {
        Log.d("TAG", "Incoming call ended number: " + number);
        query = formatNumber(number);
        if (query != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    User user = QueryUtils.fetchCallerData(USER_REQUEST_URL + query);
                    if (user != null) {
                        Log.d("TAG", "Username: " + user.getUserName());
                        Intent intent = new Intent(context, OverlayService.class);
                        Bundle extras = new Bundle();
                        extras.putBoolean("newEntryAvailable", true);
                        extras.putBoolean("showOverlay", false);
                        extras.putString("userWebpageLink", user.getUserWebpageLink());
                        extras.putString("userName", user.getUserName());
                        extras.putString("authoritativeName", user.getAuthoritativeName());
                        extras.putString("authoritativeWebpageLink", user.getAuthoritativeWebpageLink());
                        extras.putInt("callStatus", CompanyContract.CompanyEntry.CALL_STATUS_INCOMING);
                        String formattedDate = formatDate(callStartTime);
                        extras.putString("dateInfo", formattedDate);
                        intent.putExtras(extras);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(intent);
                        } else {
                            context.startService(intent);
                        }
                    }
                }
            }).start();
        }
    }

    @Override
    protected void onOutgoingCallStarted(final Context context, String number, final Date callStartTime) {

    }

    @Override
    protected void onOutGoingCallEnded(final Context context, String number, final Date callStartTime, Date callEndTime) {
        Log.d("TAG", "Outgoing call ended number: " + number);
        query = formatNumber(number);
        if (query != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    User user = QueryUtils.fetchCallerData(USER_REQUEST_URL + query);
                    if (user != null) {
                        Log.d("TAG", "Username: " + user.getUserName());
                        Intent intent = new Intent(context, OverlayService.class);
                        Bundle extras = new Bundle();
                        extras.putBoolean("newEntryAvailable", true);
                        extras.putBoolean("showOverlay", false);
                        extras.putString("userWebpageLink", user.getUserWebpageLink());
                        extras.putString("userName", user.getUserName());
                        extras.putString("authoritativeName", user.getAuthoritativeName());
                        extras.putString("authoritativeWebpageLink", user.getAuthoritativeWebpageLink());
                        extras.putInt("callStatus", CompanyContract.CompanyEntry.CALL_STATUS_OUTGOING);
                        String formattedDate = formatDate(callStartTime);
                        extras.putString("dateInfo", formattedDate);
                        intent.putExtras(extras);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(intent);
                        } else {
                            context.startService(intent);
                        }
                    }
                }
            }).start();
        }
    }

    @Override
    protected void onMissedCall(final Context context, String number, final Date callStartTime) {
        Log.d("TAG", "onMissedCall number: " + number);
        query = formatNumber(number);
        if (query != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    User user = QueryUtils.fetchCallerData(USER_REQUEST_URL + query);
                    if (user != null) {
                        Log.d("TAG", "Username: " + user.getUserName());
                        Intent intent = new Intent(context, OverlayService.class);
                        Bundle extras = new Bundle();
                        extras.putString("userWebpageLink", user.getUserWebpageLink());
                        extras.putBoolean("newEntryAvailable", true);
                        extras.putBoolean("showOverlay", false);
                        extras.putString("userName", user.getUserName());
                        extras.putString("authoritativeName", user.getAuthoritativeName());
                        extras.putString("authoritativeWebpageLink", user.getAuthoritativeWebpageLink());
                        extras.putInt("callStatus", CompanyContract.CompanyEntry.CALL_STATUS_MISSED);
                        String formattedDate = formatDate(callStartTime);
                        extras.putString("dateInfo", formattedDate);
                        intent.putExtras(extras);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            context.startForegroundService(intent);
                        } else {
                            context.startService(intent);
                        }
                    }
                }
            }).start();
        }
    }

    private String formatNumber(String number) {
        //First format: +905433723255
        //Second format: 05433723255
        String formattedNumber = null;
        if (number != null) {
            if (!number.startsWith("+9")) {
                number = "+9" + number;
            }
            if (number.length() < 13) {
                Log.d("TAG", "Length of number is not long enough");
                return null;
            }

            String zero = number.substring(2, 3);
            String firstPart = "(" + number.substring(3, 6) + ")";
            String secondPart = number.substring(6, 9);
            String thirdPart = number.substring(9, 11);
            String fourthPart = number.substring(11, 13);
            formattedNumber = zero
                    + "+"
                    + firstPart + "+"
                    + secondPart + "+"
                    + thirdPart + "+"
                    + fourthPart;
            Log.d("TAG", "Formatted number: " + formattedNumber);
        }
        return formattedNumber;
    }

    private String formatDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyy HH.mm", Locale.getDefault());
        Log.d("TAG", "formattedDate: " + dateFormat.format(date));
        return dateFormat.format(date);
    }
}
