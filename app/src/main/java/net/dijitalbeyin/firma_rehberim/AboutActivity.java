package net.dijitalbeyin.firma_rehberim;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class AboutActivity extends AppCompatActivity {

    Button btn_view_privacy_policy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        btn_view_privacy_policy = findViewById(R.id.btn_view_privacy_policy);
        btn_view_privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Go to privacy policy link
                String privacyPolicyUrl = "https://firmarehberim.com/bolumler/uyelik/privacy_policy.html";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(privacyPolicyUrl));
                startActivity(intent);
            }
        });
    }
}
