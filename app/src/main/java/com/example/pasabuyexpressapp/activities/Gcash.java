package com.example.pasabuyexpressapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pasabuyexpressapp.R;

public class Gcash extends AppCompatActivity {

    private Button btnButtons2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gcash);



        btnButtons2 = (Button) findViewById(R.id.btnBuyer);

        btnButtons2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSignInForm();
            }
        });



    }

    public void openSignInForm() {
        Intent intent = new Intent(this, Gcash.class);
        startActivity(intent);
    }

}
