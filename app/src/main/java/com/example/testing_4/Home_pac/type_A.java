package com.example.testing_4.Home_pac;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testing_4.History.Bill_Data;
import com.example.testing_4.History.History;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class type_A extends Fragment {

    private EditText date, total , location ;
    private DatePickerDialog datePickerDialog;

    private ArrayList<Class_friend> selected_list = new ArrayList<>();
    private ListView list_View;
    private long lastClickTime = 0;
    private ArrayList<Class_friend> friendList = new ArrayList<>();
    private List bill_info = new ArrayList();

    private List friend_list = new ArrayList();

    private int number;

    private double per_person;
    private StringBuilder stringBuilder = new StringBuilder();

    private ImageButton add_button;
    private String clicked_me ;
    private String friend_list_ = "";
    private String value_friend_list_ = "";

    private FragmentManager fragmentManager;
    private Fragment home_;

    public type_A() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_type__a, container, false);

        // 1. Get data
        total = view.findViewById(R.id.detail_total);
        location = view.findViewById(R.id.detail_location);

        // 2. Date
        date = view.findViewById(R.id.detail_date);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                date.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }

        });

        // 3. List View
        list_View = view.findViewById(R.id.friend_view);
        ArrayList<String> arrayList = getList(friendList);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_multiple_choice,
                arrayList);

        list_View.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list_View.setAdapter(arrayAdapter);

        list_View.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime < 500) {
                    // Double-click detected
                    String items = (String) adapterView.getItemAtPosition(i);
                    selected_list.remove(friendList.get(i));
                    // Notify the adapter to update the ListView
                    ((ArrayAdapter) list_View.getAdapter()).notifyDataSetChanged();
                    number -- ;
                } else {
                    // Single-click
                    String items = (String) adapterView.getItemAtPosition(i);
                    selected_list.add(friendList.get(i));
                    // Notify the adapter to update the ListView
                    ((ArrayAdapter) list_View.getAdapter()).notifyDataSetChanged();
                    number++;
                }
                lastClickTime = currentTime;
            }

        });

        // 4.CheckBox (include me click)
        CheckBox checkBox_me = view.findViewById(R.id.checkBox_me);
        checkBox_me.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    clicked_me = "YES";
                    number ++;
                } else {
                    // Remove "me" from the bill_info list when the CheckBox is deselected
                    clicked_me = "NO";
                    number -- ;
                }
            }
        });


        // 5. save
        add_button = view.findViewById(R.id.imageButton);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stringBuilder.setLength(0);

                DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                per_person = Double.parseDouble(decimalFormat.format(Double.parseDouble(total.getText().toString()) / number));

                if(clicked_me == "YES"){
                    stringBuilder.append("Me : "+per_person +"\n");
                }

                for(Class_friend x :selected_list){
                    String word;
                    word = (x.getName() + ": " + per_person);
                    friend_list_ += x.getName() + ",";
                    value_friend_list_ += Double.toString(per_person) + ",";
                    stringBuilder.append(word);
                    stringBuilder.append("\n");
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("✨ Result ✨");
                builder.setMessage(stringBuilder);

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Bill_Data bill_info = new Bill_Data(date.getText().toString(),friend_list_,location.getText().toString(), Double.toString(per_person)
                        ,total.getText().toString(),value_friend_list_);

                    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Bill");
                    String uniqueKey = myRef.push().getKey();
                    myRef.child(uniqueKey).setValue(bill_info);

                    total.setText("");
                    location.setText("");
                    friend_list.clear();
                    per_person = 0;
                    checkBox_me.setChecked(false);
                    selected_list.clear();
                    stringBuilder.delete(0, stringBuilder.length());
                    ((ArrayAdapter) list_View.getAdapter()).notifyDataSetChanged();

                        general_analog(getContext(),"✨ Information ✨ " , "Successful data saving. 💖");
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
                builder.setNegativeButton("No need save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        total.setText("");
                        location.setText("");
                        friend_list.clear();
                        per_person = 0;
                        checkBox_me.setChecked(false);
                        selected_list.clear();
                        stringBuilder.delete(0, stringBuilder.length());
                        ((ArrayAdapter) list_View.getAdapter()).notifyDataSetChanged();

                    }
                });

                // Create and show the AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }


        });

        // 6. Back button ************************************************************************
        ImageButton  back_button = view.findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Home homeFragment = new Home();
                FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, homeFragment);
                transaction.commit();
            }

        });




        return view;
    }

    private ArrayList<String> getList(ArrayList<Class_friend> friendList) {
        ArrayList<String> arrayList = new ArrayList<>();
        for (Class_friend x : friendList ){
            arrayList.add(x.getName());
        }
        return arrayList;
    }

    public void setFriendList(ArrayList<Class_friend> friendList) {
        this.friendList = friendList;
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


}