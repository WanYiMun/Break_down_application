package com.example.testing_4.friend_pac;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.testing_4.Home_pac.Home;
import com.example.testing_4.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Add_friend extends Fragment {

    private ImageView imageView;
    private Uri imageUri;

    private Button saveButton, imgButton;
    private EditText name, age, email, phone_number;
    private String imageURL;
    private ArrayList<Class_friend> friendList = new ArrayList<>();

    public Add_friend() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_friend, container, false);

        name = view.findViewById(R.id.friend_name);
        age = view.findViewById(R.id.friend_age);
        email = view.findViewById(R.id.friend_email);
        phone_number = view.findViewById(R.id.friend_phone);
        saveButton = view.findViewById(R.id.add_button);
        imageView = view.findViewById(R.id.images_1);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Check if any of the fields are empty
                if (name.getText().toString().isEmpty() || age.getText().toString().isEmpty()  ||
                        email.getText().toString().isEmpty()  || phone_number.getText().toString().isEmpty() ) {
                    general_analog(getContext(), "🚫 Missing Information 🚫", "Please fill in all fields.");
                    return;
                }

                for (Class_friend friend : friendList) {
                    if (friend.getName().equalsIgnoreCase(name.getText().toString().trim())) {
                        general_analog(getContext(), "🚫 Duplicate Name 🚫", "A friend with the same name already exists.");
                        return;
                    }
                }

                Class_friend newFriend = new Class_friend(
                        name.getText().toString(),
                        age.getText().toString(),
                        email.getText().toString(),
                        phone_number.getText().toString()
                );

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Friend");
                String uniqueKey = myRef.push().getKey();

                myRef.child(uniqueKey).setValue(newFriend);
                general_analog(getContext(), "✨ Information ✨", "Successful data saving. 💖");

                name.setText("");
                age.setText("");
                phone_number.setText("");
                email.setText("");
            }
        });

        // 8. Back button ************************************************************************
        ImageButton back_button = view.findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Friend friend_object = new Friend();
                Fragment fragment = friend_object;
                friend_object.setFriendList(friendList);
                FragmentTransaction transaction = getActivity()
                        .getSupportFragmentManager()
                        .beginTransaction();
                transaction.replace(R.id.fragment_container,fragment)
                        .commit();
            }

        });

        return view;
    }

    public void general_analog(Context context, String message_1, String message_2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(message_1);
        builder.setMessage(message_2);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Optionally, add code here to handle the "Ok" button click
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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


    public void setFriendList(ArrayList<Class_friend> friendList) {
        this.friendList = friendList;
    }



}

