package net.dijitalbeyin.firma_rehberim;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "New Token: " + token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "Message received");
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "data payload: " + remoteMessage.getData());
            Map<String, String> email = remoteMessage.getData();
            final String mailAddress = email.get("mail");
            final String phoneNumber = email.get("number");
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "e-mail: " + mailAddress + "\nnumber: " + phoneNumber,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            });
        }
    }
}
