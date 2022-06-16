package com.mobileclass.flywithme.adapters;

import android.content.Context;
import android.graphics.Color;
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
import com.mobileclass.flywithme.utils.Singleton;

import java.util.ArrayList;

public class RankMultipleRecyclerAdapter extends RecyclerView.Adapter<RankMultipleRecyclerAdapter.RankViewHolder>  {

    private Context context;
    private ArrayList<UserData> userDataArrayList;
    Singleton singleton = Singleton.getInstance();

    public RankMultipleRecyclerAdapter(@NonNull Context context, ArrayList<UserData> userDataArrayList) {
        this.context = context;
        this.userDataArrayList = userDataArrayList;
    }

    @NonNull
    @Override
    public RankMultipleRecyclerAdapter.RankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rank_multiple_row, parent, false);
        return new RankMultipleRecyclerAdapter.RankViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankMultipleRecyclerAdapter.RankViewHolder holder, int position) {
        UserData userData = userDataArrayList.get(position);
        holder.rank.setText(String.valueOf(position+1));
        holder.name.setText(userData.getName());
        holder.match.setText(String.valueOf(userData.getTotal()));
        holder.win.setText(String.valueOf(userData.getWin()));
        holder.lost.setText(String.valueOf(userData.getLost()));
        if (!userData.getImageUrl().equals(""))
            Glide.with(context).load(userData.getImageUrl()).into(holder.avatar);
        if (singleton.username.equals(userData.getName()))
            holder.itemView.setBackgroundColor(Color.LTGRAY);
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
        TextView rank, name, match, win, lost;
        ImageView avatar;
        RecyclerView recyclerView;

        RankViewHolder(View itemView) {
            super(itemView);
            rank = itemView.findViewById(R.id.rankMultiple);
            name = itemView.findViewById(R.id.nameMultiple);
            match = itemView.findViewById(R.id.matchMultiple);
            win = itemView.findViewById(R.id.win);
            lost = itemView.findViewById(R.id.lost);
            avatar = itemView.findViewById(R.id.avatarMultiple);
            recyclerView = itemView.findViewById(R.id.rank);
        }
    }
}
