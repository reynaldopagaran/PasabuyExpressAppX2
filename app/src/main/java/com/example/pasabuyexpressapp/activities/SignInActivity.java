package com.example.pasabuyexpressapp.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pasabuyexpressapp.UserStatus;
import com.example.pasabuyexpressapp.databinding.ActivitySignInBinding;
import com.example.pasabuyexpressapp.utilities.Constants;
import com.example.pasabuyexpressapp.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;
    private PreferenceManager preferenceManager;
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_USERNAME = "username";

    String isActivated;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        sharedPreferences = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String spName = sharedPreferences.getString(KEY_USERNAME, null);

        preferenceManager = new PreferenceManager(getApplicationContext());
        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), UserStatus.class);
            startActivity(intent);
            finish();

}

        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListeners();

    }

    private void setListeners() {
        binding.textCreateNewAccount.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));
        binding.btnLogin.setOnClickListener(v -> {
            if (isValidSignInDetails()) {
                signIn();
            }
        });
    }

    //String UserStatus = "null";

    private void signIn() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        loading(true);
        HashMap<String, Object> user = new HashMap<>();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0) {


                        database.collection(Constants.KEY_COLLECTION_USERS)
                                .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmail.getText().toString())
                                .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString())
                                .whereEqualTo("isActivated", true)
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful() && task.getResult() != null
                                                && task.getResult().getDocuments().size() > 0) {

                                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                                            preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                                            preferenceManager.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                                            preferenceManager.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                                            //preferenceManager.putString(Constants.KEY_PASABUYER, documentSnapshot.getString(Constants.KEY_PASABUYER));

                                            //my add
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString(KEY_USERNAME, binding.inputEmail.getText().toString());
                                            editor.apply();

                                            Intent intent = new Intent(getApplicationContext(), UserStatus.class);
                                            intent.putExtra("email",Constants.KEY_USER_ID);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);

                                        }else{
                                            loading(false);
                                            Intent intent = new Intent(getApplicationContext(), deactivated.class);
                                            startActivity(intent);
                                        }
                                    }
                                });

                    } else {
                        loading(false);
                        showToast("Unable to sign in");

                    }
                });
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.btnLogin.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.btnLogin.setVisibility(View.VISIBLE);
        }
    }


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private Boolean isValidSignInDetails() {
            if (binding.inputEmail.getText().toString().trim().isEmpty()) {
                showToast("Enter Email");
                return false;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
                showToast("Enter valid email");
                return false;
            } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
                showToast("Enter password");
                return false;
            } else {
                return true;
            }
        }
}

