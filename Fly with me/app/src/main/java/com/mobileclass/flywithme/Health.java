package com.mobileclass.flywithme;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class Health {
    int x, y, width, height;
    Bitmap health;

    Health (Resources res) {

        health = BitmapFactory.decodeResource(res, R.drawable.heathheath);

        width = health.getWidth();
        height = health.getHeight();

        width /= 10;
        height /= 10;

        width = (int) (width * GameView.screenRatioX);
        height = (int) (height * GameView.screenRatioY);

        health = Bitmap.createScaledBitmap(health, width, height, false);

    }

    Rect getCollisionShape () {
        return new Rect(x, y, x + width, y + height);
    }
}
