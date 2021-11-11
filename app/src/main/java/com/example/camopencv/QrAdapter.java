package com.example.camopencv;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QrAdapter extends RecyclerView.Adapter<QrAdapter.MyHolder> {

    Context context;
    List<StatusQr> arraylist;

    public QrAdapter(Context context, List<StatusQr> arraylist) {
        this.arraylist = arraylist;
        this.context = context;

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list, viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QrAdapter.MyHolder myHolder, int i) {
        Log.d("MainActivity", arraylist.get(i).getProduto());
        myHolder.home.setText(arraylist.get(i).getProduto() + " " + arraylist.get(i).getEndereco() +" "+ arraylist.get(i).getStatus());
    }

    @Override
    public int getItemCount() {
        return arraylist.size();
    }
    static class MyHolder extends RecyclerView.ViewHolder {
        TextView home;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            home = itemView.findViewById(R.id.tvHome);

        }
    }
}