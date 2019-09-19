package net.dijitalbeyin.firma_rehberim.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import net.dijitalbeyin.firma_rehberim.R;
import net.dijitalbeyin.firma_rehberim.Radio;
import net.dijitalbeyin.firma_rehberim.data.RadioDbHelper;
import java.util.ArrayList;
import java.util.List;

import static net.dijitalbeyin.firma_rehberim.data.RadioContract.*;

public class RadioAdapter extends ArrayAdapter<Radio> {
    private static final String LOG_TAG = RadioAdapter.class.getSimpleName();
    Context context;
    int layoutResourceId;
    ArrayList<Radio> radios;
    List<Radio> permanentRadiosList;

    private final static int FILTER_TYPE_RADIO = 1;
    private final static int FILTER_TYPE_CITY = 2;
    private final static int FILTER_TYPE_CATEGORY = 3;
    private int filteringSelection;

    public void setFilteringSelection(int filteringSelection) {
        this.filteringSelection = filteringSelection;
    }

    OnAddToFavouritesListener onAddToFavouritesListener;
    OnDeleteFromFavouritesListener onDeleteFromFavouritesListener;
    View.OnClickListener onRadioIconClickListener;

    public RadioAdapter(Context context,
                        int resource,
                        ArrayList<Radio> radios,
                        OnAddToFavouritesListener onAddToFavouritesListener,
                        OnDeleteFromFavouritesListener onDeleteFromFavouritesListener,
                        View.OnClickListener onRadioIconClickListener) {
        super(context, resource, radios);
        this.context = context;
        this.layoutResourceId = resource;
        this.radios = radios;
        this.onAddToFavouritesListener = onAddToFavouritesListener;
        this.onDeleteFromFavouritesListener = onDeleteFromFavouritesListener;
        this.onRadioIconClickListener = onRadioIconClickListener;
    }

    public void setPermanentRadiosList(List<Radio> permanentRadiosList) {
        this.permanentRadiosList = permanentRadiosList;
    }

    @Override
    public int getCount() {
        return radios.size();
    }

    public List<Radio> getItems() {
        return radios;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("TAG", "position: " + position);
        View row = convertView;
        RadioHolder holder;
        final Radio currentRadio = radios.get(position);
        if (row == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            row = layoutInflater.inflate(R.layout.item_radio, parent, false);
            holder = new RadioHolder();
            holder.iv_item_radio_icon = row.findViewById(R.id.iv_item_radio_icon);
            holder.tv_radio_name = row.findViewById(R.id.tv_radio_name);
            holder.pb_buffering_radio = row.findViewById(R.id.pb_buffering_radio);
//            holder.ib_share_radio = row.findViewById(R.id.ib_share_radio);
            holder.ib_add_to_favourites = row.findViewById(R.id.ib_add_to_favourites);
            row.setTag(holder);
        } else {
            holder = (RadioHolder) row.getTag();
        }
        holder.iv_item_radio_icon.setTag(position);
        holder.iv_item_radio_icon.setOnClickListener(onRadioIconClickListener);
        String iconUrl = currentRadio.getRadioIconUrl();
        float scale = getContext().getResources().getDisplayMetrics().density;
        int width = (int) (50 * scale + 0.5f);
        int height = (int) (50 * scale + 0.5f);
        Picasso.with(row.getContext()).load(iconUrl)
                .resize(width, height)
                .centerInside()
                .placeholder(R.drawable.ic_placeholder_radio_black)
                .error(R.drawable.ic_pause_radio)
                .into(holder.iv_item_radio_icon);
        holder.tv_radio_name.setText(currentRadio.getRadioName());
        if (currentRadio.isBeingBuffered()) {
//            holder.ib_share_radio.setVisibility(View.INVISIBLE);
            holder.pb_buffering_radio.setVisibility(View.VISIBLE);
        } else {
            holder.pb_buffering_radio.setVisibility(View.INVISIBLE);
//            holder.ib_share_radio.setVisibility(View.VISIBLE);
        }
        if (currentRadio.isLiked()) {
            holder.ib_add_to_favourites.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_favourite_full));
        } else {
            holder.ib_add_to_favourites.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_favourite_empty));
        }
        //
        //Add click listener to share radio ImageButton here.
        //
//        holder.ib_share_radio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                shareRadio(currentRadio);
//            }
//        });
        holder.ib_add_to_favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentRadio.isLiked()) {
                    currentRadio.setLiked(true);
                    addToFavourites(currentRadio);
                    onAddToFavouritesListener.onAddToFavouritesClick(currentRadio.getRadioId());
                } else {
                    currentRadio.setLiked(false);
                    deleteFromFavourites(currentRadio);
                    onDeleteFromFavouritesListener.onDeleteFromFavouritesClick(currentRadio.getRadioId());
                }

                notifyDataSetChanged();
            }
        });
        return row;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Radio> filteredRadios = new ArrayList<>();
                constraint = constraint.toString().toLowerCase();
                if (constraint.length() == 0) {
                    results.count = permanentRadiosList.size();
                    results.values = permanentRadiosList;
                    return results;
                }
                switch (filteringSelection) {
                    case FILTER_TYPE_RADIO:
                        for (Radio radio: permanentRadiosList) {
                            if (radio.getRadioName().toLowerCase().contains(constraint)) {
                                filteredRadios.add(radio);
                            }
                        }
                        break;
                    case FILTER_TYPE_CITY:
                        for (Radio radio: permanentRadiosList) {
                            if (radio.getRadioName().toLowerCase().contains(constraint)) {
                                filteredRadios.add(radio);
                            }
                        }
                        break;
                    case FILTER_TYPE_CATEGORY:
                        for (Radio radio: permanentRadiosList) {
                            if (radio.getCategory().toLowerCase().contains(constraint)) {
                                filteredRadios.add(radio);
                            }
                        }
                        break;
                }
                results.count = filteredRadios.size();
                results.values = filteredRadios;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                radios = (ArrayList<Radio>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }

    private class RadioHolder {
        private ImageView iv_item_radio_icon;
        private TextView tv_radio_name;
        private ProgressBar pb_buffering_radio;
        //        private ImageButton ib_share_radio;
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

    private void shareRadio(Radio radio) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        String extraText = "I'm listening to " + radio.getRadioName() + " on " + "Firma Rehberim Radyo";
        intent.putExtra(Intent.EXTRA_TEXT, extraText);
        intent.setType("text/plain");
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public interface OnAddToFavouritesListener {
        void onAddToFavouritesClick(int radioId);
    }

    public interface OnDeleteFromFavouritesListener {
        void onDeleteFromFavouritesClick(int radioId);
    }
}