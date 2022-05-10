package com.mobileclass.flywithme.multiple;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobileclass.flywithme.GameActivityMultiple;
import com.mobileclass.flywithme.MainActivity;
import com.mobileclass.flywithme.R;
import com.mobileclass.flywithme.models.Post;
import com.mobileclass.flywithme.models.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameViewMultiple extends SurfaceView implements Runnable {

    private static final String TAG = "NewPost";
    private static final String TAG_GET = "GetPost";
    private Thread thread;
    private boolean isPlaying, isGameOver = false;
    private int screenX, screenY, score = 0;
    public static float screenRatioX, screenRatioY;
    private Paint paint;
    private SharedPreferences prefs;
    private SoundPool soundPool;
    private List<BulletMultiple> bulletsLeft, bulletsRight;
    private int sound;
    private FlightMultiple flightLeft, flightRight;
    private GameActivityMultiple activity;
    private BackgroundMultiple background1, background2;

    private DatabaseReference mDatabase;
    private DatabaseReference mPostReference;

    public GameViewMultiple(GameActivityMultiple activity, int screenX, int screenY) {
        super(activity);

        this.activity = activity;

        prefs = activity.getSharedPreferences("game", Context.MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build();
        } else
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        sound = soundPool.load(activity, R.raw.shoot, 1);

        this.screenX = screenX;
        this.screenY = screenY;
        screenRatioX = 1920f / screenX;
        screenRatioY = 1080f / screenY;

        background1 = new BackgroundMultiple(screenX, screenY, getResources());
        background2 = new BackgroundMultiple(screenX, screenY, getResources());

        flightLeft = new FlightMultiple(this, screenX, screenY, getResources(), true);
        flightRight = new FlightMultiple(this, screenX, screenY, getResources(), false);

        bulletsLeft = new ArrayList<>();
        bulletsRight = new ArrayList<>();

        background2.x = screenX;

        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Initialize Database
        mPostReference = FirebaseDatabase.getInstance().getReference().child("posts");
        addPostEventListener(mPostReference);
    }


    private void addPostEventListener(DatabaseReference mPostReference) {
        // [START post_value_event_listener]
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Post post = dataSnapshot.getValue(Post.class);
                Log.w(TAG_GET, "post");
                // ..
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG_GET, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mPostReference.addValueEventListener(postListener);
        // [END post_value_event_listener]
    }


    @Override
    public void run() {

        while (isPlaying) {

            update ();
            draw ();
            sleep ();

        }

    }

    private void update () {

        background1.x -= 10 * screenRatioX;
        background2.x -= 10 * screenRatioX;

        if (background1.x + background1.background.getWidth() < 0) {
            background1.x = screenX;
        }

        if (background2.x + background2.background.getWidth() < 0) {
            background2.x = screenX;
        }

        if (flightLeft.isGoingUp)
            flightLeft.y -= 30 * screenRatioY;
        else
            flightLeft.y += 30 * screenRatioY;

        if (flightLeft.y < 0)
            flightLeft.y = 0;

        if (flightLeft.y >= screenY - flightLeft.height)
            flightLeft.y = screenY - flightLeft.height;

        flightRight.y += 30 * screenRatioY * (flightRight.isGoingUp ? -1 : 1);
        flightRight.y = Math.max(0, flightRight.y);
        flightRight.y = Math.min(screenY - flightRight.height, flightRight.y);

        List<BulletMultiple> trash = new ArrayList<>();

        for (BulletMultiple bullet : bulletsLeft) {
            if (bullet.x > screenX)
                trash.add(bullet);
            bullet.x += 50 * screenRatioX;
            if (Rect.intersects(flightLeft.getCollisionShape(),
                    bullet.getCollisionShape())) {
//                score++;
//                isGameOver = true;
            }
        }

        for (BulletMultiple bullet : bulletsRight) {
            if (bullet.x < 0)
                trash.add(bullet);
            bullet.x -= 50 * screenRatioX;
            if (Rect.intersects(flightRight.getCollisionShape(),
                    bullet.getCollisionShape())) {
//                score++;
//                isGameOver = true;
            }
        }

        for (BulletMultiple bullet : trash) {
            bulletsLeft.remove(bullet);
            bulletsRight.remove(bullet);
        }
    }

    private void draw () {

        if (getHolder().getSurface().isValid()) {

            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);

            canvas.drawText(score + "", screenX / 2f, 164, paint);

            if (isGameOver) {
                isPlaying = false;
                canvas.drawBitmap(flightLeft.getDead(), flightLeft.x, flightLeft.y, paint);
                canvas.drawBitmap(flightRight.getDead(), flightRight.x, flightRight.y, paint);
                getHolder().unlockCanvasAndPost(canvas);
                saveIfHighScore();
                waitBeforeExiting ();
                return;
            }

            canvas.drawBitmap(flightLeft.getFlight(true), flightLeft.x, flightLeft.y, paint);
            canvas.drawBitmap(flightRight.getFlight(false), flightRight.x, flightRight.y, paint);

            for (BulletMultiple bullet : bulletsLeft)
                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);
            for (BulletMultiple bullet : bulletsRight)
                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);

            getHolder().unlockCanvasAndPost(canvas);

        }

    }

    private void waitBeforeExiting() {

        try {
            Thread.sleep(3000);
            activity.startActivity(new Intent(activity, MainActivity.class));
            activity.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void saveIfHighScore() {

        if (prefs.getInt("highscore", 0) < score) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("highscore", score);
            editor.apply();
        }

    }

    private void sleep () {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume () {

        isPlaying = true;
        thread = new Thread(this);
        thread.start();

    }

    public void pause () {

        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getX() < screenX / 2) {
//                    flight.isGoingUp = true;
//                    flight.toShoot++;
                }
                final String userId = getUid();
                mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                // Get user value
                                User user = dataSnapshot.getValue(User.class);

                                if (user == null) {
                                    // User is null, error out
                                    Log.e(TAG, "User " + userId + " is unexpectedly null");
                                    Toast.makeText(getContext(),
                                            "Error: could not fetch user.",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    // Write new post
                                    writeNewPost(userId, user.username, Integer.toString(score), "test");
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                            }
                        });
                break;
            case MotionEvent.ACTION_UP:
//                flight.isGoingUp = false;
                if (event.getX() > screenX / 2)
//                    flight.toShoot++;
                break;
        }

        return true;
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void writeNewPost(String userId, String username, String title, String body) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username, title, body);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
        Log.w(TAG, "post");
    }

    public void newBulletLeft() {

        if (!prefs.getBoolean("isMute", false))
            soundPool.play(sound, 1, 1, 0, 0, 1);

        BulletMultiple bullet = new BulletMultiple(getResources(), true);
        bullet.x = flightLeft.x + flightLeft.width;
        bullet.y = flightLeft.y + (flightLeft.height / 2);
        bulletsLeft.add(bullet);
    }

    public void newBulletRight() {

        if (!prefs.getBoolean("isMute", false))
            soundPool.play(sound, 1, 1, 0, 0, 1);

        BulletMultiple bullet = new BulletMultiple(getResources(), false);
        bullet.x = flightRight.x + flightRight.width;
        bullet.y = flightRight.y + (flightRight.height / 2);
        bulletsRight.add(bullet);
    }
}
