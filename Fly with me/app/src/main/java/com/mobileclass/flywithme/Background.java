package com.mobileclass.flywithme;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.widget.ImageView;


public class Background {

    int x = 0, y = 0;
    Bitmap background;

    Background (int screenX, int screenY, Resources res) {
        ImageView img = R.drawable.assassinscreed_back;
        background = BitmapFactory.decodeResource(res, );
        background = Bitmap.createScaledBitmap(background, screenX, screenY, false);

    }

}
