package com.mobileclass.flywithme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class GameMode extends AppCompatActivity {
    private boolean isMute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_mode);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        findViewById(R.id.single).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GameMode.this, GameActivity.class));
            }
        });

        findViewById(R.id.multi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GameMode.this, GameActivityMultiple.class));
            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GameMode.this, MainActivity2.class));
            }
        });



        TextView highScoreTxt = findViewById(R.id.highScoreTxt);

        final SharedPreferences prefs = getSharedPreferences("game", MODE_PRIVATE);
        highScoreTxt.setText("HighScore: " + prefs.getInt("highscore", 0));

        isMute = prefs.getBoolean("isMute", false);

        final ImageView volumeCtrl = findViewById(R.id.volumeCtrl);

        if (isMute)
            volumeCtrl.setImageResource(R.drawable.ic_volume_off_black_24dp);
        else
            volumeCtrl.setImageResource(R.drawable.ic_volume_up_black_24dp);

        volumeCtrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isMute = !isMute;
                if (isMute)
                    volumeCtrl.setImageResource(R.drawable.ic_volume_off_black_24dp);
                else
                    volumeCtrl.setImageResource(R.drawable.ic_volume_up_black_24dp);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("isMute", isMute);
                editor.apply();

            }
        });

    }
}