package com.example.testing_4.History;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testing_4.R;

import java.util.ArrayList;
import java.util.List;

public class History_Adapter extends RecyclerView.Adapter<History_ViewHolder> {

    Context context;
    List<Bill_Data> items;

    public History_Adapter(Context context , List<Bill_Data> items){
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public History_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new History_ViewHolder(LayoutInflater.from(context).inflate((R.layout.history_view),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull History_ViewHolder holder , int position) {
        holder.totalView.setText(items.get(position).getTotal());
        holder.locationView.setText(items.get(position).getLocation());
        holder.dateView.setText(items.get(position).getDate());
        holder.peopleView.setText(items.get(position).getFriend_list());
        holder.priceView.setText(items.get(position).getValue_list());
        holder.MeView.setText(items.get(position).getMyValue());

    }

    public void setHistoryList(ArrayList<Bill_Data> history_list) {
        this.items = history_list;
        notifyDataSetChanged(); // Notify the adapter that data has changed
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setFilteredList(Context context, List<Bill_Data> filteredList) {
        this.context = context;
        this.items = filteredList;
        notifyDataSetChanged();

    }
}
