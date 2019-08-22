package net.dijitalbeyin.firma_rehberim;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class CallReceiver extends PhoneCallReceiver {
    private static String USER_REQUEST_URL = "https://firmarehberim.com/inc/telephone.php?no=";
    private String query;
    private Context context;
    private Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d("TAG", "onReceived");
        this.context = context;

    }

    @Override
    protected void onIncomingCallReceived(final Context context, String number, Date callStartTime) {
        Toast.makeText(context, "Arayan numara: " + number, Toast.LENGTH_SHORT).show();
        Log.d("TAG", "Incoming call received");
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
                        extras.putString("userName", user.getUserName());
                        extras.putString("authoritativeName", user.getAuthoritativeName());
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
    protected void onIncomingCallEnded(Context context, String number, Date callStartTime, Date callEndTime) {
    }

    @Override
    protected void onOutgoingCallStarted(Context context, String number, Date callStartTime) {
    }

    @Override
    protected void onOutGoingCallEnded(Context context, String number, Date callStartTime, Date callEndTime) {
    }

    @Override
    protected void onMissedCall(Context context, String number, Date callStartTime) {
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
