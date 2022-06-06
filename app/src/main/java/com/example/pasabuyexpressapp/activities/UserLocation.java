package com.example.pasabuyexpressapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pasabuyexpressapp.Chatbox;
import com.example.pasabuyexpressapp.R;
import com.example.pasabuyexpressapp.utilities.Constants;
import com.example.pasabuyexpressapp.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;



public class UserLocation extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    FirebaseFirestore db;
    FirebaseAuth auth;
    String item;
    private PreferenceManager preferenceManager;

    String[] ProfileLocation = {"SET YOUR LOCATION ","OFFLINE", "ABREEZA MALL DAVAO", "FELCRIS CENTRALE", "GAISANO GRAND CITIMALL ILLUSTRE", "GAISANO GRAND TIBUNGCO", "GAISANO MALL OF DAVAO", "NCCC MALL BUHANGIN", "NCCC PANACAN", "PUREGOLD LANANG", "S&R MEMBERSHIP SHOPPING",
            "ROBINSON CYBERGATE DAVAO", "SM CITY DAVAOO", "SM LANANG PREMIER"};



    Button button2;
    TextView textView;
    Spinner spinner2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_location);

        textView = findViewById(R.id.tvStatus);
        button2 = findViewById(R.id.savebutton2);


        //FirebaseFirestore database = FirebaseFirestore.getInstance();
        //databaseReference = database.getNamedQuery("Value");
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        //databaseReference = database.getReference("Value");
        spinner2 = findViewById(R.id.UserLocation);
        spinner2.setOnItemSelectedListener(this);



        //xemail = auth.getCurrentUser().getUid();

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ProfileLocation);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner2.setAdapter(arrayAdapter);



        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


               // Intent intent = new Intent(UserLocation.this, Chatbox.class);
               // intent.putExtra("location", item);

               // startActivity(intent);
                SaveValue(item);
                UserLocation.this.finish();
                //updateData(item);


            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        item = spinner2.getSelectedItem().toString();
        textView.setText(item);



    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    void SaveValue(String item) {
        if (item == "Select Status") {
            Toast.makeText(this, "please select a Status", Toast.LENGTH_SHORT).show();
        } else {
//    status.setProfilestatus(item);
//    String id = databaseReference.push().getKey();
//    databaseReference.child(id).setValue(status);
            updateData(item);


            Toast.makeText(this, "Location saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateData(String xstatus) {

       // Intent intent = getIntent();
      //  email = intent.getStringExtra("email");
//        Map<String,Object> userDetail = new HashMap<>();
//        userDetail.put("status",xstatus);


        db.collection("users").document(preferenceManager.getString(Constants.KEY_USER_ID))
                .update("location", xstatus).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Toast.makeText(UserStatus.this,"Successfully Updated", Toast.LENGTH_SHORT).show();
            }
        });
    }


}

//////

