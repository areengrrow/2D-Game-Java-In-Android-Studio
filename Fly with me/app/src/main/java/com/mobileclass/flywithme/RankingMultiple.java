package com.mobileclass.flywithme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobileclass.flywithme.adapters.RankMultipleRecyclerAdapter;
import com.mobileclass.flywithme.adapters.RankRecyclerAdapter;
import com.mobileclass.flywithme.models.UserData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RankingMultiple extends AppCompatActivity {

    RecyclerView rankRecyclerView;
    private DatabaseReference usersDataReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_multiple);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        findViewById(R.id.back7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rankRecyclerView = findViewById(R.id.rankRecycler);
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
                    Integer win = data.child("win").getValue(Integer.class);
                    userData.setWin(win == null ? 0 : win);
                    Integer lost = data.child("lost").getValue(Integer.class);
                    userData.setLost(lost == null ? 0 : lost);
                    userData.setTotal((win == null ? 0 : win) + (lost == null ? 0 : lost));
                    userDataArrayList.add(userData);
                }
                Collections.sort(userDataArrayList, new Comparator<UserData>() {
                    @Override
                    public int compare(UserData lhs, UserData rhs) {
                        return Integer.compare(rhs.getWin(), lhs.getWin());
                    }
                });
                RankMultipleRecyclerAdapter adapter = new RankMultipleRecyclerAdapter(RankingMultiple.this,
                        userDataArrayList);
                LinearLayoutManager layoutManager = new LinearLayoutManager(RankingMultiple.this, LinearLayoutManager.VERTICAL, false);

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