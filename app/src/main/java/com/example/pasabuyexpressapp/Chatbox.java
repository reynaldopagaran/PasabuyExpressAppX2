package com.example.pasabuyexpressapp;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.pasabuyexpressapp.activities.BaseActivity;
import com.example.pasabuyexpressapp.activities.ChatActivity;
import com.example.pasabuyexpressapp.activities.SignInActivity;
import com.example.pasabuyexpressapp.activities.UsersActivity;
import com.example.pasabuyexpressapp.adapters.RecentConversationsAdapter;
import com.example.pasabuyexpressapp.databinding.ActivityChatboxBinding;
import com.example.pasabuyexpressapp.listeners.ConversionListener;
import com.example.pasabuyexpressapp.models.ChatMessage;
import com.example.pasabuyexpressapp.models.User;
import com.example.pasabuyexpressapp.utilities.Constants;
import com.example.pasabuyexpressapp.utilities.LocationUpdatesService;
import com.example.pasabuyexpressapp.utilities.PreferenceManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Chatbox extends BaseActivity implements ConversionListener {
    private ActivityChatboxBinding binding;
    private PreferenceManager preferenceManager;
    private List<ChatMessage> conversations;
    private RecentConversationsAdapter conversationsAdapter;
    private FirebaseFirestore database;
    TextView textView,tvstatus,tvUserLocation;
    DatabaseReference databaseReference;
    String x,l;

    private boolean isPermissionGranted = false;

   // ImageButton btnButtons;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatboxBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        textView = findViewById(R.id.tvStatus);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Value");
        tvstatus = findViewById(R.id.displayStatus);
        tvUserLocation = findViewById(R.id.textLocation);

        Intent intent = getIntent();
        x = intent.getStringExtra("status");
        l =  intent.getStringExtra("location");
        tvstatus.setText(x);
        //tvUserLocation.setText(l);


        init();
        loadUserDetails();
        getToken();
        setListeners();
        listenConversations();



        if(x.equals("BUYER")){
            binding.fabNewChat.setVisibility(View.INVISIBLE);
            
        }else{
            binding.fabNewChat.setVisibility(View.VISIBLE);
        }
       // toolbarszx = (Toolbar) findViewById(R.id.toolbar);

        checkPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUserDetails();

    }

    private void init() {
        conversations = new ArrayList<>();
        conversationsAdapter = new RecentConversationsAdapter(conversations, this);
        binding.conversationsRecyclerView.setAdapter(conversationsAdapter);
        database = FirebaseFirestore.getInstance();


        database.collection("users").document(preferenceManager.getString(Constants.KEY_USER_ID))
                .update("docId", preferenceManager.getString(Constants.KEY_USER_ID)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

            }
        });
    }

    private void setListeners() {
        //binding.toolbar.setOnClickListener(v -> signOut());
        binding.toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Chatbox.this, dashboard2.class);
                startActivity(intent);

            }
        });
        binding.fabNewChat.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), UsersActivity.class)));
    }

    private void loadUserDetails() {
        DocumentReference documentReference = database.collection("users").document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                tvUserLocation.setText(value.getString("location"));


                if(tvUserLocation.getText().toString().equals("OFFLINE")){
                    tvUserLocation.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                }else{
                    tvUserLocation.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_location2, 0, 0, 0);
                }
            }
        });

        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        binding.textLocation.setText(preferenceManager.getString(Constants.KEY_LOCATION ));

      //status

        //binding.textLocation.setText(preferenceManager.getString(Constants.KEY_LOCATION));

        //binding.inputStatus.setText(preferenceManager.getString(Constants.KEY_PASABUY_USER));
        //binding.textPasabuyBuyer.setText(preferenceManager.getString(Constants.KEY_PASABUY_BUYER));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);

    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void listenConversations(){
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null){
            return;
        }
        if(value != null){
            for(DocumentChange documentChange : value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED){
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.senderId = senderId;
                    chatMessage.receiverId = receiverId;
                    if (preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)){
                        chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    }else {
                        chatMessage.conversionImage = documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                        chatMessage.conversionName = documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        chatMessage.conversionId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    }
                    chatMessage.message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMessage.dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    conversations.add(chatMessage);
                }else if (documentChange.getType() == DocumentChange.Type.MODIFIED){
                    for (int i = 0; i < conversations.size(); i++){
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if (conversations.get(i).senderId.equals(senderId) && conversations.get(i).receiverId.equals(receiverId)){
                            conversations.get(i).message = documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            conversations.get(i).dateObject = documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            Collections.sort(conversations, (obj1, obj2) -> obj2.dateObject.compareTo(obj1.dateObject));
            conversationsAdapter.notifyDataSetChanged();
            binding.conversationsRecyclerView.smoothScrollToPosition(0);
            binding.conversationsRecyclerView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    };

    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token){
        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token); //PUSH NOTIFICATION
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(Constants.KEY_USER_ID)
        );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> showToast("Unable to update token"));
    }

    private void signOut() {
        showToast("Signing Out");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                })
                .addOnFailureListener(e -> showToast("Unable to sign out"));
    }

    @Override
    public void onConversionClicked(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        intent.putExtra("stat", x);
        startActivity(intent);
    }
    private boolean checkGooglePlayServices(){
        GoogleApiAvailability mGooglePlayApiAvailability = GoogleApiAvailability.getInstance();
        int result = mGooglePlayApiAvailability.isGooglePlayServicesAvailable(Chatbox.this);

        if(result == ConnectionResult.SUCCESS){
            return true;
        }else if(mGooglePlayApiAvailability.isUserResolvableError(result)){
            Dialog dialog = mGooglePlayApiAvailability.getErrorDialog(Chatbox.this, result, 1102, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    Toast.makeText(Chatbox.this, "User Canceled Operation", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        }

        return false;
    }
    private void checkPermissions(){
        Dexter.withContext(Chatbox.this).withPermission(Manifest.permission.ACCESS_FINE_LOCATION).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                isPermissionGranted = true;
                if(isPermissionGranted){
                    if(checkGooglePlayServices()){

                    }else{

                    }
                }else{

                }
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(),"");
                intent.setData(uri);
                Toast.makeText(Chatbox.this, "Please Allow Location Access", Toast.LENGTH_LONG).show();
                startActivity(intent);

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

}