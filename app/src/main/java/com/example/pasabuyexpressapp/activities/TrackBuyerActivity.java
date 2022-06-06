package com.example.pasabuyexpressapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.pasabuyexpressapp.R;
import com.example.pasabuyexpressapp.models.User;
import com.example.pasabuyexpressapp.utilities.Constants;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class TrackBuyerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mMapView;
    private FirebaseFirestore db;
    private GoogleMap mGoogleMap;

    private User user;

    private LatLng mLatLng;
    private Marker buyerMarker;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private ListenerRegistration listenerRegistration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_buyer);

        initViewsVariables();

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(TrackBuyerActivity.this);

        mMapView.getMapAsync(this);
        mMapView.onCreate(savedInstanceState);

        getDataFromBundle();

    }
    private void initViewsVariables(){
        mMapView = findViewById(R.id.mapView_track);

        db = FirebaseFirestore.getInstance();
    }

    private void getDataFromBundle(){
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            user = (User) bundle.getSerializable("user");
            Log.d("Test_Tag", "id"+user.docId);


            getLocationUpdates();
        }
    }
    @SuppressLint("MissingPermission")
    private void showCurrentLocation(){
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if(location != null){
                            GeoPoint geoPoint = new GeoPoint(location.getLatitude(),location.getLongitude());
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,15);
                            if(mCameraUpdate != null){
                                mGoogleMap.animateCamera(mCameraUpdate);
                            }
                        }
                    }
                });
    }
    private void getLocationUpdates(){
        Query query = db.collection("users").document(user.docId)
                .collection("Location");

        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    return;
                }

                for(DocumentChange doc: value.getDocumentChanges()){
                    GeoPoint geoPoint = doc.getDocument().getGeoPoint("location");
                    Log.d("Test_Tag", "GeoPoint"+geoPoint);

                    LatLng latLng = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
                    float bearing = Float.parseFloat(doc.getDocument().getString("bearing"));

                    switch (doc.getType()){
                        case ADDED:
                        case MODIFIED:
                            if(buyerMarker != null){
                                buyerMarker.setPosition(latLng);
                                buyerMarker.setRotation(bearing);
                            }else{
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.title("Buyer");
                                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.image_buyer));
                                markerOptions.rotation(bearing);
                                markerOptions.position(latLng);
                                buyerMarker = mGoogleMap.addMarker(markerOptions);
                            }
                            break;
                    }
                }
            }
        });
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

        showCurrentLocation();
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