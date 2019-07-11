package net.dijitalbeyin.firma_rehberim.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
            holder.iv_item_radio_icon = row.findViewById(R.id.iv_item_radio_icon);
            holder.tv_radio_name = row.findViewById(R.id.tv_radio_name);
            holder.pb_buffering_radio = row.findViewById(R.id.pb_buffering_radio);
            holder.ib_add_to_favourites = row.findViewById(R.id.ib_add_to_favourites);
            row.setTag(holder);
        } else {
            holder = (RadioHolder) row.getTag();
        }
        final Radio currentRadio = radios.get(position);
        String iconUrl = currentRadio.getRadioIconUrl();
        Picasso.with(row.getContext()).load(iconUrl)
                .resize(200, 200)
                .centerInside()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.ic_pause_radio)
                .into(holder.iv_item_radio_icon);
        holder.tv_radio_name.setText(currentRadio.getRadioName());
        if (currentRadio.isBeingBuffered()) {
            holder.pb_buffering_radio.setVisibility(View.VISIBLE);
        } else {
            holder.pb_buffering_radio.setVisibility(View.INVISIBLE);
        }
        if (currentRadio.isLiked()) {
            holder.ib_add_to_favourites.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_favourite_full));
        } else {
            holder.ib_add_to_favourites.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_favourite_empty));
        }
        holder.ib_add_to_favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentRadio.isLiked()) {
                    currentRadio.setLiked(true);
                } else {
                    currentRadio.setLiked(false);
                }
                notifyDataSetChanged();
            }
        });
        return row;
    }

    private class RadioHolder {
        private ImageView iv_item_radio_icon;
        private TextView tv_radio_name;
        private ProgressBar pb_buffering_radio;
        private ImageButton ib_add_to_favourites;
    }
}
