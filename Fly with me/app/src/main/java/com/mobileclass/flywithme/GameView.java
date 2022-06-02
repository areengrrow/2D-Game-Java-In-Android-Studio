package com.mobileclass.flywithme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean isPlaying, isGameOver = false;
    private int screenX, screenY, score = 0;
    public static float screenRatioX, screenRatioY;
    private Paint paint;
    private Bird[] birds;
    private SharedPreferences prefs;
    private Random random;
    private SoundPool soundPool;
    private List<Bullet> bullets;
    private int sound;
    private Flight flight;
    private GameActivity activity;
    private Background background1, background2;
    private static int extendX = 250, extendY = 100;


    public GameView(GameActivity activity, int screenX, int screenY) {
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
        background1 = new Background(screenX + extendX, screenY + extendY, getResources());
        background2 = new Background(screenX + extendX, screenY + extendY, getResources());
        flight = new Flight(this, screenY, getResources());
        bullets = new ArrayList<>();
        background2.x = screenX;
        paint = new Paint();
        paint.setTextSize(128);
        paint.setColor(Color.WHITE);
        birds = new Bird[4];
        for (int i = 0;i < 4;i++) {
            Bird bird = new Bird(getResources());
            birds[i] = bird;
        }
        random = new Random();
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

        if (background1.x + background1.background.getWidth() < extendX) {
            background1.x = screenX;
        }

        if (background2.x + background2.background.getWidth() < extendX) {
            background2.x = screenX;
        }

        if (flight.isGoingUp)
            flight.y -= 30 * screenRatioY;
        else
            flight.y += 30 * screenRatioY;

        if (flight.y < 0)
            flight.y = 0;

        if (flight.y >= screenY - flight.height)
            flight.y = screenY - flight.height;
        List<Bullet> trash = new ArrayList<>();
        for (Bullet bullet : bullets) {
            if (bullet.x > screenX)
                trash.add(bullet);
            bullet.x += 50 * screenRatioX;
            for (Bird bird : birds) {
                if (Rect.intersects(bird.getCollisionShape(),
                        bullet.getCollisionShape())) {
                    score++;
                    bird.x = -500;
                    bullet.x = screenX + 500;
                    bird.wasShot = true;
                }
            }
        }
        for (Bullet bullet : trash)
            bullets.remove(bullet);
        for (Bird bird : birds) {
            bird.x -= bird.speed;
            if (bird.x + bird.width < 0) {
                if (!bird.wasShot) {
//                    isGameOver = true;
                    return;
                }
                int bound = (int) (30 * screenRatioX);
                bird.speed = random.nextInt(bound);
                if (bird.speed < 10 * screenRatioX)
                    bird.speed = (int) (10 * screenRatioX);
                bird.x = screenX;
                bird.y = random.nextInt(screenY - bird.height);
                bird.wasShot = false;
            }
            if (Rect.intersects(bird.getCollisionShape(), flight.getCollisionShape())) {

//                isGameOver = true;
                return;
            }
        }
    }

    private void draw () {
        if (getHolder().getSurface().isValid()) {
            Canvas canvas = getHolder().lockCanvas();
            canvas.drawBitmap(background1.background, background1.x, background1.y, paint);
            canvas.drawBitmap(background2.background, background2.x, background2.y, paint);
            Bitmap pause;
            pause = BitmapFactory.decodeResource(getResources(), R.drawable.pause_btn);
            canvas.drawBitmap(pause, 2000, 0, paint);

            if (isPause) {
                Bitmap menu_home, menu_continue;
                menu_home = BitmapFactory.decodeResource(getResources(), R.drawable.menu_home);
                menu_continue = BitmapFactory.decodeResource(getResources(), R.drawable.menu_continue);
                canvas.drawBitmap(menu_continue, screenX / 3, screenY / 3, paint);
                canvas.drawBitmap(menu_home, screenX / 2, screenY / 3, paint);
            }

            for (Bird bird : birds)
                canvas.drawBitmap(bird.getBird(), bird.x, bird.y, paint);
            canvas.drawText(score + "", screenX / 2f, 164, paint);

            if (isGameOver) {
                isPlaying = false;
                canvas.drawBitmap(flight.getDead(), flight.x, flight.y, paint);
                getHolder().unlockCanvasAndPost(canvas);
                saveIfHighScore();
                waitBeforeExiting ();
                return;
            }

            canvas.drawBitmap(flight.getFlight(), flight.x, flight.y, paint);
            for (Bullet bullet : bullets)
                canvas.drawBitmap(bullet.bullet, bullet.x, bullet.y, paint);
            getHolder().unlockCanvasAndPost(canvas);
            if (isPause)
                pause();

        }
    }

    private void waitBeforeExiting() {
        try {
            sound = soundPool.load(activity, R.raw.go_up, 1);
            soundPool.play(sound, 1, 1, 0, 0, 2);
            Thread.sleep(300);
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
//        isPause = false;
        isPlaying = true;
        thread = new Thread(this);
        thread.start();

    }

    public void pause () {

        try {
//            isPause = true;
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
    boolean isPause = false;
//    private boolean isPause(){
//        return isPause;
//    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isPause && event.getX() >= screenX / 2 && event.getY() <= 200 ) {
                    isPause = true;
                    break;
//                    Canvas canvas = getHolder().lockCanvas();
//                    Bitmap menu_home, menu_continue;
//                    menu_home = BitmapFactory.decodeResource(getResources(), R.drawable.menu_home);
//                    menu_continue = BitmapFactory.decodeResource(getResources(), R.drawable.menu_continue);
//                    canvas.drawBitmap(menu_continue, screenX / 3, screenY / 3, paint);
//                    canvas.drawBitmap(menu_home, screenX / 2, screenY / 3, paint);
//                    getHolder().unlockCanvasAndPost(canvas);
//                    pause();

//                    if (event.getX() < screenX / 2 && event.getY() <= screenY / 2) {
//                        flight.isGoingUp = true;
//                        flight.toShoot++;
//                        resume();
//                    }
                }
                if (isPause)
                    if (event.getX() < screenX / 2) {
                        isPause = false;
                        resume();
                        break;
                    } else {
                        isGameOver = true;
                        resume();
                        break;
                    }

                if (isPause)
                    if (event.getX() >= screenX/3 && event.getX() <= screenX/2
                            && event.getY() >= screenY/3 && event.getY() <= screenY/3 + screenY/4) {
                        isPause = false;
                        resume();
                        break;
                    }
                    else if(event.getX() >= screenX/2 && event.getX() <= screenX/2 + screenX/8
                            && event.getY() >= screenY/3 && event.getY() <= screenY/3 + screenY/4)
                    {
                        isGameOver = true;
                        resume();
                        break;
                    }

//                if(event.getX() <= screenX / 2){
//
//                            && event.getY() >= screenY/3 &&event.getY() <= screenY/3+100 )
//                            resume();
//                            activity.startActivity(new Intent(activity, MainActivity.class));
//                            activity.finish();
//                            flight.isGoingUp = true;
//                            flight.toShoot++;
//                            resume();
//                }


//                if(event.getX() < screenX / 2) {
//                    if(!isPause())
//                    {
//                        flight.isGoingUp = true;
//                        flight.toShoot++;
//                        resume();
//                    }
//                }
                if (event.getX() >= screenX /2 )
                {
                    flight.toShoot++;
                }
                break;
            case MotionEvent.ACTION_UP:
                flight.isGoingUp = false;

                break;
        }

        return true;
    }

    public void newBullet() {

        if (!prefs.getBoolean("isMute", false))
            soundPool.play(sound, 1, 1, 0, 0, 2);
        if(!isPause) {
            Bullet bullet = new Bullet(getResources());
            bullet.x = flight.x + flight.width;
            bullet.y = flight.y + (flight.height / 2);
            bullets.add(bullet);
        }
    }

}