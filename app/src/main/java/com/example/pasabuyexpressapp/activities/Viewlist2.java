package com.example.pasabuyexpressapp.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.pasabuyexpressapp.R;
import com.example.pasabuyexpressapp.listeners.UserListener;
import com.example.pasabuyexpressapp.models.User;
import com.example.pasabuyexpressapp.utilities.Constants;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class Viewlist2 extends AppCompatActivity implements UserListener {


    //Firebase Var

    private DatabaseReference mDatabase;


    //android layout
    private Button btnAdd, btnView;
    private EditText etext, etext2;
    private ListView listView;

    //Array List
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter<String> adapter;


    //my add
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_USERNAME = "username";
    String spName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewlist2);

        //my add
        sharedPreferences = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        spName = sharedPreferences.getString(KEY_USERNAME, null);


        mDatabase = FirebaseDatabase.getInstance().getReference();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);

        btnAdd = (Button) findViewById(R.id.btnAdd2);
        etext = (EditText) findViewById(R.id.etFirstName2);
        etext2 = (EditText) findViewById(R.id.etFavFood2);
        btnView = (Button) findViewById(R.id.btnDone);
        listView = (ListView) findViewById(R.id.listView2);

        listView.setAdapter(adapter);


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AddItems();


            }
        });
    }

    private void AddItems() {

        String date = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date());

        String items = etext.getText().toString().trim();
        String quantity = etext2.getText().toString().trim();

        if (!TextUtils.isEmpty(items)) {
            String id = mDatabase.push().getKey();

            AddItems addItems = new AddItems(id, items, quantity, spName+"current", date);

            mDatabase.child(id).setValue(addItems);
            Toast.makeText(this, "Items Added", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "You should  input items", Toast.LENGTH_LONG).show();

        }


        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String string = dataSnapshot.getValue(String.class);

                arrayList.add(string);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                String string = dataSnapshot.getValue(String.class);

                arrayList.remove(string);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


// mDatabase.push().setValue(etext.getText().toString());
        // mDatabase.push().setValue(etext2.getText().toString());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("My Notification", "My Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }


        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Viewlist2.this, Buttons2.class);
                startActivity(intent);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(Viewlist2.this, "My Notification");
                builder.setContentTitle("Successfully checked!");
                builder.setContentText("Thank you for using Pasabuy Express!");
                builder.setSmallIcon(R.drawable.ic_notifications);
                builder.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(Viewlist2.this);
                managerCompat.notify(2, builder.build());
            }
        });
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ListViews1.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}


