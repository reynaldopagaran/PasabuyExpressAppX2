package com.example.pasabuyexpressapp.utilities;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.pasabuyexpressapp.R;
import com.example.pasabuyexpressapp.UserStatus;
import com.example.pasabuyexpressapp.activities.UsersActivity;
import com.example.pasabuyexpressapp.models.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class LocationUpdatesService extends Service {

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    FirebaseFirestore db;
    private PreferenceManager preferenceManager;

    public static  boolean IS_ACTIVITY_RUNNING = false;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        db = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        createForegroundNotification();

        IS_ACTIVITY_RUNNING = true;

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.d("test_location", "Lat: "+locationResult.getLastLocation().getLatitude() +",Lng: "+locationResult.getLastLocation().getLongitude());

                saveLocationToDatabase(locationResult);
            }
        };
    }

    public void createForegroundNotification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel Channel = new NotificationChannel(
                    "LiveLocationForeGroundChannel",
                    "Live Location Foreground Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(Channel);
        }
    }
    public void saveLocationToDatabase(LocationResult locationResult){
        GeoPoint geoPoint = new GeoPoint(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());

        String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);

        Map<String, Object> userLocation = new HashMap<>();
        userLocation.put("location", geoPoint);
        userLocation.put("bearing",String.valueOf(locationResult.getLastLocation().getBearing()));

        db.collection("users")
                .document(currentUserId)
                .collection("Location")
                .document(currentUserId)
                .set(userLocation);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //getUserInformation();

        //Bundle extras = intent.getExtras();
        // mResQRequest = extras.getParcelable("mResQRequest");
        // Double lat = extras.getDouble("lat");
        // Double lng = extras.getDouble("long");
        // mLatLng = new LatLng(lat,lng);


       Intent locationServiceIntent = new Intent(this, UserStatus.class);
       PendingIntent pendingIntent = PendingIntent.getActivity(this, 123, locationServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT |PendingIntent.FLAG_IMMUTABLE);

        Notification foregroundNotification = new NotificationCompat.Builder(this, "LiveLocationForeGroundChannel")
                .setContentTitle("You are being tracked.")
                .setContentText("Location Updates are being sent to the service.")
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(99911, foregroundNotification);


       requestLocation();


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
        IS_ACTIVITY_RUNNING = false;
    }

    @SuppressLint("MissingPermission")
    private void requestLocation(){
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }
}
