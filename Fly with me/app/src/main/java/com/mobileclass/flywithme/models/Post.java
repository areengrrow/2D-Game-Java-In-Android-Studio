package com.mobileclass.flywithme.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Post {
    public String uid;
    public String author;
    public long scoreLeft;
    public long scoreRight;
    public long time;
    public boolean bound;
    public boolean shoot;
    public boolean left;
    public boolean right;
    public boolean end;
    public boolean ready;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String author, long scoreLeft, long scoreRight, boolean bound,
                boolean shoot, boolean left, boolean right, boolean leftWin, long time, boolean ready) {
        this.uid = uid;
        this.author = author;
        this.scoreLeft = scoreLeft;
        this.scoreRight = scoreRight;
        this.bound = bound;
        this.shoot = shoot;
        this.left = left;
        this.right = right;
        this.end = leftWin;
        this.time = time;
        this.ready = ready;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("scoreLeft", scoreLeft);
        result.put("scoreRight", scoreRight);
        result.put("time", time);
        result.put("bound", bound);
        result.put("shoot", shoot);
        result.put("left", left);
        result.put("right", right);
        result.put("end", end);
        result.put("ready", ready);

        return result;
    }
}
