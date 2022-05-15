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
import com.mobileclass.flywithme.R;
import com.mobileclass.flywithme.SelectPlayerActivity;
import com.mobileclass.flywithme.models.Post;
import com.mobileclass.flywithme.models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class GameViewMultiple extends SurfaceView implements Runnable {

    private static final String TAG = "NewPost";
    private static final String TAG_GET = "GetPost";
    private Thread thread;
    private boolean isPlaying, isGameOver = false;
    private int screenX, screenY;
    public static float screenRatioX, screenRatioY;
    private Paint paint;
    private Paint paintName;
    private SharedPreferences prefs;
    private SoundPool soundPool;
    private List<BulletMultiple> bulletsLeft, bulletsRight;
    private int sound;
    private FlightMultiple flightLeft, flightRight;
    private GameActivityMultiple activity;
    private BackgroundMultiple background1, background2;

    private DatabaseReference mDatabase;
    private DatabaseReference mPostReference;
    Singleton singleton = Singleton.getInstance();
    final String userId = getUid();
    final String databaseChild = "user-posts";
    private long scoreLeft = 0, scoreRight = 0;
    final boolean isServer = Objects.equals(singleton.left, userId);
    private boolean leftState = true, rightState = true;
    Set<Long> playTimes = new HashSet<Long>();
    Long shootTime, intersectLeft, intersectRight;

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
        paintName = new Paint();
        paintName.setTextSize(70);
        paintName.setColor(Color.BLACK);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        // Initialize Database
        mPostReference = FirebaseDatabase.getInstance().getReference().child(databaseChild);
        addPostEventListener(mPostReference);
    }


    private void addPostEventListener(DatabaseReference mPostReference) {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (!isPlaying)
//                    return;
                Map<String, Map<String, Map<String, ?>>> postMap =
                        (HashMap<String, Map<String, Map<String, ?>>>) dataSnapshot.getValue();
                for (String user : postMap.keySet()) {
                    boolean iL = Objects.equals(user, singleton.left);
                    if (!iL && !Objects.equals(user, singleton.right))
                        continue;
                    Map<String, Map<String, ?>> datumMap = postMap.get(user);
                    for (String key : datumMap.keySet()) {
                        Map<String, ?> dataMap = datumMap.get(key);
                        long time = (long) dataMap.get("time");
                        Date date = new Date();
                        if (playTimes.contains(time) || time < date.getTime() - 5000)
                            continue;
                        playTimes.add(time);
                        if (iL) {
                            scoreLeft = (long) dataMap.get("scoreLeft");
                            scoreRight = (long) dataMap.get("scoreRight");
                            flightLeft.isGoingUp = (boolean) dataMap.get("bound");
                            flightLeft.toShoot += (boolean) dataMap.get("shoot") ? 1 : 0;
                            leftState = (boolean) dataMap.get("left");
                            rightState = (boolean) dataMap.get("right");
                            isGameOver = (boolean) dataMap.get("end");
                        } else {
                            flightRight.isGoingUp = (boolean) dataMap.get("bound");
                            flightRight.toShoot += (boolean) dataMap.get("shoot") ? 1 : 0;
                        }
                    }
                }
                Log.w(TAG_GET, databaseChild);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
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
            if (isServer && Rect.intersects(flightRight.getCollisionShape(),
                    bullet.getCollisionShape())) {
                Date date = new Date();
                if (intersectRight == null || intersectRight < date.getTime() - 300) {
                    intersectRight = date.getTime();
                    composePost(scoreLeft + 1, scoreRight, false, false, true, false,
                            scoreLeft > 8);
                }
            }
        }

        for (BulletMultiple bullet : bulletsRight) {
            if (bullet.x < 0)
                trash.add(bullet);
            bullet.x -= 50 * screenRatioX;
            if (isServer && Rect.intersects(flightLeft.getCollisionShape(),
                    bullet.getCollisionShape())) {
                Date date = new Date();
                if (intersectLeft == null || intersectLeft < date.getTime() - 300) {
                    intersectLeft = date.getTime();
                    composePost(scoreLeft, scoreRight + 1, false, false, false, true,
                            scoreRight > 8);
                }
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

            canvas.drawText(scoreLeft + " - " + scoreRight, screenX / 2f - 64, 164, paint);
            canvas.drawText(singleton.leftName, flightLeft.x, flightLeft.y - 30, paintName);
            canvas.drawText(singleton.rightName, flightRight.x, flightRight.y - 30, paintName);

            canvas.drawBitmap(leftState ? flightLeft.getFlight(true) : flightLeft.getDead(),
                        flightLeft.x, flightLeft.y, paint);
            leftState = true;
            canvas.drawBitmap(rightState ? flightRight.getFlight(false) : flightRight.getDead(),
                        flightRight.x, flightRight.y, paint);
            rightState = true;
            if (isGameOver) {
                isPlaying = false;
                getHolder().unlockCanvasAndPost(canvas);
                waitBeforeExiting ();
                return;
            }

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
            activity.startActivity(new Intent(activity, SelectPlayerActivity.class));
            activity.finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
                boolean isShoot = (event.getX() < screenX / 2) != isServer;
                Date date = new Date();
                if (!isShoot)
                    composePost(scoreLeft, scoreRight, true, false,
                            true, true, false);
                else if (shootTime == null || shootTime < date.getTime() - 400) {
                    shootTime = date.getTime();
                    composePost(scoreLeft, scoreRight, false, true,
                            true, true, false);
                }
                break;
            case MotionEvent.ACTION_UP:
                composePost(scoreLeft, scoreRight, false, false,
                        true, true, false);
                break;
        }
        return true;
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void writeNewPost(String username, long scoreLeft, long scoreRight, boolean bound,
                              boolean shoot, boolean left, boolean right, boolean end) {
        String key = mDatabase.child("posts").push().getKey();
        Date date = new Date();
        Post post = new Post(userId, username, scoreLeft, scoreRight, bound, shoot, left, right,
                end, date.getTime());
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + databaseChild + "/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
        Log.w(TAG, databaseChild);
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
        bullet.x = flightRight.x;
        bullet.y = flightRight.y + (flightRight.height / 2) + 35;
        bulletsRight.add(bullet);
    }

    public void composePost(long scoreLeft, long scoreRight, boolean bound, boolean shoot, boolean left,
                            boolean right, boolean end) {
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user == null) {
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(activity, "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            writeNewPost(user.username, scoreLeft, scoreRight, bound, shoot,
                                    left, right, end);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }
}
