package com.mobileclass.flywithme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobileclass.flywithme.models.PostSelect;
import com.mobileclass.flywithme.models.User;
import com.mobileclass.flywithme.multiple.Singleton;
import com.mobileclass.flywithme.multiple.UserGVAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class SelectPlayerActivity extends AppCompatActivity {

    private static final String TAG = "NewPost";
    private static final String TAG_GET = "GetPost";
    GridView usersGV;
    private DatabaseReference mDatabase;
    private DatabaseReference mPostReference;
    ArrayList<String> users = new ArrayList<>();
    final String userId = getUid();
    Handler handler = new Handler();

    private Handler mHandler;

    String databaseChild = "posts";
    boolean isAsking = false;
    String partner;
    Singleton singleton = Singleton.getInstance();
    Set<Long> selectTimes = new HashSet<Long>();
    boolean changeActivity = false, changeUsers = false;
    long globalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_player);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        usersGV = findViewById(R.id.users);
        Date date = new Date();
        globalTime = date.getTime() - 5000;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mPostReference = FirebaseDatabase.getInstance().getReference().child(databaseChild);
        addPostEventListener(mPostReference);

        mHandler = new Handler();
        startRepeatingTask();

        usersGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView userTV = view.findViewById(R.id.user);
                partner = (String) userTV.getText();
                isAsking = true;
                view.setBackgroundColor(Color.GRAY);
                composePost(partner, true, true, false, false);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        view.setBackgroundColor(Color.LTGRAY);
                    }
                }, 200);
                Toast.makeText(SelectPlayerActivity.this,
                        "Waiting response of your partner.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.back5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRepeatingTask();
                composePost("", false, false, false, false);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (singleton.message != null) {
            Toast.makeText(SelectPlayerActivity.this, singleton.message, Toast.LENGTH_SHORT)
                    .show();
            singleton.message = null;
        }
        composePost("", true, false, false, false);
        startRepeatingTask();
        changeActivity = false;
        addPostEventListener(mPostReference);
    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            if (isAsking)
                return;
            composePost("", true, false, false, false);
            // 5 seconds by default, can be changed later
            int mInterval = 3000;
            mHandler.postDelayed(mStatusChecker, mInterval);
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    private void addPostEventListener(DatabaseReference mPostReference) {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (changeActivity)
                    return;
                Map<String, Map<String, ?>> postMap =
                        (HashMap<String, Map<String, ?>>) dataSnapshot.getValue();
                Date date = new Date();
                long currentTime = date.getTime() - 5000;
                globalTime = Math.max(globalTime, currentTime);
                for (String key : postMap.keySet()) {
                    if (currentTime < globalTime)
                        return;
                    Map<String, ?> dataMap = postMap.get(key);
                    long selectTime = (long) dataMap.get("time");
                    if (selectTime < currentTime || selectTimes.contains(selectTime))
                        continue;
                    selectTimes.add(selectTime);
                    String authorName = (String) dataMap.get("author");
                    if (Objects.equals(singleton.username, authorName))
                        continue;
                    String partnerName = (String) dataMap.get("partner");
                    if (Objects.equals(partnerName, singleton.username)) {
                        boolean ask = (boolean) dataMap.get("ask");
                        boolean accept = (boolean) dataMap.get("accept");
                        String uid = (String) dataMap.get("uid");
                        if (ask)
                            buildAskDialog(uid, authorName, partnerName);
                        else if (accept) {
                            stopRepeatingTask();
                            composePost(partnerName, false, false, false, true);
                            changeActivity = true;
                            singleton.left = userId;
                            singleton.leftName = partnerName;
                            singleton.right = uid;
                            singleton.rightName = authorName;
                            singleton.scoreLeft = 0;
                            singleton.scoreRight = 0;
                            startActivity(new Intent(SelectPlayerActivity.this,
                                    GameActivityMultiple.class));
                        } else
                            isAsking = false;
                    }
                    boolean isWait = (boolean) dataMap.get("wait");
                    if (isWait && !users.contains(authorName)) {
                        users.add(authorName);
                        changeUsers = true;
                    } else if (!isWait && users.contains(authorName)) {
                        users.remove(authorName);
                        changeUsers = true;
                    }
                }
                if (changeUsers) {
                    UserGVAdapter adapter = new UserGVAdapter(SelectPlayerActivity.this,
                            users);
                    usersGV.setAdapter(adapter);
                    findViewById(R.id.textView).setVisibility(users.isEmpty() ? View.VISIBLE :
                            View.INVISIBLE);
                }
                Log.w(TAG_GET, "select post");
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG_GET, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mPostReference.addValueEventListener(postListener);
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private void writeNewPost(String username, String partner, Boolean wait,
                              Boolean ask, Boolean accept, Boolean connect) {
        String key = mDatabase.child(databaseChild).push().getKey();
        Date date = new Date();
        PostSelect post = new PostSelect(userId, username, partner, wait, ask, accept, connect,
                date.getTime());
        Map<String, Object> postValues = post.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/" + databaseChild + "/" + key, postValues);
        mDatabase.updateChildren(childUpdates);
        Log.w(TAG, "select post");
    }

    private void buildAskDialog(String uid, String partnerName, String userName) {
        new AlertDialog.Builder(SelectPlayerActivity.this)
            .setTitle(partnerName + " challenge you. Do you join?")
            .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    stopRepeatingTask();
                    composePost(partnerName, false, false, true, false);
                    changeActivity = true;
                    singleton.left = uid;
                    singleton.right = userId;
                    singleton.leftName = partnerName;
                    singleton.rightName = userName;
                    singleton.scoreLeft = 0;
                    singleton.scoreRight = 0;
                    startActivity(new Intent(SelectPlayerActivity.this,
                            GameActivityMultiple.class));
                }
            })
            .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    composePost(partnerName, true, false, false, false);
                }
            }).show();
    }

    public void composePost(String partnerName, Boolean wait, Boolean ask, Boolean accept,
                            Boolean connect) {
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user == null) {
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(SelectPlayerActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            writeNewPost(user.username, partnerName, wait, ask, accept, connect);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

}