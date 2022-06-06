package com.example.pasabuyexpressapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pasabuyexpressapp.activities.TrackBuyerActivity;
import com.example.pasabuyexpressapp.databinding.ItemContainerUserBinding;
import com.example.pasabuyexpressapp.listeners.UserListener;
import com.example.pasabuyexpressapp.models.User;

import java.util.List;

public class  UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder>{


    private final List<User> users;
    private final UserListener userListener;
    private Context context;
    private Activity activity;

    public UsersAdapter(Activity activity, Context context, List<User> users, UserListener userListener) {
        this.users = users;
        this.userListener = userListener;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding = ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.UserViewHolder holder, int position) {
        holder.setUserData(users.get(position));

        User mUser = users.get(position);
        
        holder.binding.buyerLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TrackBuyerActivity.class);
                intent.putExtra("user",mUser);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        ItemContainerUserBinding binding;
        UserViewHolder(ItemContainerUserBinding itemContainerUserBinding){
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;

        }

        void setUserData(User user){
            binding.textName.setText(user.name);
            binding.displayStatus2.setText(user.status);
            binding.buyerLocation.setText(user.location);

            if(binding.buyerLocation.getText().toString().equals("OFFLINE")){
                binding.buyerLocation.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

            //binding.textPasabuyBuyer.setText(user.pasabuybuyer);
            binding.imageProfile.setImageBitmap(getUserImage(user.image));
            binding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user));
        }
    }

    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}


