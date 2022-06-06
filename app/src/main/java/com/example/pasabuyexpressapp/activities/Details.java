package com.example.pasabuyexpressapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pasabuyexpressapp.R;

public class Details extends AppCompatActivity {

    ImageView pasabuyDetails, buyerDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        pasabuyDetails = (ImageView) findViewById(R.id.Addlist);
        buyerDetails = (ImageView) findViewById(R.id.Viewlist);

        pasabuyDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Details.this, ListViews1.class);
                startActivity(intent);
            }
        });

        buyerDetails .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Details.this, ViewListContents.class);
                startActivity(intent);
            }
        });
    }
}