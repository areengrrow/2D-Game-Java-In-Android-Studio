package com.mobileclass.flywithme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TableLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobileclass.flywithme.models.UserData;
import com.mobileclass.flywithme.single.RankRecyclerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RankingSingle extends AppCompatActivity {

    RecyclerView rankRecyclerView;
    private DatabaseReference usersDataReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_single);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        findViewById(R.id.back3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rankRecyclerView = findViewById(R.id.rank);
        usersDataReference = FirebaseDatabase.getInstance().getReference().child("users-data");
        usersDataReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<UserData> userDataArrayList = new ArrayList<>();
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    UserData userData = new UserData();
                    userData.setName(data.child("name").getValue(String.class));
                    String imageUrl = data.child("imageUrl").getValue(String.class);
                    userData.setImageUrl(imageUrl == null ? "" : imageUrl);
                    Integer singleMatch = data.child("single-match").getValue(Integer.class);
                    userData.setSingleMatch(singleMatch == null ? 0 : singleMatch);
                    Integer singleScore = data.child("single-score").getValue(Integer.class);
                    userData.setSingleScore(singleScore == null ? 0 : singleScore);
                    userDataArrayList.add(userData);
                }
                Collections.sort(userDataArrayList, new Comparator<UserData>() {
                    @Override
                    public int compare(UserData lhs, UserData rhs) {
                        return Integer.compare(rhs.getSingleScore(), lhs.getSingleScore());
                    }
                });
                RankRecyclerAdapter adapter = new RankRecyclerAdapter(RankingSingle.this,
                        userDataArrayList);
                LinearLayoutManager layoutManager = new LinearLayoutManager(RankingSingle.this, LinearLayoutManager.VERTICAL, false);

                rankRecyclerView.setLayoutManager(layoutManager);
                rankRecyclerView.setAdapter(adapter);
                return;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}