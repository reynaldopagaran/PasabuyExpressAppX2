package com.example.pasabuyexpressapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pasabuyexpressapp.R;
import com.example.pasabuyexpressapp.adapters.UsersAdapter;
import com.example.pasabuyexpressapp.databinding.ActivityUsersPasabuyBinding;
import com.example.pasabuyexpressapp.listeners.UserListener;
import com.example.pasabuyexpressapp.models.User;
import com.example.pasabuyexpressapp.utilities.Constants;
import com.example.pasabuyexpressapp.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UsersActivityPasabuy extends BaseActivity implements UserListener {
    ImageButton btnProfile;
    private ActivityUsersPasabuyBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersPasabuyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();
        setListeners();
        getUsers();

        //btnProfile = (ImageButton) findViewById(R.id.imageProfile);

        //btnProfile.setOnClickListener(new View.OnClickListener() {
        // @Override
        //public void onClick(View view) {
        //Intent intent = new Intent(UsersActivity.this, Registration.class);
        //startActivity(intent);
    }
    private void setListeners() {
        binding.imageBack.setOnClickListener(view -> onBackPressed());
        binding.imageSignOut.setOnClickListener(v -> signOut());
    }

    private void getUsers() {
        loading(true);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo("status","PASABUYER")
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<com.example.pasabuyexpressapp.models.User> users = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserId.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            com.example.pasabuyexpressapp.models.User user = new com.example.pasabuyexpressapp.models.User();
                            user.name = queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.status = queryDocumentSnapshot.getString(Constants.STATUS);
                            //user.email = queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.location = queryDocumentSnapshot.getString(Constants.KEY_LOCATION);
                            //user.pasabuybuyer = queryDocumentSnapshot.getString(Constants.KEY_PASABUY_USER);
                            user.image = queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token = queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id = queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        if (users.size() > 0) {
                            UsersAdapter usersAdapter = new UsersAdapter(UsersActivityPasabuy.this,UsersActivityPasabuy.this,users, this);
                            binding.usersRecyclerView.setAdapter(usersAdapter);
                            binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }
                });
    }

    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "No user available"));
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    private void loadUserDetails() {
      //  binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        //binding.textPasabuyBuyer.setText(preferenceManager.getString(Constants.KEY_PASABUY_USER));
        //binding.textLocation.setText(preferenceManager.getString(Constants.KEY_LOCATION));
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
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
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                })
                .addOnFailureListener(e -> showToast("Unable to sign out"));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int item_id=item.getItemId();

        if (item_id==R.id.android){
            Toast.makeText(this, "This is android option item", Toast.LENGTH_SHORT).show();
        }
        else if (item_id==R.id.profile){
            Toast.makeText(this, "This is android profile item", Toast.LENGTH_SHORT).show();
        }
        else if (item_id==R.id.logout){
            Toast.makeText(this, "This is android download item", Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    @Override
    public void onUserClicked(User user) {
        Intent i = new Intent(this, ChatActivity.class);
        startActivityForResult(i, 1);
    }
}