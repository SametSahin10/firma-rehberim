package net.dijitalbeyin.firma_rehberim.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import net.dijitalbeyin.firma_rehberim.C0662R;
import net.dijitalbeyin.firma_rehberim.City;

public class CityAdapter extends ArrayAdapter<Object> {
    ArrayList<Object> cities;
    Context context;
    int layoutResourceId;

    private class CityHolder {
        /* access modifiers changed from: private */
        public TextView tv_city_name;

        private CityHolder() {
        }
    }

    public CityAdapter(Context context2, int i, ArrayList<Object> arrayList) {
        super(context2, i, arrayList);
        this.context = context2;
        this.layoutResourceId = i;
        this.cities = arrayList;
    }

    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        Log.d("TAG", "getDropDownView: ");
        return getCustomView(i, view, viewGroup);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.d("TAG", "getView: ");
        return getCustomView(i, view, viewGroup);
    }

    public View getCustomView(int i, View view, ViewGroup viewGroup) {
        CityHolder cityHolder;
        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(this.layoutResourceId, viewGroup, false);
            cityHolder = new CityHolder();
            cityHolder.tv_city_name = (TextView) view.findViewById(C0662R.C0664id.tv_city_name);
            view.setTag(cityHolder);
        } else {
            cityHolder = (CityHolder) view.getTag();
        }
        cityHolder.tv_city_name.setText(((City) this.cities.get(i)).getCityName());
        return view;
    }
}
