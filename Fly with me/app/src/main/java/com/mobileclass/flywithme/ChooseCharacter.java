package com.mobileclass.flywithme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

public class ChooseCharacter extends AppCompatActivity {
    OpenClass data = new OpenClass();
    ImageView back, pilot, flash, my_character;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_character);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        back = findViewById(R.id.back);
        pilot = findViewById(R.id.pilot);
        flash = findViewById(R.id.flash);
        my_character = findViewById(R.id.my_character);
        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                startActivity(new Intent(ChooseCharacter.this, SingleMode.class));
                return false;
            }
        });

        pilot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                data.setCharacter(0);
                pilot.setImageResource(R.drawable.shoot1);
                flash.setImageResource(R.drawable.flash_not_choosed);
                my_character.setImageResource(R.drawable.shoot1);
                return false;
            }
        });

        flash.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                data.setCharacter(1);
                pilot.setImageResource(R.drawable.not_choosed);
                flash.setImageResource(R.drawable.shoot_red1);
                my_character.setImageResource(R.drawable.shoot_red1);
                return false;
            }
        });

    }
}