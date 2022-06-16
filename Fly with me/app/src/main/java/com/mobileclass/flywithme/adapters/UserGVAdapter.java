package com.mobileclass.flywithme.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mobileclass.flywithme.R;

import java.util.ArrayList;

public class UserGVAdapter extends ArrayAdapter<String> {
    public UserGVAdapter(@NonNull Context context, ArrayList<String> users) {
        super(context, 0, users);
    }

    String uImage;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.card_item, parent, false);
        }
        String userName = getItem(position);
        TextView user = listitemView.findViewById(R.id.user);
        user.setText(userName);
        ImageView avatar = listitemView.findViewById(R.id.avatar_card);
        DatabaseReference mReference = FirebaseDatabase.getInstance().getReference().child("users-data");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean userFound = false;
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    if (userName.equals(data.getKey())) {
                        uImage = data.child("imageUrl").getValue(String.class);
                        userFound = true;
                        break;
                    }
                }
                if (userFound && uImage != null) {
                    Glide.with(getContext()).load(uImage).into(avatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return listitemView;
    }
}
