package com.example.pasabuyexpressapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class PasabuyerLocation extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    FirebaseFirestore db;
    FirebaseAuth auth;
    String item;
    private PreferenceManager preferenceManager;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    String[] ProfileLocation1 = {"SET YOUR LOCATION ", "OFFLINE", "POBLACION"," Barangay 1-A (Pob.)"," Barangay 2-A (Pob.)"," Barangay 3-A (Pob.)"," Barangay 4-A (Pob.)"," Barangay 5-A (Pob.)"," Barangay 6-A (Pob.)"," Barangay 7-A (Pob.)"," Barangay 8-A (Pob.)"," Barangay 9-A (Pob.)","Barangay 10-A (Pob.)",
            " Barangay 11-B (Pob.)"," Barangay 12-B (Pob.)"," Barangay 13-B (Pob.)"," Barangay 14-B (Pob.)"," Barangay 15-B (Pob.)"," Barangay 16-B (Pob.)"," Barangay 17-B (Pob.)"," Barangay 18-B (Pob.)"," Barangay 19-B (Pob.)"," Barangay 20-B (Pob.)",
            " Barangay 21-C (Pob.)"," Barangay 22-C (Pob.)"," Barangay 23-C (Pob.)"," Barangay 24-C (Pob.)"," Barangay 25-C (Pob.)"," Barangay 26-C (Pob.)"," Barangay 27-C (Pob.)"," Barangay 28-C (Pob.)"," Barangay  29-C (Pob.)"," Barangay  30-C (Pob.)",
            " Barangay 31-D (Pob.)"," Barangay 32-D (Pob.)"," Barangay 33-D (Pob.)"," Barangay 34-D (Pob.)"," Barangay 35-D (Pob.)"," Barangay 36-D (Pob.)"," Barangay 37-D (Pob.)"," Barangay 38-D (Pob.)"," Barangay 39-D (Pob.)"," Barangay 40-D (Pob.)",
            "TALOMO","Bago Aplaya","Bago Gallera","Baliok","Bucana","Catalunan Grande","Catalunan Pequeño","Dumoy","Langub","Maa","Magtuod","Matina Aplaya","Crossing","Matina Pangi","Talomo Proper",
            "AGDAO","Agdao Proper","Centro (San Juan)","Gov. Paciano Bangoy","Kap. Tomas Monteverde, Sr.","Lapu-Lapu","Leon Garcia","Rafael Castillo","San Antonio","Ubalde","Wilfredo Quirino",
            "BUHANGIN","Acasia","Alfonso Angliongto","Gov. Paciano Banggoy","Buhangin Proper","Cabantian","Caliawa","Communal","Indangan","Mandug","Pampanga","Sasa","Tigatto","Vicente Hizon Sr.","Waan",
            "BUNAWAN ","Alejandro Navarro (Lasang)","Bunawan Proper","Gatungan","Ilang","Mahayag","Mudiang","Panacan","San Isidro (Licanan)","Tibungco",
            "PAQUIBATO"," Colosas ","Fatima (Benowang)","Lumiad","Mabuhay","Malabog","Mapula","Panalum","Pandaitan","Pacquibato Proper","Paradise Embak","Salapawan","Sumimao","Tapak",
            "BAGUIO ","Baguio Proper","Cadalian","Carmen","Gumalang","Malagos","Tambubong","Tawan Tawan","Wines",
            "CALINAN","Biao Joaquin","Calinan Proper","Cawayan","Dacudao","Dalagdag","Dominga","Inayangan","Lacson","Pangyan","Riverside","Saloy","Sirib","Subasta","Talomo River","Tamayong","Wangan",
            "MARILOG","Baganihan","Bantul","Buda","Dalag","Datu Salumay","Gumitan","Magsaysay","Malamba","Marilog Proper","Salaysay","Suawan (Tuli)","Tamugan",
            "TORIL","Alambre","Atan-Awe","Bangkas Heights","Baracatan","Bato","Daliao","Daliaoan Plantation","Eden","Kilate","Lizada","Bayabas","Binugao","Calamansi","Catigan","Crossing Bayabas","Lubogan","Marapangi","Sibulan","Sirawan","Tagluno","Tagurano","Tibuloy","Toril Proper","Tungkalan",
            "TUGBOK","Angalan","Bago Oshiro","Balenggaeng","Biao Escuela","Biao Guinga","Daliao","Daliaoan Plantation","Eden","Kilate","Lizada","Los Amigos","Manambulan","Manuel Guianga","Matina Biao","Mintal","New Carmen","New Valencia","Santo Niño","Tacunan","Tagakpan","Talandang","Tugbok Proper","Ula"};

    Button button3;
    TextView textView1;
    Spinner spinner3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasabuyer_location);

        textView1 = findViewById(R.id.tvStatus1);
        button3 = findViewById(R.id.savebutton3);

        //FirebaseFirestore database = FirebaseFirestore.getInstance();
        //databaseReference = database.getNamedQuery("Value");
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(PasabuyerLocation.this);
        //databaseReference = database.getReference("Value");
        spinner3 = findViewById(R.id.UserLocation1);
        spinner3.setOnItemSelectedListener(this);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, ProfileLocation1);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner3.setAdapter(arrayAdapter);

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Intent intent = new Intent(PasabuyerLocation.this, Chatbox.class);
               // intent.putExtra("location", item);

               // startActivity(intent);
                SaveValue(item);
                PasabuyerLocation.this.finish();
            }
        });
    }

    @SuppressLint("MissingPermission")
    public void onClickSetPinnedLocation(View view){
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);

                            GeoPoint geoPoint = new GeoPoint(location.getLatitude(),location.getLongitude());
                            Map<String, Object> mapLatLng = new HashMap<>();
                            mapLatLng.put("Location", geoPoint);
                            db.collection("pasabuyLocations").document(currentUserId)
                                    .set(mapLatLng);
                            Toast.makeText(PasabuyerLocation.this, "Pinned Location Set", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

@Override
public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        item = spinner3.getSelectedItem().toString();
        textView1.setText(item);



        }

@Override
public void onNothingSelected(AdapterView<?> adapterView) {

        }

        void SaveValue(String item) {
        if (item == "Select Status") {
        Toast.makeText(this, "please select a Status", Toast.LENGTH_SHORT).show();
        } else {
        updateData(item);

        Toast.makeText(this, "Location saved", Toast.LENGTH_SHORT).show();
        }
        }

    private void updateData(String xstatus) {
        db.collection("users").document(preferenceManager.getString(Constants.KEY_USER_ID))
        .update("location", xstatus).addOnSuccessListener(new OnSuccessListener<Void>() {


        @Override
    public void onSuccess(Void unused) {
        // Toast.makeText(UserStatus.this,"Successfully Updated", Toast.LENGTH_SHORT).show();
            }
        });
    }


}