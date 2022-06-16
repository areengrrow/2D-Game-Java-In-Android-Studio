package com.mobileclass.flywithme.single;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import com.mobileclass.flywithme.utils.OpenClass;
import com.mobileclass.flywithme.R;


public class Flight {
    OpenClass my_flight = new OpenClass();
    int toShoot = 0;
    boolean isGoingUp = false;
    int x, y, width, height, wingCounter = 0, shootCounter = 1;
    Bitmap flight1, flight2, shoot1, shoot2, shoot3, shoot4, shoot5, dead;
    private GameView gameView;

    Flight (GameView gameView, int screenY, Resources res) {

        this.gameView = gameView;
        if(my_flight.getCharacter() == 0) {
            flight1 = BitmapFactory.decodeResource(res, R.drawable.fly1);
            flight2 = BitmapFactory.decodeResource(res, R.drawable.fly2);
            shoot1 = BitmapFactory.decodeResource(res, R.drawable.shoot1);
            dead = BitmapFactory.decodeResource(res, R.drawable.dead);
        }
        else {
            flight1 = BitmapFactory.decodeResource(res, R.drawable.fly_red1);
            flight2 = BitmapFactory.decodeResource(res, R.drawable.fly_red2);
            shoot1 = BitmapFactory.decodeResource(res, R.drawable.shoot_red);
            dead = BitmapFactory.decodeResource(res, R.drawable.dead_red);
        }

        width = flight1.getWidth();
        height = flight1.getHeight();

        width /= 4;
        height /= 4;

        width = (int) (width * GameView.screenRatioX);
        height = (int) (height * GameView.screenRatioY);

        flight1 = Bitmap.createScaledBitmap(flight1, width, height, false);
        flight2 = Bitmap.createScaledBitmap(flight2, width, height, false);

//        shoot1 = BitmapFactory.decodeResource(res, R.drawable.shoot_red1);
//        shoot2 = BitmapFactory.decodeResource(res, R.drawable.shoot2);
//        shoot3 = BitmapFactory.decodeResource(res, R.drawable.shoot3);
//        shoot4 = BitmapFactory.decodeResource(res, R.drawable.shoot4);
//        shoot5 = BitmapFactory.decodeResource(res, R.drawable.shoot5);

        shoot1 = Bitmap.createScaledBitmap(shoot1, width, height, false);
//        shoot2 = Bitmap.createScaledBitmap(shoot2, width, height, false);
//        shoot3 = Bitmap.createScaledBitmap(shoot3, width, height, false);
//        shoot4 = Bitmap.createScaledBitmap(shoot4, width, height, false);
//        shoot5 = Bitmap.createScaledBitmap(shoot5, width, height, false);

//        dead = BitmapFactory.decodeResource(res, R.drawable.dead_red);
        dead = Bitmap.createScaledBitmap(dead, width, height, false);

        y = screenY / 2;
        x = (int) (64 * GameView.screenRatioX);

    }

    Bitmap getFlight () {

        if (toShoot != 0) {

//            if (shootCounter == 1) {
//                shootCounter++;
//                return shoot1;
//            }

//            if (shootCounter == 2) {
//                shootCounter++;
//                return shoot2;
//            }
//
//            if (shootCounter == 3) {
//                shootCounter++;
//                return shoot3;
//            }
//
//            if (shootCounter == 4) {
//                shootCounter++;
//                return shoot4;
//            }

//            shootCounter = 1;
            toShoot--;
            gameView.newBullet();

            return shoot1;
        }

        if (wingCounter == 0) {
            wingCounter++;
            return flight1;
        }
        wingCounter--;

        return flight2;
    }

    Rect getCollisionShape () {
        return new Rect(x, y, x + width, y + height);
    }

    Bitmap getDead () {
        return dead;
    }

}
