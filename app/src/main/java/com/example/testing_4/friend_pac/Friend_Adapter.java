package com.example.testing_4.friend_pac;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testing_4.R;

import java.util.ArrayList;
import java.util.List;

public class Friend_Adapter extends RecyclerView.Adapter<Friend_ViewHolder> {

    Context context;
    List<Class_friend> items;

    public Friend_Adapter(Context context, List<Class_friend> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public Friend_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Friend_ViewHolder(LayoutInflater.from(context).inflate(R.layout.friend_view,parent,false ));
    }

    @Override
    public void onBindViewHolder(@NonNull Friend_ViewHolder holder, int position) {
        holder.nameView.setText(items.get(position).getName());
        holder.ageView.setText(items.get(position).getAge());
        holder.phoneView.setText(items.get(position).getPhone_number());
        holder.emailView.setText(items.get(position).getEmail());

    }

    public void setFriendList(ArrayList<Class_friend> friendList) {
        this.items = friendList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {

        return items.size();
    }

    public void setFilteredList(Context context,List<Class_friend> filteredList){
        this.context = context;
        this.items = filteredList;
        notifyDataSetChanged();
    }

}
