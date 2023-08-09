package com.example.testing_4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.testing_4.History.Bill_Data;
import com.example.testing_4.History.History;
import com.example.testing_4.Home_pac.Home;
import com.example.testing_4.friend_pac.Class_friend;
import com.example.testing_4.friend_pac.Friend;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private Fragment home_;
    private ArrayList<String> keyList = new ArrayList<>();

    private ArrayList<Bill_Data> BillList = new ArrayList<>();
    private ArrayList<Class_friend> friendList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        home_ = new Home();

        load_FriendList();
        load_HistoryList();

        setFragment(home_);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                if(id == R.id.home){
                    setFragment(home_);
                    return true;
                }else if (id == R.id.friend) {

                    Friend friendFragment = new Friend();
                    friendFragment.setFriendList(friendList);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, friendFragment)
                            .addToBackStack(null)
                            .commit();

                    return true;
                } else if (id == R.id.history) {

                    History history_fragment = new History();
                    history_fragment.setKeyList(keyList);
                    history_fragment.setFriendList(friendList);
                    history_fragment.setBillList(BillList);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, history_fragment)
                            .addToBackStack(null)
                            .commit();
                    return true;
                }
                else{
                    return false;
                }
            }
        });

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
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

    public void load_HistoryList() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Bill");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                BillList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Add some logging for debugging

                    Bill_Data bill_data = dataSnapshot.getValue(Bill_Data.class);
                    if (bill_data != null) {
                        BillList.add(bill_data);
                        String uniqueKey = dataSnapshot.getKey();
                        keyList.add(uniqueKey);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserData", "Error retrieving data: " + error.getMessage());
            }
        });

    }


}