package com.example.testing_4.friend_pac;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testing_4.R;

public class Friend_ViewHolder extends RecyclerView.ViewHolder {

    TextView nameView , ageView , phoneView , emailView;

    public Friend_ViewHolder(@NonNull View itemView) {
        super(itemView);
        nameView = itemView.findViewById(R.id.Type);
        ageView = itemView.findViewById(R.id.date);
        phoneView=itemView.findViewById(R.id.location);
        emailView = itemView.findViewById(R.id.fiend_list);

    }
}
