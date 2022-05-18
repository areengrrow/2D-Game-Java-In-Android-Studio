package com.mobileclass.flywithme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class ChooseTheme extends AppCompatActivity {

    ImageView back, next, assassin, fantasy, palace, nature;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_choose_theme);
        back = findViewById(R.id.back);
        assassin = findViewById(R.id.asasin_back);
        fantasy = findViewById(R.id.fantastic_back);
        palace = findViewById(R.id.palace_back);
        nature = findViewById(R.id.nature_back);
        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                startActivity(new Intent(ChooseTheme.this, MainActivity2.class));
                return false;
            }
        });

        assassin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                return false;
            }
        });

    }

}