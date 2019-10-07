package net.dijitalbeyin.firma_rehberim.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import net.dijitalbeyin.firma_rehberim.helper.QueryUtils;
import net.dijitalbeyin.firma_rehberim.R;
import net.dijitalbeyin.firma_rehberim.activities.RadiosActivity;
import net.dijitalbeyin.firma_rehberim.datamodel.User;

public class SignInFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = SignInFragment.class.getSimpleName();
    private final static String REQUEST_URL_PHONE_NUMBER = "https://firmarehberim.com/inc/telephone.php?no=";
    private final static String REQUEST_URL_EMAIL = "https://firmarehberim.com/inc/telephone.php?mail=";

    private EditText et_username;
    private EditText et_password;
    public ProgressBar pb_verifying_user;
    private Button btn_login;
    private TextView tv_launch_sign_up_activity;
    private TextView tv_skip_logging_in;

    OnLaunchSignUpClickListener onLaunchSignUpClickListener;
    OnUserAuthorizedToSignInListener onUserAuthorizedToSignInListener;

    public void setOnLaunchSignUpClickListener(OnLaunchSignUpClickListener onLaunchSignUpClickListener) {
        this.onLaunchSignUpClickListener = onLaunchSignUpClickListener;
    }

    public void setOnUserAuthorizedToSignInListener(OnUserAuthorizedToSignInListener onUserAuthorizedToSignInListener) {
        this.onUserAuthorizedToSignInListener = onUserAuthorizedToSignInListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        et_username = view.findViewById(R.id.et_username);
        et_password = view.findViewById(R.id.et_password);
        tv_launch_sign_up_activity = view.findViewById(R.id.tv_launch_sign_up_activity);
        pb_verifying_user = view.findViewById(R.id.pb_verifying_user);
        btn_login = view.findViewById(R.id.btn_login);
        tv_skip_logging_in = view.findViewById(R.id.tv_skip_logging_in);

        tv_launch_sign_up_activity.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        tv_skip_logging_in.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_launch_sign_up_activity:
                //Launch activity to create account
                onLaunchSignUpClickListener.onLaunchSignUpClick();
                break;
            case R.id.btn_login:
                final String userName = et_username.getText().toString();
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(getContext(), "Telefon numarası veya e-posta giriniz", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String password = et_password.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getContext(), "Parola giriniz", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isEmail(userName)) {
                    verifyUsingEmail(userName, password);
                }
                else {
//                    verifyUsingPhoneNumber(userName, password);
                    Toast.makeText(getContext(), "Lütfen geçerli bir e-posta adresi giriniz", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tv_skip_logging_in:
                Intent skipLoggingInIntent = new Intent(getContext(), RadiosActivity.class);
                skipLoggingInIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(skipLoggingInIntent);
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
                        //Authenticate user through Firebase.
                        onUserAuthorizedToSignInListener.onUserAuthorizedToSignIn(userName, password);
                        SharedPreferences sharedPreferences =
                                PreferenceManager.getDefaultSharedPreferences(getContext());
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", user.getUserName());
                        editor.putString("webpageLink", user.getUserWebpageLink());
                        editor.apply();
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Lütfen doğru parolayı girdiğinizden emin olun", Toast.LENGTH_SHORT).show();
                                pb_verifying_user.setVisibility(View.GONE);
                            }
                        });
                    }
                } else {
                        getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Kullanıcı adı veya parola yanlış. Lütfen tekrar deneyiniz", Toast.LENGTH_SHORT).show();
                            pb_verifying_user.setVisibility(View.GONE);
                        }
                    });
                }
            }
        }).start();
    }

//    private void verifyUsingPhoneNumber(final String userName, final String password) {
//        pb_verifying_user.setVisibility(View.VISIBLE);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //The case where user wanted to use phone number to login.
//                String formattedUserName = formatNumber(userName);
//                User user = QueryUtils.fetchUserData(REQUEST_URL_PHONE_NUMBER + formattedUserName + "&pass=" + password);
//                if (user != null) {
//                    if (user.isVerified() && (user.getMatch() == 0)) {
//                        //Authenticate user.
//                        Intent intent = new Intent(SignInActivity.this, RadiosActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                        SharedPreferences sharedPreferences =
//                                PreferenceManager.getDefaultSharedPreferences(getContext());
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString("username", user.getUserName());
//                        editor.putString("webpageLink", user.getUserWebpageLink());
//                        editor.apply();
//                    } else {
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getContext(), "Lütfen doğru parolayı girdiğinizden emin olun", Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                } else {
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getContext(), "Kullanıcı adı veya parola yanlış. Lütfen tekrar deneyiniz", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        pb_verifying_user.setVisibility(View.INVISIBLE);
//                    }
//                });
//            }
//        }).start();
//    }

    private boolean isEmail(String userName) {
        if (userName.contains("@")) {
            return true;
        }
        return false;
    }

//    private String formatNumber(String number) {
//        number = number.replaceAll("\\s", "");
//        if (!number.startsWith("+90")) {
//            if (number.substring(0, 1).equals("0")) {
//                //First case: 05433723255
//                number = "+9" + number;
//            } else {
//                //Second case: 5433723255
//                number = "+90" + number;
//            }
//        }
//        if (number.length() < 13) {
//            Log.d("TAG", "Length of number is not long enough");
//            return null;
//        }
//        String zero = number.substring(2, 3);
//        String firstPart = "(" + number.substring(3, 6) + ")";
//        String secondPart = number.substring(6, 9);
//        String thirdPart = number.substring(9, 11);
//        String fourthPart = number.substring(11, 13);
//        String formattedNumber = zero
//                + "+"
//                + firstPart + "+"
//                + secondPart + "+"
//                + thirdPart + "+"
//                + fourthPart;
//        Log.d("TAG", "Formatted number: " + formattedNumber);
//        return formattedNumber;
//    }

    public interface OnLaunchSignUpClickListener {
        void onLaunchSignUpClick();
    }

    public interface OnUserAuthorizedToSignInListener {
        void onUserAuthorizedToSignIn(String userName, String password);
    }
}
