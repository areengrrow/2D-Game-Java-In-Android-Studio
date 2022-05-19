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
    Set<String> users = new HashSet<String>();
    final String userId = getUid();
    Handler handler = new Handler();

    private Handler mHandler;

    String databaseChild = "posts";
    boolean isAsking = false;
    String partner;
    Singleton singleton = Singleton.getInstance();
    Set<Long> selectTimes = new HashSet<Long>();
    boolean changeActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_player);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        usersGV = findViewById(R.id.users);

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
                composePost(partner, false, true, false, false);
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
                composePost("", false, false, false, false);
                stopRepeatingTask();
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
            startRepeatingTask();
            changeActivity = false;
            addPostEventListener(mPostReference);
        }
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
                // Get Post object and use the values to update the UI
                Map<String, Map<String, ?>> postMap =
                        (HashMap<String, Map<String, ?>>) dataSnapshot.getValue();
                for (String key : postMap.keySet()) {
                    Map<String, ?> dataMap = postMap.get(key);
                    String uid = (String) dataMap.get("uid");
                    String authorName = (String) dataMap.get("author");
                    String partnerName = (String) dataMap.get("partner");
                    long selectTime = (long)dataMap.get("time");
                    Date date = new Date();
                    if (selectTimes.contains(selectTime) ||
                            Objects.equals(singleton.username, authorName) ||
                            selectTime < date.getTime() - 5000)
                        continue;
                    selectTimes.add(selectTime);
                    boolean wait = (boolean) dataMap.get("wait");
                    if (!Objects.equals(singleton.username, authorName))
                        if (wait)
                            users.add(authorName);
                        else
                            users.remove(authorName);
                    else if (Objects.equals(partnerName, singleton.username)) {
                        boolean ask = (boolean) dataMap.get("ask");
                        boolean accept = (boolean) dataMap.get("accept");
                        if (ask)
                            buildAskDialog(uid, authorName, partnerName);
                        if (accept) {
                            stopRepeatingTask();
                            changeActivity = true;
                            singleton.left = userId;
                            singleton.leftName = partnerName;
                            singleton.right = uid;
                            singleton.rightName = authorName;
                            startActivity(new Intent(SelectPlayerActivity.this,
                                    GameActivityMultiple.class));
                        } else
                            isAsking = false;

                    }
                }
                ArrayList<String> usernames = new ArrayList<>();
                usernames.addAll(users);
                UserGVAdapter adapter = new UserGVAdapter(SelectPlayerActivity.this,
                        usernames);
                usersGV.setAdapter(adapter);
                findViewById(R.id.textView).setVisibility(usernames.isEmpty() ? View.VISIBLE :
                        View.INVISIBLE);
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
                    composePost(partnerName, false, false, true, false);
                    stopRepeatingTask();
                    changeActivity = true;
                    singleton.left = uid;
                    singleton.right = userId;
                    singleton.leftName = partnerName;
                    singleton.rightName = userName;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(SelectPlayerActivity.this,
                                    GameActivityMultiple.class));
                        }
                    }, 300);
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