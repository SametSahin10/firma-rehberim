package net.dijitalbeyin.firma_rehberim.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.dijitalbeyin.firma_rehberim.FavouriteRadiosFragment;
import net.dijitalbeyin.firma_rehberim.R;
import net.dijitalbeyin.firma_rehberim.Radio;
import net.dijitalbeyin.firma_rehberim.data.RadioDbHelper;

import java.util.ArrayList;

import static net.dijitalbeyin.firma_rehberim.data.RadioContract.*;

public class RadioAdapter extends ArrayAdapter<Radio> {
    private static final String LOG_TAG = RadioAdapter.class.getSimpleName();
    Context context;
    int layoutResourceId;
    ArrayList<Radio> radios;

    OnAddToFavouriteListener onAddToFavouriteListener;

    public RadioAdapter(Context context, int resource, ArrayList<Radio> radios, OnAddToFavouriteListener onAddToFavouriteListener) {
        super(context, resource, radios);
        this.context = context;
        this.layoutResourceId = resource;
        this.radios = radios;
        this.onAddToFavouriteListener = onAddToFavouriteListener;
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
                    addToFavourites(currentRadio);
                } else {
                    currentRadio.setLiked(false);
                    deleteFromFavourites(currentRadio);
                }
                onAddToFavouriteListener.onAddToFavouriteClick();
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

    private void addToFavourites(Radio radio) {
        RadioDbHelper dbHelper = new RadioDbHelper(getContext());
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RadioEntry.COLUMN_RADIO_ID, radio.getRadioId());
        contentValues.put(RadioEntry.COLUMN_RADIO_NAME, radio.getRadioName());
        contentValues.put(RadioEntry.COLUMN_RADIO_CATEGORY, radio.getCategory());
        contentValues.put(RadioEntry.COLUMN_RADIO_ICON_URL, radio.getRadioIconUrl());
        contentValues.put(RadioEntry.COLUMN_RADIO_STREAM_LINK, radio.getStreamLink());
        contentValues.put(RadioEntry.COLUMN_RADIO_SHAREABLE_LINK, radio.getShareableLink());
        contentValues.put(RadioEntry.COLUMN_RADIO_HIT, radio.getHit());
        contentValues.put(RadioEntry.COLUMN_NUM_OF_ONLINE_LISTENERS, radio.getNumOfOnlineListeners());
        contentValues.put(RadioEntry.COLUMN_RADIO_IS_BEING_BUFFERED, radio.isBeingBuffered());
        contentValues.put(RadioEntry.COLUMN_RADIO_IS_LIKED, radio.isLiked());
        long newRowId = sqLiteDatabase.insert(RadioEntry.TABLE_NAME, null, contentValues);
        Log.d(LOG_TAG, "newRowId: " + newRowId);
    }

    private void deleteFromFavourites(Radio radio) {
        RadioDbHelper dbHelper = new RadioDbHelper(getContext());
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        String selection = RadioEntry.COLUMN_RADIO_NAME + "=?";
        String selectionArgs[] = {radio.getRadioName()};
        int numOfDeletedRows = sqLiteDatabase.delete(RadioEntry.TABLE_NAME, selection, selectionArgs);
    }

    public interface OnAddToFavouriteListener {
        void onAddToFavouriteClick();
    }
}
