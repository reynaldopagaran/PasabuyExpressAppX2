package com.example.pasabuyexpressapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pasabuyexpressapp.Chatbox;
import com.example.pasabuyexpressapp.R;
import com.example.pasabuyexpressapp.databinding.ActivityNotifyBuyerBinding;
import com.example.pasabuyexpressapp.utilities.Constants;
import com.example.pasabuyexpressapp.utilities.PreferenceManager;

public class NotifyBuyerActivity extends AppCompatActivity {

    private Button btnAccept, btnDecline;

    private ActivityNotifyBuyerBinding binding;
    private PreferenceManager preferenceManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotifyBuyerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        loadUserDetails();

        btnAccept = (Button) findViewById(R.id.btnAccept);
        btnDecline = (Button) findViewById(R.id.btnDecline);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignInForm();
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotifyBuyerActivity.this, Chatbox.class);
                NotifyBuyerActivity.this.startActivity(intent);
            }
        });

        }

    private void loadUserDetails() {
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.textLocation.setText(preferenceManager.getString(Constants.KEY_LOCATION));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);

    };

    public void openSignInForm() {
        Intent intent = new Intent(NotifyBuyerActivity.this, UsersActivity.class);
        startActivity(intent);

    }
}