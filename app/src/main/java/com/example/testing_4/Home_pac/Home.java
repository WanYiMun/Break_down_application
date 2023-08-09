package com.example.testing_4.Home_pac;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.testing_4.History.Bill_Data;
import com.example.testing_4.History.History;
import com.example.testing_4.R;
import com.example.testing_4.friend_pac.Class_friend;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Home extends Fragment {

    private ArrayList<Class_friend> friendList = new ArrayList<>();
    private ImageView type_a_button , type_b_button , type_c_button,type_d_button , type_e_button;
    private ArrayList<String> keyList = new ArrayList<>();
    private ArrayList<Bill_Data> BillList = new ArrayList<>();

    public Home() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        load_FriendList();
        load_HistoryList();
        History history = new History();
        history.setBillList(BillList);
        history.setFriendList(friendList);
        history.setKeyList(keyList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);


        type_a_button = view.findViewById(R.id.type_A);
        type_a_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                type_A object_typeA = new type_A();
                Fragment fragment = object_typeA;
                object_typeA.setFriendList(friendList);

                FragmentTransaction transaction = getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.fragment_container,fragment)
                        .commit();
            }
        });

        type_b_button = view.findViewById(R.id.type_B);
        type_b_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type_B object_typeB = new type_B();
                Fragment fragment = object_typeB;
                object_typeB.setFriend_List(friendList);
                FragmentTransaction transaction = getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.fragment_container,fragment)
                        .commit();
            }
        });

        type_c_button = view.findViewById(R.id.type_C);
        type_c_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type_C object_typeC = new type_C();
                Fragment fragment = object_typeC;
                object_typeC.setFriend_List(friendList);
                FragmentTransaction transaction = getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.fragment_container,fragment)
                        .commit();
            }
        });

        type_d_button = view.findViewById(R.id.type_D);
        type_d_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type_D object_typeD = new type_D();
                Fragment fragment = object_typeD;
                object_typeD.setFriend_List(friendList);
                FragmentTransaction transaction = getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.fragment_container,fragment)
                        .commit();
            }
        });

        type_e_button = view.findViewById(R.id.type_E);
        type_e_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type_E object_typeE = new type_E();
                Fragment fragment = object_typeE;
                object_typeE.setFriend_List(friendList);
                FragmentTransaction transaction = getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.fragment_container,fragment)
                        .commit();
            }
        });


        return view;
    }

    public void load_FriendList(){
        // 1. Read database and display it
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Friend");
        /// Notification !!!
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendList.clear();
                for (DataSnapshot friendSnapshot : snapshot.getChildren()) {
                    Class_friend friend = friendSnapshot.getValue(Class_friend.class);
                    friendList.add(friend);
                }

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