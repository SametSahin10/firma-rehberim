package net.dijitalbeyin.firma_rehberim.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.dijitalbeyin.firma_rehberim.City;
import net.dijitalbeyin.firma_rehberim.R;

import java.util.ArrayList;

public class CityAdapter extends ArrayAdapter<City> {

    Context context;
    int layoutResourceId;
    ArrayList<City> cities;

    public CityAdapter(Context context, int resource, ArrayList<City> cities) {
        super(context, resource, cities);
        this.context = context;
        layoutResourceId = resource;
        this.cities = cities;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
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
        City currentCity = cities.get(position);
        holder.tv_city_name.setText(currentCity.getCityName());
        return row;
    }

    private class CityHolder {
        private TextView tv_city_name;
    }
}
