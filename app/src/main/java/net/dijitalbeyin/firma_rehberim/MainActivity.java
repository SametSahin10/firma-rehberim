package net.dijitalbeyin.firma_rehberim;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    ImageButton ib_listenToRadio;
    ImageButton ib_watchTelevision;
    Button btn_go_cities;
    Button btn_go_categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ib_listenToRadio = findViewById(R.id.ib_listenToRadio);
        ib_listenToRadio.setClipToOutline(true);
        ib_watchTelevision = findViewById(R.id.ib_watchTelevison);
        ib_watchTelevision.setClipToOutline(true);

//        btn_go_cities = findViewById(R.id.btn_go_cities);
//        btn_go_cities.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), CitiesActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        btn_go_categories = findViewById(R.id.btn_go_categories);
//        btn_go_categories.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), CategoriesActivity.class);
//                startActivity(intent);
//            }
//        });
    }
}
