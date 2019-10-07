package net.dijitalbeyin.firma_rehberim.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

import net.dijitalbeyin.firma_rehberim.helper.QueryUtils;
import net.dijitalbeyin.firma_rehberim.R;
import net.dijitalbeyin.firma_rehberim.datamodel.User;


public class SignUpFragment extends Fragment implements View.OnClickListener {
    private final static String REQUEST_URL_PHONE_NUMBER = "https://firmarehberim.com/inc/telephone.php?no=";
    private final static String REQUEST_URL_EMAIL = "https://firmarehberim.com/inc/telephone.php?mail=";

    private EditText et_username;
    private EditText et_password;
    private TextView tv_launch_sign_in_activity;
    ProgressBar pb_creating_account;
    private Button btn_create_account;

    OnLaunchSignInClickListener onLaunchSignInClickListener;
    OnUserAuthorizedToSignUpListener onUserAuthorizedToSignUpListener;

    public void setOnLaunchSignInClickListener(OnLaunchSignInClickListener onLaunchSignInClickListener) {
        this.onLaunchSignInClickListener = onLaunchSignInClickListener;
    }

    public void setOnUserAuthorizedToSignUpListener(OnUserAuthorizedToSignUpListener onUserAuthorizedToSignUpListener) {
        this.onUserAuthorizedToSignUpListener = onUserAuthorizedToSignUpListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        Intent receivedIntent = getIntent();
//        if (receivedIntent != null) {
//            if (receivedIntent.getExtras() != null) {
//                boolean isVerified = receivedIntent
//                        .getBooleanExtra("isVerified", false);
//                receivedIntent.putExtra("isVerified", isVerified);
//            }
//            setResult(SignUpActivity.RESULT_OK, receivedIntent);
//        }

        et_username = view.findViewById(R.id.et_username);
        et_password = view.findViewById(R.id.et_password);
        tv_launch_sign_in_activity = view.findViewById(R.id.tv_launch_sign_in_activity);
        pb_creating_account = view.findViewById(R.id.pb_creating_account);
        btn_create_account = view.findViewById(R.id.btn_create_account);

        tv_launch_sign_in_activity.setOnClickListener(this);
        btn_create_account.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_launch_sign_in_activity:
                //Launch activity to create account
                onLaunchSignInClickListener.OnLaunchSignInClick();
                break;
            case R.id.btn_create_account:
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
                } else {
                    Toast.makeText(getContext(), "Lütfen geçerli bir e-posta adresi giriniz", Toast.LENGTH_SHORT).show();
//                    verifyUsingPhoneNumber(userName, password);
                }
                break;
        }
    }

    private void verifyUsingEmail(final String userName, final String password) {
        pb_creating_account.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                User user = QueryUtils.fetchUserData(REQUEST_URL_EMAIL + userName + "&pass=" + password);
                if (user != null) {
                    if (user.isVerified() && (user.getMatch() == 0)) {
                        //Authenticate user.
                        onUserAuthorizedToSignUpListener.onUserAuthorizedToSignUp(userName, password);
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
                                pb_creating_account.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Kullanıcı adı veya parola yanlış. Lütfen tekrar deneyiniz. Hesap oluşturabilmeniz için firmarehberim.com'da bir hesabınız olmalıdır", Toast.LENGTH_LONG).show();
                            pb_creating_account.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        }).start();
    }

//    private void verifyUsingPhoneNumber(final String userName, final String password) {
//        pb_creating_account.setVisibility(View.VISIBLE);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                //The case where user wanted to use phone number to login.
//                String formattedUserName = formatNumber(userName);
//                User user = QueryUtils.fetchUserData(REQUEST_URL_PHONE_NUMBER + formattedUserName + "&pass=" + password);
//                if (user != null) {
//                    if (user.isVerified() && (user.getMatch() == 0)) {
////                        Authenticate user.
//                        Intent intent = new Intent(SignInActivity.this, RadiosActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                        SharedPreferences sharedPreferences =
//                                PreferenceManager.getDefaultSharedPreferences(getContext());
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putString("username", user.getUserName());
//                        editor.putString("webpageLink", user.getUserWebpageLink());
//                        editor.apply();
//                        finish();
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
//                        pb_creating_account.setVisibility(View.INVISIBLE);
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

    public interface OnLaunchSignInClickListener {
        void OnLaunchSignInClick();
    }

    public interface OnUserAuthorizedToSignUpListener {
        void onUserAuthorizedToSignUp(String userName, String password);
    }
}
