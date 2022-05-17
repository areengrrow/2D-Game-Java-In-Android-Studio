package com.mobileclass.flywithme.multiple;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mobileclass.flywithme.R;

import java.util.ArrayList;

public class UserGVAdapter extends ArrayAdapter<String> {
    public UserGVAdapter(@NonNull Context context, ArrayList<String> users) {
        super(context, 0, users);
    }

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
        return listitemView;
    }
}
