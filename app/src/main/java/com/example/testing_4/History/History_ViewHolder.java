package com.example.testing_4.History;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testing_4.R;

public class History_ViewHolder extends RecyclerView.ViewHolder {

    TextView dateView,locationView,totalView,peopleView,priceView , MeView;

    public History_ViewHolder(@NonNull View itemView) {

        super(itemView);
        totalView = itemView.findViewById(R.id.history_total);
        locationView = itemView.findViewById(R.id.history_location);
        dateView = itemView.findViewById(R.id.history_date);
        peopleView = itemView.findViewById(R.id.history_people);
        priceView = itemView.findViewById(R.id.history_price);
        MeView = itemView.findViewById(R.id.history_me);

    }
}
