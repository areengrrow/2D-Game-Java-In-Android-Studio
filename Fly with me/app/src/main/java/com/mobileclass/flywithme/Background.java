package com.mobileclass.flywithme;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.widget.ImageView;
import com.mobileclass.flywithme.OpenClass;


public class Background {
    OpenClass theme = new OpenClass();
    int x = 0, y = 0;
    Bitmap background;

    Background (int screenX, int screenY, Resources res) {
        switch (theme.getTheme()){
            case 1:
                background = BitmapFactory.decodeResource(res, R.drawable.assassinscreed_back);
                background = Bitmap.createScaledBitmap(background, screenX, screenY, false);
                break;
            case 2:
                background = BitmapFactory.decodeResource(res, R.drawable.fantasyart_back);
                background = Bitmap.createScaledBitmap(background, screenX, screenY, false);
                break;
            case 3:
                background = BitmapFactory.decodeResource(res, R.drawable.palace_back);
                background = Bitmap.createScaledBitmap(background, screenX, screenY, false);
                break;
            case 4:
                background = BitmapFactory.decodeResource(res, R.drawable.nature_back);
                background = Bitmap.createScaledBitmap(background, screenX, screenY, false);
                break;
        }

    }

}
