package com.example.pasabuyexpressapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.example.pasabuyexpressapp.R;

public class UserStatus2 extends AppCompatActivity {
    Button btnskip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_status2);

        btnskip = (Button) findViewById(R.id.skip);


        btnskip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserStatus2.this, SignInActivity.class);
                startActivity(intent);
            }
        });

    }
}