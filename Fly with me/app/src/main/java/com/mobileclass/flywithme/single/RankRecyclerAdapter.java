package com.mobileclass.flywithme.single;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mobileclass.flywithme.R;
import com.mobileclass.flywithme.models.UserData;

import java.util.ArrayList;

public class RankRecyclerAdapter extends RecyclerView.Adapter<RankRecyclerAdapter.RankViewHolder> {

    private Context context;
    private ArrayList<UserData> userDataArrayList;

    public RankRecyclerAdapter(@NonNull Context context, ArrayList<UserData> userDataArrayList) {
        this.context = context;
        this.userDataArrayList = userDataArrayList;
    }

    @NonNull
    @Override
    public RankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rank_row, parent, false);
        return new RankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankViewHolder holder, int position) {
        UserData userData = userDataArrayList.get(position);
        holder.rank.setText(String.valueOf(position+1));
        holder.name.setText(userData.getName());
        holder.match.setText(String.valueOf(userData.getSingleMatch()));
        holder.highScore.setText(String.valueOf(userData.getSingleScore()));
        Glide.with(context).load(userData.getImageUrl()).into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return userDataArrayList.size();
    }

    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class RankViewHolder extends RecyclerView.ViewHolder {
        TextView rank, name, match, highScore;
        ImageView avatar;
        RecyclerView recyclerView;

        RankViewHolder(View itemView) {
            super(itemView);
            rank = itemView.findViewById(R.id.rankSingle);
            name = itemView.findViewById(R.id.nameSingle);
            match = itemView.findViewById(R.id.matchSingle);
            highScore = itemView.findViewById(R.id.scoreSingle);
            avatar = itemView.findViewById(R.id.avatarSingle);
            recyclerView = itemView.findViewById(R.id.rank);
        }
    }
}
