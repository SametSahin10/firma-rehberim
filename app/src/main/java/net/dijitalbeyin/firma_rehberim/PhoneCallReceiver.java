package net.dijitalbeyin.firma_rehberim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.TelephonyManager;

import java.util.Date;

public abstract class PhoneCallReceiver extends BroadcastReceiver {
    private static int lastState = TelephonyManager.CALL_STATE_IDLE;
    private static Date callStartTime;
    private static boolean isIncoming;
    private static String savedNumber;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.NEW_OUTGOING_CALL")) {
            savedNumber = intent.getExtras().getString("android.intent.extra.PHONE_NUMBER");
        } else {
            String stateAsString = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            int stateAsInt = 0;
            if (stateAsString.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                stateAsInt = TelephonyManager.CALL_STATE_IDLE;
            } else if (stateAsString.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                stateAsInt = TelephonyManager.CALL_STATE_OFFHOOK;
            } else if (stateAsString.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                stateAsInt = TelephonyManager.CALL_STATE_RINGING;
            }
            onCallStatedChanged(context, stateAsInt, number);
        }
    }

    protected abstract void onIncomingCallReceived(Context context, String number, Date callStartTime);
    protected abstract void onIncomingCallAnswered(Context context, String number, Date callStartTime);
    protected abstract void onIncomingCallEnded(Context context, String number, Date callStartTime, Date callEndTime);
    protected abstract void onOutgoingCallStarted(Context context, String number, Date callStartTime);
    protected abstract void onOutGoingCallEnded(Context context, String number, Date callStartTime, Date callEndTime);
    protected abstract void onMissedCall(Context context, String number, Date callStartTime);

    public void onCallStatedChanged(Context context, int state, String number) {
        if (lastState == state) {
            return;
        }
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                callStartTime = new Date();
                savedNumber = number;
                onIncomingCallReceived(context, number, callStartTime);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (lastState != TelephonyManager.CALL_STATE_RINGING) {
                    isIncoming = false;
                    callStartTime = new Date();
                    onOutgoingCallStarted(context, number, callStartTime);
                } else {
                    isIncoming = true;
                    callStartTime = new Date();
                    onIncomingCallAnswered(context, number, callStartTime);
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                if (lastState == TelephonyManager.CALL_STATE_RINGING) {
                    onMissedCall(context, number, callStartTime);
                } else if (isIncoming) {
                    onIncomingCallEnded(context, number, callStartTime, new Date());
                } else {
                    onOutGoingCallEnded(context, number, callStartTime, new Date());
                }
                break;
        }
        lastState = state;
    }
}
