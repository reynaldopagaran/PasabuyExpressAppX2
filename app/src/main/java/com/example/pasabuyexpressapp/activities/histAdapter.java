package com.example.pasabuyexpressapp.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pasabuyexpressapp.R;

import java.util.List;

public class histAdapter extends RecyclerView.Adapter<histAdapter.ViewHolder> {

    Context context;
    private List<historyData> myData;

    public histAdapter(Context context, List<historyData> myData){
        this.context = context;
        this.myData = myData;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull histAdapter.ViewHolder holder, int position) {
        holder._item.setText("Item: " + myData.get(position).getItem());
        holder._qty.setText("Quantity: " + myData.get(position).getQty());
        holder._date.setText("Date: " + myData.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return myData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView _item;
        TextView _qty;
        TextView _date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            _item = itemView.findViewById(R.id. _item);
            _qty = itemView.findViewById(R.id._qty);
            _date = itemView.findViewById(R.id._date);

        }
    }
}
