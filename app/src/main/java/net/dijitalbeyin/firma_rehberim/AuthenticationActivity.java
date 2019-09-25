package net.dijitalbeyin.firma_rehberim;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.List;

public class AuthenticationActivity extends AppCompatActivity implements SignInFragment.OnLaunchSignUpClickListener,
                                                                         SignInFragment.OnUserAuthorizedToSignInListener,
                                                                         SignUpFragment.OnLaunchSignInClickListener,
                                                                         SignUpFragment.OnUserAuthorizedToSignUpListener {

    private final static String LOG_TAG = AuthenticationActivity.class.getSimpleName();

    SignInFragment signInFragment;
    SignUpFragment signUpFragment;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        firebaseAuth = FirebaseAuth.getInstance();

        signInFragment = new SignInFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, signInFragment).commit();
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof SignInFragment) {
            signInFragment.setOnLaunchSignUpClickListener(this);
            signInFragment.setOnUserAuthorizedToSignInListener(this);
        }
        if (fragment instanceof  SignUpFragment) {
            signUpFragment.setOnLaunchSignInClickListener(this);
            signUpFragment.setOnUserAuthorizedToSignUpListener(this);
        }
    }

    private void checkIfUserExists(final String email, final String password) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                        if (task.isSuccessful()) {
                            SignInMethodQueryResult result = task.getResult();
                            List<String> signInMethods = result.getSignInMethods();
                            if (signInMethods.isEmpty()) {
                                Log.d(LOG_TAG, "User does not have an account. Launch account creation process.");
                                signUpToFirebase(email, password);
                            } else {
                                Log.d(LOG_TAG, "User has an account. Simply login.");
                                signInToFirebase(email, password);
                            }
                        }
                    }
                });
    }

    @Override
    public void onUserAuthorizedToSignIn(String userName, String password) {
        checkIfUserExists(userName, password);
    }

    @Override
    public void onUserAuthorizedToSignUp(String userName, String password) {
        // Create new account on Firebase.
        Log.d(LOG_TAG, "User is authorized to sign up.");

    }

    private void signInToFirebase(String email, String password) {
        Log.d(LOG_TAG, "User is authorized to sign in.");
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "Signing into firebase successful");
                            Intent intent = new Intent(AuthenticationActivity.this, RadiosActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Log.d(LOG_TAG, "Signing into firebase failed");
                            Toast.makeText(getApplicationContext(), "Kullanıcı adı veya parola yanlış. Lütfen tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                        }
                        signInFragment.pb_verifying_user.setVisibility(View.GONE);
                    }
                });
    }

    private void signUpToFirebase(final String email, final String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "Signing up successful");
                            Toast.makeText(AuthenticationActivity.this,
                                    "Başarıyla hesap oluşturuldu",
                                    Toast.LENGTH_SHORT).show();
                            signInToFirebase(email, password);
                        } else {
                            Log.d(LOG_TAG, "Signing up failed");
                            Toast.makeText(getApplicationContext(), "Kullanıcı adı veya parola yanlış. Lütfen tekrar deneyiniz.\nHesap oluşturabilmeniz için firmarehberim.com'da bir hesabınız olmalıdır", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onLaunchSignUpClick() {
        signUpFragment = new SignUpFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, signUpFragment).commit();
    }

    @Override
    public void OnLaunchSignInClick() {
        signInFragment = new SignInFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, signInFragment).commit();
    }
}
