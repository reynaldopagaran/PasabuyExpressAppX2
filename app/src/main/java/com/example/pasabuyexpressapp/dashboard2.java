package com.example.pasabuyexpressapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pasabuyexpressapp.activities.ChatActivity;
import com.example.pasabuyexpressapp.activities.PasabuyerLocation;
import com.example.pasabuyexpressapp.activities.SignInActivity;
import com.example.pasabuyexpressapp.activities.UserLocation;
import com.example.pasabuyexpressapp.databinding.ActivityDashboard2Binding;
import com.example.pasabuyexpressapp.utilities.Constants;
import com.example.pasabuyexpressapp.utilities.LocationUpdatesService;
import com.example.pasabuyexpressapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class dashboard2 extends AppCompatActivity {
    ActivityDashboard2Binding binding;
    private PreferenceManager preferenceManager;
    ImageView btnLocation, btnLocation2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboard2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setListeners();

        btnLocation = (ImageView) findViewById(R.id.Profile);
        btnLocation2 = (ImageView) findViewById(R.id.Profile2);



        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(dashboard2.this, UserLocation.class);
                startActivity(intent);
            }
        });
        btnLocation2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(dashboard2.this, PasabuyerLocation.class);
                startActivity(intent);
            }
        });
    }


    private void setListeners() {
        binding.Logout.setOnClickListener(v -> signOut());
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signOut() {
        showToast("Signing Out");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();

                    if(isLocationServiceRunning(LocationUpdatesService.class)){
                        stopService(new Intent(dashboard2.this, LocationUpdatesService.class));
                    }

                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                })
                .addOnFailureListener(e -> showToast("Unable to sign out"));


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
    public void onBackPressed() {
        this.finish();
    }
}