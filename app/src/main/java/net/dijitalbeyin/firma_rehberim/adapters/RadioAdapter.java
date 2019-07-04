package net.dijitalbeyin.firma_rehberim.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import net.dijitalbeyin.firma_rehberim.R;
import net.dijitalbeyin.firma_rehberim.Radio;

import java.util.ArrayList;

public class RadioAdapter extends ArrayAdapter<Radio> {
    Context context;
    int layoutResourceId;
    ArrayList<Radio> radios;

    public RadioAdapter(Context context, int resource, ArrayList<Radio> radios) {
        super(context, resource, radios);
        this.context = context;
        this.layoutResourceId = resource;
        this.radios = radios;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RadioHolder holder;
        if (row == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            row = layoutInflater.inflate(R.layout.item_radio, parent, false);
            holder = new RadioHolder();
            holder.tv_radio_name = row.findViewById(R.id.tv_radio_name);
            row.setTag(holder);
        } else {
            holder = (RadioHolder) row.getTag();
        }
        Radio currentRadio = radios.get(position);
        holder.tv_radio_name.setText(currentRadio.getRadioName());
        return row;
    }

    private class RadioHolder {
        private TextView tv_radio_name;
    }
}
