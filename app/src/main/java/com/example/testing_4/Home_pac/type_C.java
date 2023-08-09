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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;

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

public class type_C extends Fragment {


    private ArrayList<Class_friend> friendList = new ArrayList<>();
    private EditText date, total, location;
    private DatePickerDialog datePickerDialog;

    private Integer ratio_total = 0;

    private String check_me = "NO";

    private Integer percentage_me = 0;

    private double percentage_value = 0;

    private List<String> friend_List = new ArrayList<>();
    private EditText percentageEditText;

    private List bill_info = new ArrayList();

    private String name_list;
    private String value_list;


    private StringBuilder stringBuilder = new StringBuilder();

    private List<Integer> percentageEditTextList = new ArrayList<>();

    private String name;

    private String percentageText;

    public type_C() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_type__c, container, false);

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
                                // set day of month, month, and year value in the edit text
                                date.setText(dayOfMonth + "/"
                                        + (monthOfYear + 1) + "/" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }

        });

        // 3. CheckBox (include me click)
        TableLayout tableLayout_me = view.findViewById(R.id.table_layout_me);
        CheckBox checkBox_me = view.findViewById(R.id.checkBox_me);
        TableRow row = new TableRow(getContext());

        checkBox_me.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                EditText editText = new EditText(getContext());
                editText.setHint("Enter ratio");
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);

                if (isChecked) {

                    check_me = "YES";
                    Button buttonSave = new Button(getContext());
                    buttonSave.setText("Save");

                    Button buttonDelete = new Button(getContext());
                    buttonDelete.setText("Delete");
                    row.addView(editText);
                    row.addView(buttonSave);
                    row.addView(buttonDelete);
                    tableLayout_me.addView(row);

                    buttonSave.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            percentage_me = Integer.parseInt(editText.getText().toString());
                            ratio_total += percentage_me;
                        }
                    });
                    buttonDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ratio_total -= percentage_me;
                            percentage_me = 0;
                            editText.setText("");
                        }
                    });
                } else {
                    percentage_me = 0;
                    check_me = "NO";
                    if (percentageEditText != null) {
                        tableLayout_me.removeView(row);
                    }
                }

            }
        });


        // 4. friend
        TableLayout tableLayout = view.findViewById(R.id.table_layout);
        Button button_1 = view.findViewById(R.id.button2);

        button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TableRow row = new TableRow(getContext());
                EditText nameEditText = new EditText(getContext());
                EditText percentageEditText = new EditText(getContext());

                nameEditText.setHint("Name");
                percentageEditText.setHint("Enter ratio");
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
                                Integer percentage = Integer.parseInt(percentageText);
                                int found = 0;

                                for (Class_friend friend : friendList) {
                                    if (friend.getName().toLowerCase().trim().equalsIgnoreCase(name.toLowerCase().trim())) {
                                        boolean isDuplicate = false;
                                        for (String x : friend_List) {
                                            if (x.toLowerCase().trim().equalsIgnoreCase(name.toLowerCase().trim())) {
                                                isDuplicate = true;
                                                break;
                                            }
                                        }
                                        if (!isDuplicate) {
                                            friend_List.add(name);
                                            percentageEditTextList.add(percentage);
                                            ratio_total += percentage;
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

                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                String error_word = "Please insert again the name or ratio 💨";
                                general_analog(getContext(),"✨ Attention ✨" , error_word);
                                nameEditText.setText("");
                                percentageEditText.setText("");
                            }
                        }else{
                            String error_word = "Please insert the name or ratio. 💨";
                            general_analog(getContext(),"✨ Attention ✨" , error_word);
                        }
                    }
                });

                // 6. Delet button
                buttonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            int size_list = friend_List.size();

                            ratio_total -= percentageEditTextList.get(size_list - 1);

                            friend_List.remove(size_list - 1);
                            percentageEditTextList.remove(size_list - 1);

                            tableLayout.removeView(row);

                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                });

                row.addView(nameEditText);
                row.addView(percentageEditText);
                row.addView(buttonSave);
                row.addView(buttonDelete);
                tableLayout.addView(row);
            }
        });

        // 5. Submit button
        ImageButton button_2 = view.findViewById(R.id.imageButton1);
        button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stringBuilder.setLength(0);

                double total_bill = Double.parseDouble(total.getText().toString());
                DecimalFormat decimalFormat = new DecimalFormat("#0.00");

                if (check_me.equals("YES")) {
                    percentage_value = Double.parseDouble(decimalFormat.format(total_bill * ((double)percentage_me / ratio_total)));
                    stringBuilder.append("ME : RM" + percentage_value + "\n");
                }

                for (int i = 0; i < friend_List.size(); i++) {
                    String name = friend_List.get(i);
                    int percentage = percentageEditTextList.get(i);
                    double value = Double.parseDouble(decimalFormat.format(total_bill * ((double)percentage / ratio_total)));
                    if(i == 0){
                        name_list = name + ",";
                        value_list = Double.toString(value) + ",";
                    }else{
                        name_list += name + ",";
                        value_list += Double.toString(value) + ",";
                    }
                    stringBuilder.append(name + " : RM" + value + "\n");
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("✨ Result ✨");
                builder.setMessage(stringBuilder.toString());

                builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Bill_Data bill_info = new Bill_Data(date.getText().toString(),name_list,location.getText().toString(), Double.toString(percentage_value)
                                ,total.getText().toString(),value_list);

                        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Bill");
                        String uniqueKey = myRef.push().getKey();
                        myRef.child(uniqueKey).setValue(bill_info);

                        total.setText("");
                        location.setText("");
                        friend_List.clear();
                        date.setText("1/1/2023");
                        percentageEditTextList.clear();
                        checkBox_me.setChecked(false);
                        check_me = "NO";
                        percentage_me = 0;
                        ratio_total = 0;
                        stringBuilder.delete(0, stringBuilder.length());

                        general_analog(getContext(),"✨ Information ✨ " , "Successful data saving. 💖");
                        nextPage();
                    }
                });

                builder.setNegativeButton("No need save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        total.setText("");
                        location.setText("");
                        friend_List.clear();
                        date.setText("1/1/2023");
                        percentageEditTextList.clear();
                        checkBox_me.setChecked(false);
                        check_me = "NO";
                        percentage_me = 0;
                        ratio_total = 0;
                        stringBuilder.delete(0, stringBuilder.length());                    }
                });

                // Create and show the AlertDialog
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
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

        return view;
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
        type_C object_typeC = new type_C();
        Fragment fragment = object_typeC;
        object_typeC.setFriend_List(friendList);
        FragmentTransaction transaction = getActivity()
                .getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.fragment_container,fragment)
                .commit();

    }

}


