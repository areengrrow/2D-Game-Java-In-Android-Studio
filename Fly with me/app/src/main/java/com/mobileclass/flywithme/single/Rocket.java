package com.mobileclass.flywithme.single;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.mobileclass.flywithme.R;

public class Rocket {
    int x, y, width, height;
    Bitmap rocket;

    Rocket (Resources res) {

        rocket = BitmapFactory.decodeResource(res, R.drawable.rocket);

        width = rocket.getWidth();
        height = rocket.getHeight();

        width /= 10;
        height /= 10;

        width = (int) (width * GameView.screenRatioX);
        height = (int) (height * GameView.screenRatioY);

        rocket = Bitmap.createScaledBitmap(rocket, width, height, false);

    }

    Rect getCollisionShape () {
        return new Rect(x, y, x + width, y + height);
    }
}
