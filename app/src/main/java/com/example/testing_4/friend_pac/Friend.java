package com.example.testing_4.friend_pac;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.testing_4.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;


public class Friend extends Fragment {

    private ArrayList<Class_friend> friendList = new ArrayList<>();
    private Friend_Adapter friend_adapter = new Friend_Adapter(getContext(),friendList);

    public Friend() {
        load_FriendList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        // 2. Floating Button
        FloatingActionButton fabButton = view.findViewById(R.id.add_friend_button);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Add_friend addFriendFragment = new Add_friend();
                Fragment fragment = addFriendFragment;
                addFriendFragment.setFriendList(friendList);
                FragmentTransaction transaction = getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.fragment_container,fragment)
                        .commit();


            }
        });

        RecyclerView friend_recyclerView = view.findViewById(R.id.friend_recycleView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(friend_recyclerView);
        friend_adapter = new Friend_Adapter(getContext(), friendList);
        friend_recyclerView.setLayoutManager(layoutManager);
        friend_recyclerView.setAdapter(friend_adapter);


        SearchView friend_searchView = view.findViewById(R.id.history_searchView);
        friend_searchView.clearFocus();
        friend_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filterList(s);
                return true;
            }
        });

        return view;
    }

    private void filterList(String text) {
        ArrayList<Class_friend> filteredList = new ArrayList<>();
        for (Class_friend item : friendList){
            if(item.getName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(item);
            }
        }
        if(filteredList.isEmpty()){
//            Toast.makeText(Friend.this,"No data found",Toast.LENGTH_SHORT).show();
        }else{
            friend_adapter.setFilteredList(getContext(),filteredList);
//            friend_adapter.notifyDataSetChanged();
        }
    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            if (position < friendList.size()) { // Check if the position is within the valid range
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Delete Friend");
                builder.setMessage("Are you sure to delete");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Class_friend friend = friendList.get(position);
                        String name = friend.getName();
                        friendList.remove(position); // Remove the item from the list
                        friend_adapter.setFriendList(friendList);
                        friend_adapter.notifyItemRemoved(position); // Notify the adapter about the removal

                        // Delete the friend from the database
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                        Query query = databaseReference.child("Friend").orderByChild("name").equalTo(name);

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        // Delete data
                                        String userKey_del = snapshot.getKey();
                                        DatabaseReference userRef_del = databaseReference.child("Friend").child(userKey_del);
                                        userRef_del.removeValue();
                                    }
                                } else {
                                    Log.d("UserData", "No data found.");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("UserData", "Error retrieving data: " + error.getMessage());
                            }
                        });

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Notify the adapter that the item was not deleted
                        friend_adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                });
                builder.show();
            }
        }
    };

    public void setFriendList(ArrayList<Class_friend> friendList) {
        this.friendList = friendList;
        if (friend_adapter != null) {
            friend_adapter.setFriendList(friendList);
            friend_adapter.notifyDataSetChanged();
        }
    }

    public void load_FriendList(){
        // 1. Read database and display it
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Friend");
        /// Notification !!!
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendList.clear();
                for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                    Class_friend friend = friendSnapshot.getValue(Class_friend.class);
                    friendList.add(friend);
                }
                // Data retrieval is complete, do something with the friendList here
                // For example, update your UI or perform other operations with the data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read data, handle the error here

            }

        });
    }

}