package com.mobileclass.flywithme.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class PostSelect {
    public String uid;
    public String author;
    public String type;
    public String partner;
    public Boolean wait;
    public Boolean ask;
    public Boolean accept;
    public Boolean connect;
    public long time;

    public PostSelect() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public PostSelect(String uid, String author, String partner, Boolean wait, Boolean ask,
                      Boolean accept, Boolean connect, long time) {
        this.uid = uid;
        this.author = author;
        this.partner = partner;
        this.wait = wait;
        this.ask = ask;
        this.accept = accept;
        this.time = time;
        this.type = "select";
        this.connect = connect;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("partner", partner);
        result.put("wait", wait);
        result.put("ask", ask);
        result.put("accept", accept);
        result.put("time", time);
        result.put("type", "select");
        result.put("connect", connect);

        return result;
    }
}
