package com.mobileclass.flywithme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class Store extends AppCompatActivity {
    OpenClass data = new OpenClass();
    ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        findViewById(R.id.btnBack).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                String place = data.get_place();
                if (place == "theme")
                    startActivity(new Intent(Store.this, ChooseTheme.class));
                else
                    startActivity(new Intent(Store.this, ChooseCharacter.class));
                return false;
            }
        });

        findViewById(R.id.healthImage).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                data.addHealth(1);
                return false;
            }
        });

        findViewById(R.id.rocketImage).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                data.addRocket(1);
                return false;
            }
        });
    }
}