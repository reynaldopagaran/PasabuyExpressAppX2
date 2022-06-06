package com.example.pasabuyexpressapp.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pasabuyexpressapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class PaymentMethod extends AppCompatActivity {
    private ImageView Pgcash, Ppaypal, back;
    String name;
    String finalNumber="";
    Button copy;
    TextView gcaash;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        back = (ImageView) findViewById(R.id.imageBack);
        Pgcash = (ImageView) findViewById(R.id.Gcash);
        //Ppaypal = (ImageView) findViewById(R.id.Paypal);
        gcaash = findViewById(R.id.gcashNum);
        copy = findViewById(R.id.copy);

        Intent intent1 = getIntent();
        name = intent1.getExtras().getString("namePay");


        getGcashNum(name);

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finalNumber.equals("")){
                    Toast.makeText(PaymentMethod.this, "No number.", Toast.LENGTH_SHORT).show();
                }else{
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("", finalNumber);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(PaymentMethod.this, "Number copied.", Toast.LENGTH_LONG).show();

                }


            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentMethod.this, Buttons2.class);
                PaymentMethod.this.startActivity(intent);
            }
        });


        //Ppaypal.setOnClickListener(new View.OnClickListener() {
           // @Override
            //public void onClick(View view) {
                //gotoUrl("https://www.paypal.com/myaccount/summary?intl=0");
           // }
       // });

        Pgcash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://m.gcash.com/gcashapp/gcash-promotion-web/2.0.0/index.html#/?referralCode=hWlkIm1");

            }

            private void gotoUrl(String s) {
                Uri uri = Uri.parse(s);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }

        });
    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    public void getGcashNum(String name) {
        db.collection("users")
                .whereEqualTo("name", name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    gcaash.setText("Gcash Number: " + document.getData().get("gcash").toString());
                                    finalNumber = document.getData().get("gcash").toString();
                                } catch (Exception e) {
                                    // Toast.makeText(PaymentMethod.this, "Buyer has already saved number, please request one by messaging.", Toast.LENGTH_LONG).show();
                                }
                            }
                        } else {

                        }
                    }
                });
    }
}
