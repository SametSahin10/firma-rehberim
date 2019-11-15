package com.firmarehberim.canliradyo.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.firmarehberim.canliradyo.datamodel.City;
import com.firmarehberim.canliradyo.R;

import java.util.ArrayList;

public class CityAdapter extends ArrayAdapter<Object> {

    Context context;
    int layoutResourceId;
    ArrayList<Object> cities;

    public CityAdapter(Context context, int resource, ArrayList<Object> cities) {
        super(context, resource, cities);
        this.context = context;
        layoutResourceId = resource;
        this.cities = cities;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        Log.d("TAG", "getDropDownView: ");
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("TAG", "getView: ");
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CityHolder holder;
        if (row == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            row = layoutInflater.inflate(layoutResourceId, parent, false);
            holder = new CityHolder();
            holder.tv_city_name = row.findViewById(R.id.tv_city_name);
            row.setTag(holder);
        } else {
            holder = (CityHolder) row.getTag();
        }
        City currentCity = (City) cities.get(position);
        holder.tv_city_name.setText(currentCity.getCityName());
        return row;
    }

//    public View getDefaultValue(int position, View convertView, ViewGroup parent) {
//        View row = convertView;
//        CityHolder holder;
//        if (row == null) {
//            LayoutInflater layoutInflater = LayoutInflater.from(context);
//            row = layoutInflater.inflate(layoutResourceId, parent, false);
//            holder = new CityHolder();
//            holder.tv_city_name = row.findViewById(R.id.tv_city_name);
//            row.setTag(holder);
//        } else {
//            holder = (CityHolder) row.getTag();
//        }
//        City currentCity = (City) cities.get(position);
//        holder.tv_city_name.setText(currentCity.getCityName());
//        return row;
//    }

    private class CityHolder {
        private TextView tv_city_name;
    }
}