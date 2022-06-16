package com.mobileclass.flywithme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mobileclass.flywithme.models.UserData;
import com.mobileclass.flywithme.models.PostSelect;
import com.mobileclass.flywithme.models.User;
import com.mobileclass.flywithme.utils.Singleton;
import com.mobileclass.flywithme.adapters.UserGVAdapter;
import com.mobileclass.flywithme.utils.SelectImageHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class SelectPlayerActivity extends AppCompatActivity {

    private static final String TAG = "NewPost";
    private static final String TAG_GET = "GetPost";
    GridView usersGV;
    private DatabaseReference mDatabase;
    private DatabaseReference mPostReference, usersDataReference;
    ArrayList<String> users = new ArrayList<>();
    final String userId = getUid();
    Handler handler = new Handler();

    private Handler mHandler;

    String databaseChild = "posts";
    boolean isAsking = false;
    String partner;
    Singleton singleton = Singleton.getInstance();
    Set<Long> selectTimes = new HashSet<Long>();
    boolean changeActivity = false, changeUsers = false, hasPicture = false;
    long globalTime;
    SelectImageHelper selectImageHelper;
    ImageView avatar;
    StorageReference storageReference;
    String uImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_player);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Date date = new Date();
        globalTime = date.getTime() - 5000;

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mPostReference = FirebaseDatabase.getInstance().getReference().child(databaseChild);
        usersDataReference = FirebaseDatabase.getInstance().getReference().child("users-data");
        addPostEventListener(mPostReference);

        mHandler = new Handler();
        startRepeatingTask();

        usersGV = findViewById(R.id.users);
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

        findViewById(R.id.log_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRepeatingTask();
                composePost("", false, false, false, false);
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(SelectPlayerActivity.this, SignInActivity.class));
            }
        });

        findViewById(R.id.rankBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRepeatingTask();
                composePost("", false, false, false, false);
                startActivity(new Intent(SelectPlayerActivity.this, RankingMultiple.class));
            }
        });

        avatar = findViewById(R.id.avatar);
        selectImageHelper = new SelectImageHelper(this, avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImageHelper.selectImageOption();
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference();
        TextView userName = findViewById(R.id.user_name);
        userName.setText(singleton.username);
        getUserDetails();
    }

    private void getUserDetails() {
        usersDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (hasPicture)
                    return;
                boolean userFound = false;
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    if (singleton.username.equals(data.getKey())) {
                        uImage = data.child("imageUrl").getValue(String.class);
                        userFound = true;
                        break;
                    }
                }
                if (userFound && uImage != null) {
                    Glide.with(getApplicationContext()).load(uImage).into(avatar);
                    hasPicture = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        selectImageHelper.handleResult(requestCode, resultCode, result);  // call this helper class method
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, final @NonNull String[] permissions, final @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        selectImageHelper.handleGrantedPermisson(requestCode, grantResults);   // call this helper class method
    }


    @Override
    protected void onResume() {
        super.onResume();
        Uri file = selectImageHelper.getURI_FOR_SELECTED_IMAGE();
        if (file != null) {
            Random random = new Random();
            final String s1 = String.valueOf(random.nextInt(263443));
            StorageReference sR = storageReference.child(s1);
            sR.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    if (taskSnapshot.getMetadata() != null) {
                        if (taskSnapshot.getMetadata().getReference() != null) {
                            Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String downloadUrl = uri.toString();

                                    UserData userData = new UserData();
                                    userData.setName(singleton.username);
                                    userData.setImageUrl(Objects.requireNonNull(downloadUrl));
                                    usersDataReference.child(singleton.username).setValue(userData);
                                    hasPicture = true;
                                }
                            });
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SelectPlayerActivity.this, "Database Error", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
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
                    changeUsers = false;
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