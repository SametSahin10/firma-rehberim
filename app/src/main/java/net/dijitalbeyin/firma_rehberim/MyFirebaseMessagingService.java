package net.dijitalbeyin.firma_rehberim;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private static final String ROOT_URL_LITERAL = "https://firmarehberim.com/";
    private static final String TOPIC_NUMBER_TRANSFER = "/topics/number_transfer";
    private static final String TOPIC_WHATSAPP_MESSAGE = "/topics/whatsapp_message";
    private static final String TOPIC_SMS = "/topics/sms";
    private static final String TOPIC_WEBPAGE = "/topics/view_webpage";

    FirebaseUser firebaseUser;

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "New Token: " + token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "Message received");
        if (remoteMessage.getData().size() > 0) {
            firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if (firebaseUser != null) {
                String userEmail = firebaseUser.getEmail();
                Map<String, String> dataPayload = remoteMessage.getData();
                String mailAddress = dataPayload.get("mail");
                if (mailAddress != null) {
                    if (mailAddress.equals(userEmail)) {
                        String phoneNumber = dataPayload.get("number");
                        String topic = remoteMessage.getFrom();
                        Log.d(TAG, "Topic: " + topic);
                        switch (topic) {
                            case TOPIC_NUMBER_TRANSFER:
                                handlePhoneNumber(phoneNumber);
                                break;
                            case TOPIC_WHATSAPP_MESSAGE:
                                String whatsappMesageBody = dataPayload.get("whatsapp_message_body");
                                sendWhatsappMessage(phoneNumber, whatsappMesageBody);
                                break;
                            case TOPIC_SMS:
                                String smsBody = dataPayload.get("sms_body");
                                sendSMS(phoneNumber, smsBody);
                                break;
                            case TOPIC_WEBPAGE:
                                String webpageUrl = dataPayload.get("webpage_url");
                                viewWebpage(webpageUrl);
                                break;
                            default:
                                Log.e(TAG, "Unknown topic");
                        }
                    }
                }
            }
        }
    }

    private void handlePhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.fromParts("tel", phoneNumber, null));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void sendWhatsappMessage(String phoneNumber, String messageBody) {
        PackageManager packageManager = getPackageManager();
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PackageInfo packageInfo = packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            intent.setPackage("com.whatsapp");
            String url = "https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + URLEncoder.encode(messageBody, "UTF-8");
            intent.setData(Uri.parse(url));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Bu özelliği kullanabilmek için Whatsapp telefonunuzda yüklü olmalıdır",
                    Toast.LENGTH_SHORT)
                    .show();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void sendSMS(String phoneNumber, String smsBody) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setType("vnd.android-dir/mms-sms");
        intent.putExtra("address", phoneNumber.trim());
        intent.putExtra("sms_body", smsBody);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void viewWebpage(String webpageUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse(ROOT_URL_LITERAL + webpageUrl));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
