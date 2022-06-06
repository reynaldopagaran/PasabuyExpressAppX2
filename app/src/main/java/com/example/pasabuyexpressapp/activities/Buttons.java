package com.example.pasabuyexpressapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pasabuyexpressapp.R;
import com.example.pasabuyexpressapp.databinding.ActivityButtonsBinding;
import com.example.pasabuyexpressapp.utilities.PreferenceManager;

public class Buttons extends AppCompatActivity {

    private Button btnButtons1, btnButtons2;
    private PreferenceManager preferenceManager;
    private ActivityButtonsBinding binding;
    private Boolean isReceiverAvailable = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buttons);

        btnButtons1 = (Button) findViewById(R.id.btnBuyer);
        btnButtons2 = (Button) findViewById(R.id.btnPasabuy);

        btnButtons2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Buttons.this, UsersActivity.class);
                startActivity(intent);
            }
        });

        btnButtons1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Buttons.this, UsersActivity.class);
                Buttons.this.startActivity(intent);
            }
        });
    }
}