package com.example.pasabuyexpressapp.activities;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pasabuyexpressapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class history extends AppCompatActivity {

    List<historyData> list;

    Context context;
    EditText date1;
    final Calendar myCalendar= Calendar.getInstance();
    Button search;

    RecyclerView recyclerView;
    //my add
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_USERNAME = "username";
    String spName;
    String stringDate;
    Button listAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //my add
        sharedPreferences = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        spName = sharedPreferences.getString(KEY_USERNAME, null);
        date1 = findViewById(R.id.date);
        search = findViewById(R.id.btnSearch);
        listAll = findViewById(R.id.btnListAll);
        list = new ArrayList<>();

        listAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getHistory(spName);
            }
        });

        DatePickerDialog.OnDateSetListener date =new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH,month);
                myCalendar.set(Calendar.DAY_OF_MONTH,day);
                updateLabel();
            }
        };

        date1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(history.this,date,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH),myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(stringDate)){
                    date1.setError("Pick a date.");
                    return;
                }else {
                    searchHistory(spName, stringDate);
                }

            }
        });

    }

    public void searchHistory(String string, String date){
        recyclerView = findViewById(R.id.histRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(history.this));

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference fdr = database.getReference("items");

        fdr.orderByChild("email").equalTo(string + "-old").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {

                    if(ds.child("date").getValue().toString().equals(date)){
                        historyData historyData = new historyData(
                                ds.child("addItem").getValue().toString(),
                                ds.child("addQuantity").getValue().toString(),
                                ds.child("date").getValue().toString()
                        );
                        list.add(historyData);
                    }else {

                    }
                }
                histAdapter histAdapter = new histAdapter(history.this, list);
                recyclerView.setAdapter(histAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getHistory(String string) {
        recyclerView = findViewById(R.id.histRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(history.this));

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference fdr = database.getReference("items");

        fdr.orderByChild("email").equalTo(string + "-old").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                   historyData historyData = new historyData(
                           ds.child("addItem").getValue().toString(),
                           ds.child("addQuantity").getValue().toString(),
                           ds.child("date").getValue().toString()
                   );
                   list.add(historyData);
                }
                histAdapter histAdapter = new histAdapter(history.this, list);
                recyclerView.setAdapter(histAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void updateLabel(){
        String myFormat="MM-dd-yyyy";
        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
        date1.setText(dateFormat.format(myCalendar.getTime()));
        stringDate = date1.getText().toString();
    }
}