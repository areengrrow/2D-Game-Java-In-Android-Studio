package com.mobileclass.flywithme.multiple;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.mobileclass.flywithme.R;


public class BackgroundMultiple {

    int x = 0, y = 0;
    Bitmap background;

    BackgroundMultiple(int screenX, int screenY, Resources res) {

        background = BitmapFactory.decodeResource(res, R.drawable.background_palace);
        background = Bitmap.createScaledBitmap(background, screenX, screenY, false);

    }

}
