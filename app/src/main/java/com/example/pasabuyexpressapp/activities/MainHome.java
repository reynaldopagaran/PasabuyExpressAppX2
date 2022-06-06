package com.example.pasabuyexpressapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pasabuyexpressapp.R;

public class MainHome extends AppCompatActivity {
   // private Button moveButton;
    private ImageButton click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        ////


        click= (ImageButton) findViewById(R.id.clickme);


        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainHome.this, UserStatus2.class);
                startActivity(intent);
            }
        });
    }

}