package com.example.camopencv;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.List;

public class JsonAdapter extends BaseAdapter {

    Context context;
    List<JSONObject> arrayList;

    public JsonAdapter(Context context, List<JSONObject> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public  View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView ==  null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        }
        TextView x, y,resultado;
        Integer position1 = position;
        Log.e("MainActivity", position1.toString());
//        x = (TextView) convertView.findViewById(R.id.x);
//        y = (TextView) convertView.findViewById(R.id.y);
//        resultado = (TextView) convertView.findViewById(R.id.resultado);
//        x.setText((int) arrayList.get(position).getX());
//        y.setText((int) arrayList.get(position).getY());
//        resultado.setText(arrayList.get(position).getResultado());

        return convertView;
    }
}