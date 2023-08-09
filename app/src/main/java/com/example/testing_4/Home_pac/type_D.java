package com.example.testing_4.Home_pac;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.testing_4.History.Bill_Data;
import com.example.testing_4.R;
import com.example.testing_4.friend_pac.Class_friend;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class type_D extends Fragment {

    private ArrayList<Class_friend> friendList = new ArrayList<>();
    private EditText date, total, location;
    private DatePickerDialog datePickerDialog;

    private final double percentage_fix = 100;

    private double total_value = 0;

    private String check_me_percentage = "NO";
    private String check_me_amount = "NO";

    private double check_percentage_me = 0;
    private double check_amount_me = 0;


    private List<String> friend_List_percentage = new ArrayList<>();
    private List<String> friend_List_amount = new ArrayList<>();

    private String name_list;
    private String value_list;

    private String inserted_friend_percentage = "NO";
    private String inserted_friend_amount = "NO";

    private StringBuilder stringBuilder = new StringBuilder();

    private List<Double> percentageEditTextList_percentage = new ArrayList<>();
    private List<Double> percentageEditTextList_amount = new ArrayList<>();

    private String me = "NO";
    private Double me_value = 0.0;


    public type_D() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_type__d, container, false);


        // 1. Get data
        total = view.findViewById(R.id.detail_total);
        location = view.findViewById(R.id.detail_location);


        total.addTextChangedListener(new TextWatcher() {
            String newText = "";
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // This method is called before the text is changed.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // This method is called when the text is changing.
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // This method is called after the text has changed.
                newText = editable.toString();
                total_value = Double.parseDouble(newText);
            }
        });

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
                                // set day of month, month, and year value in the edit text
                                date.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }

        });


        // 5. Add different friend
        TableLayout tableLayout = view.findViewById(R.id.table_layout);
        Button button_1 = view.findViewById(R.id.button2);

        button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableRow row = new TableRow(getContext());
                EditText nameEditText = new EditText(getContext());
                EditText percentageEditText = new EditText(getContext());

                nameEditText.setHint("Name");
                percentageEditText.setHint("Percentage");
                percentageEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

                Button buttonSave = new Button(getContext());
                buttonSave.setText("Save");

                Button buttonDelete = new Button(getContext());
                buttonDelete.setText("Delete");

                buttonSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = nameEditText.getText().toString().trim();
                        String percentageText = percentageEditText.getText().toString().trim();

                        if (!name.isEmpty() && !percentageText.isEmpty()) {
                            try {
                                Double percentage = Double.parseDouble(percentageText);
                                Double value_percentage = Double.valueOf(total_value * (percentage / percentage_fix));
                                int found = 0;

                                if (name.equals("ME")) {
                                    check_me_percentage = "YES";
                                    check_percentage_me = value_percentage;
                                    total_value -= value_percentage;

                                }else{
                                    for (Class_friend friend : friendList) {
                                        if (friend.getName().toLowerCase().trim().equalsIgnoreCase(name.toLowerCase().trim())) {
                                            boolean isDuplicate = false;
                                            for (String x : friend_List_percentage) {
                                                if (x.toLowerCase().trim().equalsIgnoreCase(name.toLowerCase().trim())) {
                                                    isDuplicate = true;
                                                    break;
                                                }
                                            }
                                            if (!isDuplicate) {
                                                friend_List_percentage.add(name);
                                                percentageEditTextList_percentage.add(value_percentage);
                                                inserted_friend_percentage = "YES";
                                                total_value -= value_percentage;
                                                found++;
                                                break;
                                            }
                                        }
                                    }

                                    if (found == 0) {
                                        String error_word = "Please try again with the correct name. \n" +
                                                "Hint : Press the info icon to check the name list. ";

                                        general_analog(getContext(),"✨ Incorrect Input ✨" , error_word);
                                        nameEditText.setText("");
                                        percentageEditText.setText("");
                                    }
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                String error_word = "Please insert again the name or percentage 💨";
                                general_analog(getContext(),"✨ Attention ✨" , error_word);
                                nameEditText.setText("");
                                percentageEditText.setText("");
                            }
                        }else{
                            String error_word = "Please insert the name or percentage. 💨";
                            general_analog(getContext(),"✨ Attention ✨" , error_word);
                        }
                    }
                });

                row.addView(nameEditText);
                row.addView(percentageEditText);
                row.addView(buttonSave);
                row.addView(buttonDelete);
                tableLayout.addView(row);

                // Delete button
                buttonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (inserted_friend_percentage.equals("YES")) {

                            // Remove the last item from the lists
                            if (!percentageEditTextList_percentage.isEmpty()) {
                                int percentagetotal = percentageEditTextList_percentage.size();
                                Double removedValue = percentageEditTextList_percentage.get(percentagetotal - 1);
                                percentageEditTextList_percentage.remove(percentagetotal - 1);
                                total_value += removedValue;
                            }
                            if (!friend_List_percentage.isEmpty()) {
                                friend_List_percentage.remove(friend_List_percentage.size() - 1);
                            }

                            // Remove the row from the tableLayout
                            tableLayout.removeView(row);
                        } else {
                            general_analog(getContext(), "Attention", "Can't delete because no value is inserted");
                        }
                    }
                });
            }
        });

        // 5. Add different friend
        TableLayout tableLayout2 = view.findViewById(R.id.table_layout_amount);
        Button button_2 = view.findViewById(R.id.Add_amount);

        button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableRow row = new TableRow(getContext());
                EditText nameEditText = new EditText(getContext());
                EditText percentageEditText = new EditText(getContext());

                nameEditText.setHint("Name");
                percentageEditText.setHint("Amount");
                percentageEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);


                Button buttonSave = new Button(getContext());
                buttonSave.setText("Save");

                Button buttonDelete = new Button(getContext());
                buttonDelete.setText("Delete");


                buttonSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = nameEditText.getText().toString().trim();
                        String percentageText = percentageEditText.getText().toString().trim();

                        if (!name.isEmpty() && !percentageText.isEmpty()) {
                            try {
                                Double percentage = Double.parseDouble(percentageText);

                                if(name.equals("ME")){
                                    check_me_amount = "YES";
                                    check_amount_me = percentage;
                                    total_value -= percentage;

                                }else{

                                    int found = 0;

                                    for (Class_friend friend : friendList) {
                                        if (friend.getName().toLowerCase().trim().equalsIgnoreCase(name.toLowerCase().trim())) {
                                            boolean isDuplicate = false;
                                            for (String x : friend_List_amount) {
                                                if (x.toLowerCase().trim().equalsIgnoreCase(name.toLowerCase().trim())) {
                                                    isDuplicate = true;
                                                    break;
                                                }
                                            }
                                            if (!isDuplicate) {
                                                friend_List_amount.add(name);
                                                percentageEditTextList_amount.add(percentage);
                                                inserted_friend_amount = "YES";
                                                total_value -= percentage;
                                                found++;
                                                break;
                                            }
                                        }
                                    }

                                    if (found == 0) {
                                        String error_word = "Please try again with the correct name. \n" +
                                                "Hint : Press the info icon to check the name list. ";

                                        general_analog(getContext(),"✨ Incorrect Input ✨" , error_word);
                                        nameEditText.setText("");
                                        percentageEditText.setText("");
                                    }

                                }

                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                String error_word = "Please insert again the name or amount 💨";
                                general_analog(getContext(),"✨ Attention ✨" , error_word);
                                nameEditText.setText("");
                                percentageEditText.setText("");
                            }
                        }else{
                            String error_word = "Please insert the name or amount. 💨";
                            general_analog(getContext(),"✨ Attention ✨" , error_word);

                        }
                    }

                });

                row.addView(nameEditText);
                row.addView(percentageEditText);
                row.addView(buttonSave);
                row.addView(buttonDelete);
                tableLayout2.addView(row);

                // 6. Delete button
                buttonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (inserted_friend_amount.equals("YES")) {

                            // Remove the last item from the lists
                            if (!percentageEditTextList_amount .isEmpty()) {
                                int percentagetotal = percentageEditTextList_amount .size();
                                Double removedValue = percentageEditTextList_amount .get(percentagetotal - 1);
                                percentageEditTextList_amount.remove(percentagetotal - 1);
                                total_value += removedValue;
                            }
                            if (!friend_List_percentage.isEmpty()) {
                                friend_List_percentage.remove(friend_List_percentage.size() - 1);
                            }

                            // Remove the row from the tableLayout
                            tableLayout2.removeView(row);
                        } else {
                            general_analog(getContext(), "✨ Attention ✨", "Can't delete because no value is inserted 💨");
                        }
                    }
                });
            }
        });

        // 5. Submit button
        ImageButton save_button = view.findViewById(R.id.imageButton1);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stringBuilder.setLength(0);

                if(total_value == 0.0 ){
                    if (check_me_percentage.equals("YES") && check_me_amount.equals("YES")) {
                        me_value = check_percentage_me + check_amount_me;
                        stringBuilder.append("ME : RM" + me_value + "\n");
                    }else if(check_me_percentage.equals("YES")&& ! (check_me_amount.equals("YES"))){
                        stringBuilder.append("ME : RM" + check_percentage_me + "\n");
                        me_value = check_percentage_me;
                        me = "YES";
                    }else if (!(check_me_percentage.equals("YES")) && check_me_amount.equals("YES")){
                        stringBuilder.append("ME : RM" + check_amount_me + "\n");
                        me_value = check_amount_me;
                        me = "YES";
                    }

                    for (int i = 0; i < friend_List_percentage.size(); i++) {
                        String name = friend_List_percentage.get(i);
                        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                        Double percentage = Double.valueOf(decimalFormat.format(percentageEditTextList_percentage.get(i)));

                        if (name.equals("ME")){
                            continue;
                        }else{
                            if(i == 0){
                                name_list = name + ",";
                                value_list = percentage + ",";
                            }else{
                                name_list += name + ",";
                                value_list += percentage + ",";
                            }
                            stringBuilder.append(name + " : RM" + percentage + "\n");
                        }

                    }

                    for (int i = 0; i < friend_List_amount.size(); i++) {
                        String name = friend_List_amount.get(i);
                        Double amount = percentageEditTextList_amount.get(i);

                        if (name.equals("ME")){
                            continue;
                        }else{
                            if(i == 0){
                                name_list = name + ",";
                                value_list = amount + ",";
                            }else{
                                name_list += name + ",";
                                value_list += amount + ",";
                            }
                            stringBuilder.append(name + " : RM" + amount + "\n");
                        }

                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("✨ Result ✨");
                    builder.setMessage(stringBuilder.toString());

                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                            Bill_Data bill_info = new Bill_Data(date.getText().toString(),name_list,location.getText().toString(), Double.toString(me_value)
                                    ,total.getText().toString(),value_list);

                            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Bill");

                            String uniqueKey = myRef.push().getKey();

                            myRef.child(uniqueKey).setValue(bill_info);
                            location.setText("");
                            date.setText("1/1/2023");
                            friend_List_percentage.clear();
                            friend_List_amount.clear();
                            percentageEditTextList_percentage .clear();
                            percentageEditTextList_amount .clear();
                            inserted_friend_percentage = "NO";
                            inserted_friend_amount = "NO";
                            check_me_percentage = "NO";
                            check_me_amount = "NO";
                            check_percentage_me = 0.0;
                            check_amount_me = 0.0;
                            name_list="";
                            value_list = "";
                            total_value = 0.0;
                            me = "NO";
                            me_value = 0.0;
                            stringBuilder.delete(0, stringBuilder.length());
                            general_analog(getContext(),"✨ Information ✨ " , "Successful data saving. 💖");
                            nextPage();
                        }
                    });

                    builder.setNegativeButton("No need save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            location.setText("");
                            date.setText("1/1/2023");
                            friend_List_percentage.clear();
                            friend_List_amount.clear();
                            percentageEditTextList_percentage .clear();
                            percentageEditTextList_amount .clear();
                            inserted_friend_percentage = "NO";
                            inserted_friend_amount = "NO";
                            check_me_percentage = "NO";
                            check_me_amount = "NO";
                            check_percentage_me = 0.0;
                            check_amount_me = 0.0;
                            name_list="";
                            value_list = "";
                            total_value = 0.0;
                            me = "NO";
                            me_value = 0.0;
                            stringBuilder.delete(0, stringBuilder.length());
                            general_analog(getContext(),"✨ Information ✨ " , "Okeii. 💖");
                            nextPage();
                        }
                    });

                    // Create and show the AlertDialog
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }else{

                    location.setText("");
                    date.setText("1/1/2023");
                    friend_List_percentage.clear();
                    friend_List_amount.clear();
                    percentageEditTextList_percentage .clear();
                    percentageEditTextList_amount .clear();
                    inserted_friend_percentage = "NO";
                    inserted_friend_amount = "NO";
                    check_me_percentage = "NO";
                    check_me_amount = "NO";
                    check_percentage_me = 0.0;
                    check_amount_me = 0.0;
                    name_list="";
                    value_list = "";
                    total_value = 0.0;
                    me = "NO";
                    me_value = 0.0;
                    stringBuilder.delete(0, stringBuilder.length());

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("✨ Incorrect Input ✨");
                    builder.setMessage("Please Try again with the correct amount and percentage. 💨\n" +
                            "Hint : Press the light bulb icon to check the amount and percentage. ");

                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            nextPage();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });


        // 6. Hint Button
        ImageButton hint_button = view.findViewById(R.id.hint_button);
        hint_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String stringBuilder2 = "";
                Double total_A = Double.parseDouble(total.getText().toString());
                Double left ;

                left = total_A - total_value ;

                stringBuilder2 += "Total inserted  : " + total_A + "\n";
                stringBuilder2 += "Total currently : " + left + "\n";
                stringBuilder2 += "Left            : "+ total_value + "\n\n";

                general_analog(getContext(),"Attention" , stringBuilder2);


            }

        });

        // 7. Info button ************************************************************************
        ImageButton  infobutton = view.findViewById(R.id.info_button);
        infobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String string_1 = "";

                List<String> display_name = new ArrayList<>();

                for (Class_friend friend : friendList) {
                    display_name.add(friend.getName());
                }

                Collections.sort(display_name);
                string_1 += "----- Friend List ------\n\n";

                for(int i =0 ; i< display_name.size() ; i++){
                    int number = i+1;
                    string_1 += number+"\t"+display_name.get(i) + "\n";
                };

                general_analog(getContext(),"✨ Attention ✨" , string_1);


            }

        });

        // 8. Back button ************************************************************************
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

        return view ;
    }

    public void setFriend_List(ArrayList<Class_friend> friendList) {
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

    public void nextPage(){
        type_D object_typeD = new type_D();
        Fragment fragment = object_typeD;
        object_typeD.setFriend_List(friendList);
        FragmentTransaction transaction = getActivity()
                .getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fragment_container,fragment)
                .commit();
    };

}