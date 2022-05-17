package com.mobileclass.flywithme.multiple;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.mobileclass.flywithme.R;


public class FlightMultiple {

    int toShoot = 0;
    boolean isGoingUp = false;
    int x, y, width, height;
    Bitmap flight1, shoot1, dead;
    private GameViewMultiple gameView;

    FlightMultiple(GameViewMultiple gameView, int screenX, int screenY, Resources res, boolean isLeft) {

        this.gameView = gameView;

        flight1 = BitmapFactory.decodeResource(res, isLeft ? R.drawable.fly1 : R.drawable.fly_red1_flip);

        width = flight1.getWidth();
        height = flight1.getHeight();

        width /= 4;
        height /= 4;

        width = (int) (width * GameViewMultiple.screenRatioX);
        height = (int) (height * GameViewMultiple.screenRatioY);

        flight1 = Bitmap.createScaledBitmap(flight1, width, height, false);

        shoot1 = BitmapFactory.decodeResource(res, isLeft ? R.drawable.shoot1 : R.drawable.shoot_red1_flip);

        shoot1 = Bitmap.createScaledBitmap(shoot1, width, height, false);

        dead = BitmapFactory.decodeResource(res, isLeft ? R.drawable.dead : R.drawable.dead_red_flip);
        dead = Bitmap.createScaledBitmap(dead, width, height, false);

        y = screenY / 2;
        x = (int) (isLeft ? GameViewMultiple.screenRatioX : screenX * GameViewMultiple.screenRatioX);

    }

    Bitmap getFlight (Boolean isLeft) {
        if (toShoot != 0) {
            toShoot--;
            if (isLeft)
                gameView.newBulletLeft();
            else
                gameView.newBulletRight();
            return shoot1;
        }
        return flight1;
    }

    Rect getCollisionShape () {
        return new Rect(x, y, x + width, y + height);
    }

    Bitmap getDead () {
        return dead;
    }

}
