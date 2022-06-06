package com.example.pasabuyexpressapp;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ViewStatus extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView textView;
    DatabaseReference databaseReference;
    Status status;
    String item;
    String spinner;

    String [] profilestatus = {"Select Status", "PASABUY USER", "BUYER"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_status);

        textView = findViewById(R.id.tvStatus);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Value");
        //spinner = findViewById(R.id.spin);
      //  spinner.();

        status = new Status();
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, profilestatus);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveValue(item);
            }
        });
    }

    private void SaveValue(String item) {
        if (item == "Select Status"){
            Toast.makeText(this, "please select a Status", Toast.LENGTH_SHORT).show();
        }else {
            status.setProfilestatus(item);
            String id = databaseReference.push().getKey();
            databaseReference.child(id).setValue(status);
            Toast.makeText(this, "value saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        item = spinner.toString();
        textView.setText(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}



