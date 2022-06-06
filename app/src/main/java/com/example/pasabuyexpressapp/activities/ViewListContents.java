package com.example.pasabuyexpressapp.activities;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.pasabuyexpressapp.R;
import com.example.pasabuyexpressapp.utilities.Constants;
import com.example.pasabuyexpressapp.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("ALL")
public class ViewListContents extends AppCompatActivity{

    ImageView back;
    Button done;
    DatabaseHelper myDB;
    ArrayList<User> userList = new ArrayList<>();
    ListView listView;
    User user;
    CheckBox checkBox;
    private int position;
    //mode of payment
    TextView paymentStatus;
    String j;

    TextView g;

    String name;
    String string = "";
    String email_update;
    Button save;

    TwoColumn_ListAdapter adapter;
    private PreferenceManager preferenceManager;

    //my add
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_USERNAME = "username";
    String spName;

    EditText gcashNum;

    LinearLayout main3;

    TextView dsa;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewcontents_layout);




        Intent intent1 = getIntent();
        name = intent1.getExtras().getString("nameToAddlist");

        //my add
        sharedPreferences = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        spName = sharedPreferences.getString(KEY_USERNAME, null);



        checkBox = (CheckBox) findViewById(R.id.checkbox2);
        back = (ImageView) findViewById(R.id.imageBack);
        done = findViewById(R.id.btnDone);
        listView = (ListView) findViewById(R.id.listView);
        gcashNum = findViewById(R.id.gcashNumber);
        save = findViewById(R.id.button);

        //mode of payment
        paymentStatus = (TextView) findViewById(R.id.ModeOfPaymentId2);
        preferenceManager = new PreferenceManager(getApplicationContext());

        Intent intent = getIntent();
        j = intent.getStringExtra("payment status");
        paymentStatus.setText(j);

        main3 = findViewById(R.id.main3);

        dsa = findViewById(R.id.textViewItems);

        g = findViewById(R.id.g);


        //query
        getGcashNum(spName);
        item_();
        updateStatus();


        FirebaseFirestore.getInstance()
                .collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@androidx.annotation.Nullable QuerySnapshot value, @androidx.annotation.Nullable FirebaseFirestoreException error) {
                        updateStatus();
                    }
                });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gcashNum.getText().toString().equals("")){
                    Toast.makeText(ViewListContents.this, "Please input number first", Toast.LENGTH_SHORT).show();
                }else{
                    setGcashNum(gcashNum.getText().toString());
                }

            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //   Intent intent = new Intent(ViewListContents.this, dashboard.class);
                // ViewListContents.this.startActivity(intent);


                AlertDialog.Builder builder1 = new AlertDialog.Builder(ViewListContents.this);
                builder1.setMessage("Are you sure you completed the checklist?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {


                                Intent intent = new Intent(ViewListContents.this, dashboard.class);
                                ViewListContents.this.startActivity(intent);
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(ViewListContents.this, "My Notification");
                                builder.setContentTitle("Successfully checked!");
                                builder.setContentText("Thank you for using Pasabuy Express!");
                                builder.setSmallIcon(R.drawable.ic_notifications);
                                builder.setAutoCancel(true);

                                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(ViewListContents.this);
                                managerCompat.notify(1, builder.build());



                                //update
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference mDatabaseRef = database.getReference();

                                mDatabaseRef.child("items").orderByChild("email").equalTo(email_update + "-current").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        HashMap hashMap = new HashMap();
                                        hashMap.put("email", email_update + "-old");
                                        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                            userSnapshot.getRef().updateChildren(hashMap);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                //

                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewListContents.this.finish();
            }
        });

    }


    public void setGcashNum(String gcash){
        db.collection("users").document(preferenceManager.getString(Constants.KEY_USER_ID))
                .update("gcash", gcash).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ViewListContents.this, "GCash Number Saved", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void getGcashNum(String spName) {

        db.collection("users")
                .whereEqualTo("email", spName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try{
                                    gcashNum.setText(document.getData().get("gcash").toString());
                                }catch (Exception e){
                                    Toast.makeText(ViewListContents.this, "No Gcash number saved.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        } else {

                        }
                    }
                });
    }

    public void item_(){
        db.collection("users")
                .whereEqualTo("name", name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                getItems(document.getData().get("email").toString());
                                email_update = document.getData().get("email").toString();
                            }
                        } else {

                        }
                    }
     });
    }

    public void updateStatus(){

        db.collection("users")
                .whereEqualTo("name", name)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try{
                                    paymentStatus.setText(document.getData().get("paystatus").toString());

                                   if(paymentStatus.getText().toString().equals("Online Payment")){
                                        gcashNum.setVisibility(View.VISIBLE);
                                        save.setVisibility(View.VISIBLE);
                                        g.setVisibility(View.VISIBLE);
                                    }else{
                                        gcashNum.setVisibility(View.GONE);
                                        save.setVisibility(View.GONE);
                                        g.setVisibility(View.GONE);
                                    }

                                }catch (Exception e){

                                }

                            }
                        } else {

                        }
                    }
                });
    }

    public void getItems(String string) {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference fdr = database.getReference("items");
        fdr.orderByChild("email").equalTo(string + "-current").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                updateStatus();
                userList.clear();
                int i = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    user = new User(ds.child("addItem").getValue().toString(), ds.child("addQuantity").getValue().toString(), ds.child("itemsId").getValue().toString());
                    userList.add(i, user);
                    i++;
                }
                adapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter = new TwoColumn_ListAdapter(ViewListContents.this, R.layout.list_adapter_view, userList);
        listView.setAdapter(adapter);
    }

}






