package com.example.testing_4.History;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.collection.CircularArray;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.testing_4.R;
import com.example.testing_4.friend_pac.Class_friend;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class History extends Fragment {

    private ArrayList<Bill_Data> BillList = new ArrayList<>();
    private ArrayList<String> keyList = new ArrayList<>();

    private History_Adapter history_adapter;
    private ArrayList<Class_friend> friendList  = new ArrayList<>();

    public History() {
        load_FriendList();
        load_HistoryList();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);


        // 3. Recycler view + ItemTouchHelper (to delete)
        RecyclerView history_recyclerView = view.findViewById(R.id.history_recycleView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        history_adapter = new History_Adapter(getContext(), BillList);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(history_recyclerView);

        ItemTouchHelper itemTouchHelper_share = new ItemTouchHelper(simpleCallback_share);
        itemTouchHelper_share.attachToRecyclerView(history_recyclerView);



        SearchView history_searchView = view.findViewById(R.id.history_searchView);
        history_searchView.clearFocus();
        history_searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        history_recyclerView.setLayoutManager(layoutManager);
        history_recyclerView.setAdapter(history_adapter);

        return view;
    }


    private void filterList(String text) {
        ArrayList<Bill_Data> filteredList = new ArrayList<>();
        for (Bill_Data item : BillList) {
            if (item.getDate().trim().contains(text.trim())) {
                filteredList.add(item);
            }
        }
        if (filteredList.isEmpty()) {
//            Toast.makeText(Friend.this,"No data found",Toast.LENGTH_SHORT).show();
        } else {
            history_adapter.setFilteredList(getContext(), filteredList);
//            friend_adapter.notifyDataSetChanged();
        }
    }


    // Swipt
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            if (position < BillList.size()) { // Check if the position is within the valid range
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Delete Bill History");
                builder.setMessage("Are you sure to delete");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String key = keyList.get(position);
                        BillList.remove(position); // Remove the item from the list
                        keyList.remove(position);
                        history_adapter.setHistoryList(BillList);
                        history_adapter.notifyItemRemoved(position); // Notify the adapter about the removal

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Bill");

                        databaseReference.child(key).removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        // Key and associated data deletion successful
//                                        Log.d("Hey", "Successfully deleted the key and its data: " + uniqueKeyToDelete);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Key deletion failed
//                                        Log.d("Hey", "Failed to delete the key and its data: " + uniqueKeyToDelete);
                                    }
                                });
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        history_adapter.notifyItemChanged(viewHolder.getAdapterPosition());
                    }
                });
                builder.show();
            }
        }

    };

    ItemTouchHelper.SimpleCallback simpleCallback_share = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // Build a list of friend names to display in the AlertDialog

            int position = viewHolder.getAdapterPosition();
            if (position < BillList.size()) { // Check if the position is within the valid range

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Share Bill information");
                builder.setMessage("Are you sure to share ? ");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        List<String> friendNames = new ArrayList<>();
                        String email_friend = "";
                        String bill_information = "";

                        Bill_Data bill_data = BillList.get(position);
                        String friend_name = bill_data.getFriend_list();
                        String friend_value = bill_data.getValue_list();
                        String[] friend_namelist = friend_name.split(",");
                        String[] friend_valuelist = friend_value.split(",");

                        StringBuilder info_string_builder = new StringBuilder();

                        info_string_builder.append("This is the bill information \n");
                        info_string_builder.append("Date\t:" + bill_data.getDate() + "\n");
                        info_string_builder.append("Location \t:" + bill_data.getLocation() + "\n");
                        info_string_builder.append("Total\t:" + bill_data.getTotal() + "\n\n\n");
                        info_string_builder.append(" ---------- Result ----------" + "\n");

                        if( Double.parseDouble(bill_data.getMyValue()) >0){
                            info_string_builder.append("Me     : RM" + bill_data.getMyValue()+"\n");
                        }

                        for(String x : friend_namelist){
                            friendNames.add(x);

                        }

                        for(int i = 0 ; i< friend_namelist.length ; i++){
                            friendNames.add(friend_namelist[i]);
                            info_string_builder.append(friend_namelist[i] + ": RM" + friend_valuelist[i] +"\n");
                        }

                        for (Class_friend friend : friendList) {
                            if(friendNames.contains(friend.getName().trim())){
                                email_friend += (friend.getEmail() + ";");
                            }
                        }

                        share(email_friend, String.valueOf(info_string_builder),getContext());
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform action when the "Cancel" button is clicked
                        dialog.dismiss();
                    }
                });


                builder.show();
                history_adapter.notifyItemChanged(viewHolder.getAdapterPosition());


            }
        }
    };

    private void share(String emailAddress, String message ,Context context) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Bill");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        context.startActivity(Intent.createChooser(intent, "Send Email"));

    }

    public void setFriendList(ArrayList<Class_friend> friendList) {
        this.friendList = friendList;
    }

    public void setKeyList(ArrayList<String> keyList) {
        this.keyList = keyList;
    }

    public void setBillList(ArrayList<Bill_Data> BillList) {
        this.BillList = BillList;
        if (history_adapter != null) {
            history_adapter.setHistoryList(BillList);
            history_adapter.notifyDataSetChanged();
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