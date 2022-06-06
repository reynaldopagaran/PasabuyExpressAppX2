package com.example.pasabuyexpressapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pasabuyexpressapp.R;
import com.google.android.material.card.MaterialCardView;

public class dashboard extends AppCompatActivity {
    ImageView btnAddLists, btnViewLists, btnbpayment, hist, img;
    String name;
    String stat;
    LinearLayout l_add, l_view, l_pay, H_add, H_view, H_pay;
    TextView x,v,pay,histo;
    MaterialCardView addNotice, addList, addPay, history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R .layout.activity_dashboard);

        btnAddLists = (ImageView) findViewById(R.id.Addlist);
        btnViewLists = (ImageView) findViewById(R.id.Viewlist);
        btnbpayment = (ImageView) findViewById(R.id.dpayment);
        hist = findViewById(R.id.histImg);
        histo = findViewById(R.id.Htext);
        history = findViewById(R.id.hist);


        l_pay = findViewById(R.id.pay_id);
        pay = findViewById(R.id.pay);
        addPay = findViewById(R.id.addPay);

        l_add = findViewById(R.id.l_addd);
        l_view = findViewById(R.id.l_viewd);
        x = findViewById(R.id.x);
        v = findViewById(R.id.v);
        addList = findViewById(R.id.addList);
        addNotice = findViewById(R.id.addNotice);


        Intent intent = getIntent();
        name = intent.getExtras().getString("name");


        Intent intent1 = getIntent();
        stat = intent1.getExtras().getString("stat");

        if(stat.equals("PASABUYER")){

            btnbpayment.setVisibility(View.VISIBLE);
            l_pay.setVisibility(View.VISIBLE);
            pay.setVisibility(View.VISIBLE);
            addPay.setVisibility(View.VISIBLE);

            btnAddLists.setVisibility(View.VISIBLE);
            l_add.setVisibility(View.VISIBLE);
            x.setVisibility(View.VISIBLE);
            v.setVisibility(View.GONE);
            l_view.setVisibility(View.GONE);
            btnViewLists.setVisibility(View.GONE);
            addNotice.setVisibility(View.GONE);
            addList.setVisibility(View.VISIBLE);
        }else if(stat.equals("BUYER")){

            btnbpayment.setVisibility(View.GONE);
            l_pay.setVisibility(View.GONE);
            pay.setVisibility(View.GONE);
            addPay.setVisibility(View.GONE);


            hist.setVisibility(View.GONE);
            histo.setVisibility(View.GONE);
            history.setVisibility(View.GONE);

            btnAddLists.setVisibility(View.GONE);
            v.setVisibility(View.VISIBLE);
            l_view.setVisibility(View.VISIBLE);
            l_add.setVisibility(View.GONE);
            btnViewLists.setVisibility(View.VISIBLE);
            addList.setVisibility(View.GONE);
            addNotice.setVisibility(View.VISIBLE);
        }

        btnAddLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(dashboard.this, ListViews1.class);
                intent.putExtra("namee", name);
                startActivity(intent);
            }
        });

        btnViewLists.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(dashboard.this, ViewListContents.class);
                intent.putExtra("nameToAddlist", name);
                startActivity(intent);
            }
        });

        btnbpayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(dashboard.this, PaymentMethod.class);
                intent.putExtra("namePay", name);
                startActivity(intent);
            }
        });

        hist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(dashboard.this, history.class);
                startActivity(intent);
            }
        });
    }
}