package com.example.pasabuyexpressapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pasabuyexpressapp.Chatbox;
import com.example.pasabuyexpressapp.R;

public class Buttons2 extends AppCompatActivity {

    Button btnButtons1, btnButtons2;
    ImageView back;
    ImageButton payment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buttons2);

        back = (ImageView) findViewById(R.id.imageBack);
        btnButtons1 = (Button) findViewById(R.id.btnAddList);
        btnButtons2 = (Button) findViewById(R.id.btnViewList);
        payment = (ImageButton) findViewById(R.id.btnpayment);

        btnButtons1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Buttons2.this, ListViews1.class);
                Buttons2.this.startActivity(intent);
            }
        });

        btnButtons2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Buttons2.this, ViewListContents.class) ;
                Buttons2.this.startActivity(intent);
           }
        });

        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Buttons2.this, PaymentMethod.class);
                startActivity(intent);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Buttons2.this, Chatbox.class);
                startActivity(intent);
            }
        });

    }
}

