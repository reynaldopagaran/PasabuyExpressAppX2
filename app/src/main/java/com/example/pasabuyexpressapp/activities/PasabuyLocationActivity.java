package com.example.pasabuyexpressapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.pasabuyexpressapp.R;
import com.example.pasabuyexpressapp.models.User;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

public class PasabuyLocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mMapView;
    private FirebaseFirestore db;
    private GoogleMap mGoogleMap;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasabuy_location);

        initViewsVariables();

        mMapView.getMapAsync(this);
        mMapView.onCreate(savedInstanceState);
    }
    private void initViewsVariables(){
        mMapView = findViewById(R.id.mapView_pasabuy);

        db = FirebaseFirestore.getInstance();
    }
    private void getUserPinnedLocation(){
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            user = (User) bundle.getSerializable("User");
            db.collection("pasabuyLocations").document(user.docId)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        GeoPoint geoPoint = task.getResult().getGeoPoint("Location");

                        MarkerOptions markerOptions = new MarkerOptions();
                        LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                        markerOptions.title(user.name);
                        markerOptions.position(latLng);

                        mGoogleMap.addMarker(markerOptions);

                        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,15);
                        if(mCameraUpdate != null){
                            mGoogleMap.animateCamera(mCameraUpdate);
                        }
                    }
                }
            });
        }
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;


        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);

        getUserPinnedLocation();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
}