package com.example.pasabuyexpressapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pasabuyexpressapp.R;
import com.example.pasabuyexpressapp.Status;
import com.example.pasabuyexpressapp.utilities.Constants;
import com.example.pasabuyexpressapp.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class   ListViews1 extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //
    private DatabaseReference mDatabase;
    //
    ArrayList<User> userList = new ArrayList<>();
    ListView listView;
    User user;

    //my add
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_USERNAME = "username";
    String spName;


    ImageView back;
    EditText etFirstName, etFavFood;
    DatabaseHelper myDB;
    //mode of payment
    Button btnAdd, btnView;
    TextView ModeOfPayment;
    Spinner spinner;
    DatabaseReference databaseReference;
    String item;
    Status status;
    TextView name;
    String[] paymentstatus = {"Cash On Delivery", "Online Payment"};
    String email;
    FirebaseAuth auth;
    private PreferenceManager preferenceManager;
    TwoColumn_ListAdapter adapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //
    String namee;
    String nameToAddList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_views1);
        //
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //

        //my add
        sharedPreferences = this.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        spName = sharedPreferences.getString(KEY_USERNAME, null);


        etFavFood = (EditText) findViewById(R.id.etFavFood);
        etFirstName = (EditText) findViewById(R.id.etFirstName);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnView = (Button) findViewById(R.id.btnDone);
        back = (ImageView) findViewById(R.id.imageBack);
        myDB = new DatabaseHelper(this);
        //mode of payment
        name = (TextView) findViewById(R.id.ModeOfPaymentId);
        ModeOfPayment = (TextView) findViewById(R.id.ModeOfPaymentId);
        spinner = (Spinner) findViewById(R.id.spinnerId);

        //FirebaseFirestore database = FirebaseFirestore.getInstance();
        //databaseReference = database.getNamedQuery("Value");
        //FirebaseDatabase database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        preferenceManager = new PreferenceManager(getApplicationContext());
        //databaseReference = database.getReference("Value");
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, paymentstatus);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);


        getItems(spName);

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ModeOfPayment.equals("--")) {
                    Toast.makeText(ListViews1.this, "please select a status", Toast.LENGTH_SHORT).show();
                } else {
                    updateData(item);
                    Toast.makeText(ListViews1.this, "Items saved", Toast.LENGTH_SHORT).show();
                    ListViews1.this.finish();

                }
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int which_item = position;

                new AlertDialog.Builder(ListViews1.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete this item")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String s = userList.get(position).getId();

                                //firebase delete
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                                Query applesQuery = ref.child("items").orderByChild("itemsId").equalTo(s);
                                applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                                            appleSnapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Toast.makeText(ListViews1.this, "Cancelled.", Toast.LENGTH_LONG).show();
                                    }
                                });
                                //

                                userList.remove(which_item);
                                adapter.notifyDataSetChanged();

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return false;
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ListViews1.this.finish();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fName = etFirstName.getText().toString();
                String fFood = etFavFood.getText().toString();

                if (fName.length() != 0 && fFood.length() != 0) {

                    AddData(fName, fFood);
                    etFavFood.setText("");
                    etFirstName.setText("");


                } else {
                    Toast.makeText(ListViews1.this, "You must put something in the text field!", Toast.LENGTH_LONG).show();
                }

            }
        });




    }


    public void AddData(String firstName, String favFood) {

        String date = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date());
        databaseReference = FirebaseDatabase.getInstance().getReference().child("items");
        boolean insertData = myDB.addData(firstName, favFood);
        TwoColumn_ListAdapter adapter = new TwoColumn_ListAdapter(ListViews1.this, R.layout.list_adapter_view2, userList);
        listView = (ListView) findViewById(R.id.listView1);
        listView.setAdapter(adapter);
        if (!TextUtils.isEmpty(firstName)) {

            String id = mDatabase.push().getKey();
            AddItems addItems = new AddItems(id, firstName, favFood, spName + "-current", date);
            databaseReference.push().setValue(addItems).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(ListViews1.this, "Successfully Entered Data!", Toast.LENGTH_LONG).show();
                    getItems(spName);
                }
            });
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        item = spinner.getSelectedItem().toString();
        ModeOfPayment.setText(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void updateData(String pstatus) {
        db.collection("users").document(preferenceManager.getString(Constants.KEY_USER_ID))
                .update("paystatus", pstatus).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                });
    }

    public void getItems(String string) {

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference fdr = database.getReference("items");

        fdr.orderByChild("email").equalTo(string + "-current").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                int i = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    user = new User(ds.child("addItem").getValue().toString(), ds.child("addQuantity").getValue().toString(), ds.child("itemsId").getValue().toString());
                    userList.add(i, user);
                    i++;
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new TwoColumn_ListAdapter(ListViews1.this, R.layout.list_adapter_view2, userList);
        listView = (ListView) findViewById(R.id.listView1);
        listView.setAdapter(adapter);

    }
}