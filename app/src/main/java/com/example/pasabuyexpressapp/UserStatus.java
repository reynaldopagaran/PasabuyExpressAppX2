package com.example.pasabuyexpressapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pasabuyexpressapp.activities.report;
import com.example.pasabuyexpressapp.utilities.Constants;
import com.example.pasabuyexpressapp.utilities.LocationUpdatesService;
import com.example.pasabuyexpressapp.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class UserStatus extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button button, btn_service_stop, report;
    Spinner spinner;
    TextView textView;
    DatabaseReference databaseReference;
    String item;
    Status status;
    TextView name;
    String[] profilestatus = {"PASABUYER", "BUYER"};
    String email, xemail;
    FirebaseFirestore db;
    FirebaseAuth auth;
    private PreferenceManager preferenceManager;

    //my add
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_USERNAME = "username";
    String spName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_status);


        //my add
        sharedPreferences = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        spName = sharedPreferences.getString(KEY_USERNAME, null);

        textView = findViewById(R.id.tvStatus);
        button = findViewById(R.id.savebutton);
        btn_service_stop = findViewById(R.id.stopLocationUpdates);


        report = findViewById(R.id.report);
        //FirebaseFirestore database = FirebaseFirestore.getInstance();
        //databaseReference = database.getNamedQuery("Value");
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        //databaseReference = database.getReference("Value");
        spinner = findViewById(R.id.StatuspinnerId);
        spinner.setOnItemSelectedListener(this);

        //xemail = auth.getCurrentUser().getUid();

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, profilestatus);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);

        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserStatus.this, com.example.pasabuyexpressapp.activities.report.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                userRole(spName);
                SaveValue(item);

                Intent intent = new Intent(UserStatus.this, Chatbox.class);
                intent.putExtra("status", item);
                startActivity(intent);

            }
        });

        if(isLocationServiceRunning(LocationUpdatesService.class)){
            btn_service_stop.setVisibility(View.VISIBLE);
        }

        btn_service_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(UserStatus.this, LocationUpdatesService.class));
            }
        });
    }

    private boolean isLocationServiceRunning(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(serviceInfo.service.getClassName())){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        item = spinner.getSelectedItem().toString();
        textView.setText(item);

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    void SaveValue(String item) {
        if (item == "Select Status") {
            Toast.makeText(this, "please select a Status", Toast.LENGTH_SHORT).show();
        } else {
            updateData(item);
            Toast.makeText(this, "status saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateData(String xstatus) {

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        db.collection("users").document(preferenceManager.getString(Constants.KEY_USER_ID))
                .update("status", xstatus).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Toast.makeText(UserStatus.this,"Successfully Updated", Toast.LENGTH_SHORT).show();
                if(xstatus.equals("BUYER")){
                    if(!isLocationServiceRunning(LocationUpdatesService.class)){
                        Intent intent = new Intent(UserStatus.this, LocationUpdatesService.class);
                        startService(intent);
                    }
                }
            }
        });
    }

    public void userRole(String spName){
        db.collection("users")
                .whereEqualTo("email", spName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                //document.getData().get("name").toString();
                                //Date
                                Date c = Calendar.getInstance().getTime();
                                SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                                String formattedDate = df.format(c);

                                //data
                                Map<String, Object> data = new HashMap<>();
                                data.put("name", document.getData().get("name").toString());
                                data.put("role",spinner.getSelectedItem().toString());
                                data.put("date", formattedDate);

                                db.collection("userRole")
                                        .add(data)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {

                                            }
                                        });
                            }
                        } else {

                        }
                    }
                });
    }

}