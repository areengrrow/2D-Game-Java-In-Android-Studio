package com.mobileclass.flywithme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class ChooseTheme extends AppCompatActivity {
    OpenClass data = new OpenClass();
    ImageView back, next, assassin, fantasy, palace, nature;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_choose_theme);
        back = findViewById(R.id.back);
        assassin = findViewById(R.id.pilot);
        fantasy = findViewById(R.id.fantastic_back);
        palace = findViewById(R.id.flash);
        nature = findViewById(R.id.nature_back);
        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                startActivity(new Intent(ChooseTheme.this, SingleMode.class));
                return false;
            }
        });

        assassin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                data.setTheme(1);
                assassin.setImageResource(R.drawable.assassinscreed_back);
                fantasy.setImageResource(R.drawable.fantasy_choose);
                palace.setImageResource(R.drawable.palace_choose);
                nature.setImageResource(R.drawable.nature_choose);
                return false;
            }
        });
        fantasy.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                data.setTheme(2);
                assassin.setImageResource(R.drawable.assasin_choose);
                fantasy.setImageResource(R.drawable.fantasyart_back);
                palace.setImageResource(R.drawable.palace_choose);
                nature.setImageResource(R.drawable.nature_choose);
                return false;
            }
        });
        palace.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                data.setTheme(3);
                assassin.setImageResource(R.drawable.assasin_choose);
                fantasy.setImageResource(R.drawable.fantasy_choose);
                palace.setImageResource(R.drawable.palace_back);
                nature.setImageResource(R.drawable.nature_choose);
                return false;
            }
        });
        nature.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                data.setTheme(4);
                assassin.setImageResource(R.drawable.assasin_choose);
                fantasy.setImageResource(R.drawable.fantasy_choose);
                palace.setImageResource(R.drawable.palace_choose);
                nature.setImageResource(R.drawable.nature_back);
                return false;
            }
        });

    }

}