package com.mobileclass.flywithme.multiple;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.mobileclass.flywithme.R;


public class BulletMultiple {

    int x, y, width, height;
    Bitmap bullet;

    BulletMultiple(Resources res) {

        bullet = BitmapFactory.decodeResource(res, R.drawable.bullet_red);

        width = bullet.getWidth();
        height = bullet.getHeight();

        width /= 4;
        height /= 4;

        width = (int) (width * GameViewMultiple.screenRatioX);
        height = (int) (height * GameViewMultiple.screenRatioY);

        bullet = Bitmap.createScaledBitmap(bullet, width, height, false);

    }

    Rect getCollisionShape () {
        return new Rect(x, y, x + width, y + height);
    }

}
