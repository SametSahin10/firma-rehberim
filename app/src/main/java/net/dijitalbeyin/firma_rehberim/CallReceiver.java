package net.dijitalbeyin.firma_rehberim;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

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
    protected void onIncomingCallReceived(final Context context, String number, Date callStartTime) {
        Toast.makeText(context, "onIncomingCallReceived: " + number, Toast.LENGTH_SHORT).show();
        Log.d("TAG", "Incoming call received");
        query = formatNumber(number);
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = QueryUtils.fetchCallerData(USER_REQUEST_URL + query);
                if (user != null) {
                    Intent intent = new Intent(context, OverlayService.class);
                    intent.putExtra("username", user.getUserName());
                    context.startService(intent);
                }
            }
        }).start();
    }

    @Override
    protected void onIncomingCallAnswered(Context context, String number, Date callStartTime) {
        Toast.makeText(context, "onIncomingCallAnswered: " + number, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onIncomingCallEnded(Context context, String number, Date callStartTime, Date callEndTime) {
        Toast.makeText(context, "onIncomingCallEnded: " + number, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onOutgoingCallStarted(Context context, String number, Date callStartTime) {
        Toast.makeText(context, "onOutgoingCallStarted: " + number, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onOutGoingCallEnded(Context context, String number, Date callStartTime, Date callEndTime) {
        Toast.makeText(context, "onOutgoingCallEnded: " + number, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onMissedCall(Context context, String number, Date callStartTime) {
        Toast.makeText(context, "onMissedCall: " + number, Toast.LENGTH_SHORT).show();
    }

    private String formatNumber(String number) {
        //+905433723255
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
