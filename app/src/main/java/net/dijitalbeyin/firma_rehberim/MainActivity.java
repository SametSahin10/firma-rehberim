package net.dijitalbeyin.firma_rehberim;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    Button btn_go_categories;
    Button btn_go_cities;
    ImageButton ib_listenToRadio;
    ImageButton ib_watchTelevision;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) C0662R.layout.activity_main);
        this.ib_listenToRadio = (ImageButton) findViewById(C0662R.C0664id.ib_listenToRadio);
        this.ib_listenToRadio.setClipToOutline(true);
        this.ib_watchTelevision = (ImageButton) findViewById(C0662R.C0664id.ib_watchTelevison);
        this.ib_watchTelevision.setClipToOutline(true);
        this.ib_listenToRadio.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                MainActivity.this.startActivity(new Intent(MainActivity.this.getApplicationContext(), RadiosActivity.class));
            }
        });
    }
}
