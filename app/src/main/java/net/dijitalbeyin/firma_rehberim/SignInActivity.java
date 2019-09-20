package net.dijitalbeyin.firma_rehberim;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String REQUEST_URL_PHONE_NUMBER = "https://firmarehberim.com/inc/telephone.php?no=";
    private final static String REQUEST_URL_EMAIL = "https://firmarehberim.com/inc/telephone.php?mail=";

    private EditText et_username;
    private EditText et_password;
    private ProgressBar pb_verifying_user;
    private Button btn_login;
    private TextView tv_launch_sign_up_activity;
    private TextView tv_skip_logging_in;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        firebaseAuth = FirebaseAuth.getInstance();

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        tv_launch_sign_up_activity = findViewById(R.id.tv_launch_sign_up_activity);
        pb_verifying_user = findViewById(R.id.pb_verifying_user);
        btn_login = findViewById(R.id.btn_login);
        tv_skip_logging_in = findViewById(R.id.tv_skip_logging_in);

        tv_launch_sign_up_activity.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        tv_skip_logging_in.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_launch_sign_up_activity:
                //Launch activity to create account
                Intent launchSignUpIntent = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(launchSignUpIntent);
                break;
            case R.id.btn_login:
                final String userName = et_username.getText().toString();
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(getApplicationContext(), "Telefon numarası veya e-posta giriniz", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String password = et_password.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Parola giriniz", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isEmail(userName)) {
                    verifyUsingEmail(userName, password);
                } else {
                    verifyUsingPhoneNumber(userName, password);
                }
                break;
            case R.id.tv_skip_logging_in:
                Intent skipLoggingInIntent = new Intent(SignInActivity.this, RadiosActivity.class);
                skipLoggingInIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(skipLoggingInIntent);
                finish();
                break;
        }
    }

    private void verifyUsingEmail(final String userName, final String password) {
        pb_verifying_user.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = QueryUtils.fetchUserData(REQUEST_URL_EMAIL + userName + "&pass=" + password);
                if (user != null) {
                    if (user.isVerified() && (user.getMatch() == 0)) {
                        //Authenticate user.
                        Intent intent = new Intent(SignInActivity.this, RadiosActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        SharedPreferences sharedPreferences =
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", user.getUserName());
                        editor.putString("webpageLink", user.getUserWebpageLink());
                        editor.apply();
                        finish();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Lütfen doğru parolayı girdiğinizden emin olun", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Kullanıcı adı veya parola yanlış. Lütfen tekrar deneyiniz", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pb_verifying_user.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();
    }

    private void verifyUsingPhoneNumber(final String userName, final String password) {
        pb_verifying_user.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //The case where user wanted to use phone number to login.
                String formattedUserName = formatNumber(userName);
                User user = QueryUtils.fetchUserData(REQUEST_URL_PHONE_NUMBER + formattedUserName + "&pass=" + password);
                if (user != null) {
                    if (user.isVerified() && (user.getMatch() == 0)) {
                        //Authenticate user.
                        Intent intent = new Intent(SignInActivity.this, RadiosActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        SharedPreferences sharedPreferences =
                                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", user.getUserName());
                        editor.putString("webpageLink", user.getUserWebpageLink());
                        editor.apply();
                        finish();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Lütfen doğru parolayı girdiğinizden emin olun", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Kullanıcı adı veya parola yanlış. Lütfen tekrar deneyiniz", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pb_verifying_user.setVisibility(View.INVISIBLE);
                    }
                });
            }
        }).start();
    }

    private void signIntoFirebase() {

    }

    private boolean isEmail(String userName) {
        if (userName.contains("@")) {
            return true;
        }
        return false;
    }

    private String formatNumber(String number) {
        number = number.replaceAll("\\s", "");
        if (!number.startsWith("+90")) {
            if (number.substring(0, 1).equals("0")) {
                //First case: 05433723255
                number = "+9" + number;
            } else {
                //Second case: 5433723255
                number = "+90" + number;
            }
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
