package net.dijitalbeyin.firma_rehberim;

import android.content.Context;
import android.widget.Toast;

import java.util.Date;

public class CallReceiver extends PhoneCallReceiver {
    @Override
    protected void onIncomingCallReceived(Context context, String number, Date callStartTime) {
        Toast.makeText(context, "onIncomingCallReceived", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onIncomingCallAnswered(Context context, String number, Date callStartTime) {
        Toast.makeText(context, "onIncomingCallAnswered", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onIncomingCallEnded(Context context, String number, Date callStartTime, Date callEndTime) {
        Toast.makeText(context, "onIncomingCallEnded", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onOutgoingCallStarted(Context context, String number, Date callStartTime) {
        Toast.makeText(context, "onOutgoingCallStarted", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onOutGoingCallEnded(Context context, String number, Date callStartTime, Date callEndTime) {
        Toast.makeText(context, "onOutgoingCallEnded", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onMissedCall(Context context, String number, Date callStartTime) {
        Toast.makeText(context, "onMissedCall", Toast.LENGTH_SHORT).show();
    }
}
