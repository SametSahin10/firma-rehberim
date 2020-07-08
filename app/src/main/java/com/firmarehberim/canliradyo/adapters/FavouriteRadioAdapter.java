package com.firmarehberim.canliradyo.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firmarehberim.canliradyo.data.RadioContract;
import com.squareup.picasso.Picasso;
import com.firmarehberim.canliradyo.R;
import com.firmarehberim.canliradyo.datamodel.Radio;
import com.firmarehberim.canliradyo.data.RadioDbHelper;
import java.util.ArrayList;
import java.util.List;

import static com.firmarehberim.canliradyo.data.RadioContract.*;

public class FavouriteRadioAdapter extends ArrayAdapter<Radio> {
    private static final String LOG_TAG = FavouriteRadioAdapter.class.getSimpleName();
    private Context context;
    private int layoutResourceId;
    private ArrayList<Radio> radios;

    OnAddToFavouritesListener onAddToFavouritesListener;
    OnDeleteFromFavouritesListener onDeleteFromFavouritesListener;
    View.OnClickListener onRadioIconClickListener;

    public FavouriteRadioAdapter(Context context,
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

    @Override
    public int getCount() {
        return radios.size();
    }

    public List<Radio> getItems() {
        return radios;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RadioHolder holder;
        final Radio currentRadio = radios.get(position);
        if (row == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            row = layoutInflater.inflate(R.layout.item_radio, parent, false);
            holder = new RadioHolder();
            holder.iv_item_radio_icon = row.findViewById(R.id.iv_item_radio_icon);
            holder.tv_radio_name = row.findViewById(R.id.tv_radio_name);
            holder.iv_playing_gif = row.findViewById(R.id.iv_playing_gif);
            holder.pb_buffering_radio = row.findViewById(R.id.pb_buffering_radio);
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
        Picasso.get().load(iconUrl)
                .resize(width, height)
                .centerInside()
                .placeholder(R.drawable.ic_placeholder_radio_black)
                .error(R.drawable.ic_pause_radio)
                .into(holder.iv_item_radio_icon);
        holder.tv_radio_name.setText(currentRadio.getRadioName());
        if (currentRadio.isPlaying()) {
            holder.iv_playing_gif.setVisibility(View.VISIBLE);
        } else {
            holder.iv_playing_gif.setVisibility(View.INVISIBLE);
        }
        if (currentRadio.isBeingBuffered()) {
            holder.iv_playing_gif.setVisibility(View.INVISIBLE);
            holder.pb_buffering_radio.setVisibility(View.VISIBLE);
        } else {
            holder.pb_buffering_radio.setVisibility(View.INVISIBLE);
        }
        holder.ib_add_to_favourites.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_favourite_full));
        holder.ib_add_to_favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radios.remove(currentRadio);
                notifyDataSetChanged();
            }
        });
        return row;
    }

    private static class RadioHolder {
        private ImageView iv_item_radio_icon;
        private TextView tv_radio_name;
        private ImageView iv_playing_gif;
        private ProgressBar pb_buffering_radio;
        private ImageButton ib_add_to_favourites;
    }

    private void addToFavourites(Radio radio) {
        RadioDbHelper dbHelper = new RadioDbHelper(getContext());
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RadioEntry.COLUMN_ID, radio.getRadioId());
        contentValues.put(RadioEntry.COLUMN_CITY_ID, radio.getCityId());
        contentValues.put(RadioEntry.COLUMN_TOWN_ID, radio.getTownId());
        contentValues.put(RadioEntry.COLUMN_NEIGHBOURHOOD_ID, radio.getNeighbourhoodId());
        contentValues.put(RadioEntry.COLUMN_ICON_URL, radio.getRadioIconUrl());
        contentValues.put(RadioEntry.COLUMN_SHAREABLE_LINK, radio.getShareableLink());
        contentValues.put(RadioEntry.COLUMN_NAME, radio.getRadioName());
        contentValues.put(RadioEntry.COLUMN_STREAM_LINK, radio.getStreamLink());
        contentValues.put(RadioEntry.COLUMN_HIT, radio.getHit());
        contentValues.put(RadioEntry.COLUMN_CATEGORY_ID, radio.getCategoryId());
        contentValues.put(RadioEntry.COLUMN_USER_ID, radio.getUserId());
        contentValues.put(RadioEntry.COLUMN_CATEGORY, radio.getCategory());
        contentValues.put(RadioEntry.COLUMN_NUM_OF_ONLINE_LISTENERS, radio.getNumOfOnlineListeners());
        contentValues.put(RadioEntry.COLUMN_IS_BEING_BUFFERED, radio.isBeingBuffered());
        contentValues.put(RadioEntry.COLUMN_IS_LIKED, radio.isLiked());
        long newRowId = sqLiteDatabase.insert(RadioEntry.TABLE_NAME, null, contentValues);
    }

    private void deleteFromFavourites(Radio radio) {
        RadioDbHelper dbHelper = new RadioDbHelper(getContext());
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        String selection = RadioEntry.COLUMN_NAME + "=?";
        String selectionArgs[] = {radio.getRadioName()};
        int numOfDeletedRows = sqLiteDatabase.delete(RadioEntry.TABLE_NAME, selection, selectionArgs);
    }

    private Cursor queryAllTheRadios(Context context) {
        RadioDbHelper dbHelper = new RadioDbHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        String[] projection = {
                RadioContract.RadioEntry._ID,
                RadioContract.RadioEntry.COLUMN_ID,
                RadioContract.RadioEntry.COLUMN_CITY_ID,
                RadioContract.RadioEntry.COLUMN_TOWN_ID,
                RadioContract.RadioEntry.COLUMN_NEIGHBOURHOOD_ID,
                RadioContract.RadioEntry.COLUMN_ICON_URL,
                RadioContract.RadioEntry.COLUMN_SHAREABLE_LINK,
                RadioContract.RadioEntry.COLUMN_NAME,
                RadioContract.RadioEntry.COLUMN_STREAM_LINK,
                RadioContract.RadioEntry.COLUMN_HIT,
                RadioContract.RadioEntry.COLUMN_CATEGORY_ID,
                RadioContract.RadioEntry.COLUMN_USER_ID,
                RadioContract.RadioEntry.COLUMN_CATEGORY,
                RadioContract.RadioEntry.COLUMN_NUM_OF_ONLINE_LISTENERS,
                RadioContract.RadioEntry.COLUMN_IS_BEING_BUFFERED,
                RadioContract.RadioEntry.COLUMN_IS_LIKED};
        Cursor cursor = sqLiteDatabase.query(RadioContract.RadioEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
        return cursor;
    }

    private List<Radio> retrieveRadiosFromCursor(Cursor cursor) {
        int idColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_ID);
        int cityIdColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_CITY_ID);
        int townIdColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_TOWN_ID);
        int neighbourhoodIdColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_NEIGHBOURHOOD_ID);
        int categoryIdColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_CATEGORY_ID);
        int userIdColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_USER_ID);
        int nameColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_NAME);
        int categoryColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_CATEGORY);
        int iconUrlColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_ICON_URL);
        int streamLinkColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_STREAM_LINK);
        int isInHLSFormatColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_IS_IN_HLS_FORMAT);
        int shareableLinkColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_SHAREABLE_LINK);
        int numOfOnlineListenersColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_NUM_OF_ONLINE_LISTENERS);
        int hitColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_HIT);
        int isBeingBufferedColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_IS_BEING_BUFFERED);
        int isLikedColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_IS_LIKED);

        List<Radio> radios = new ArrayList<>();

        while (cursor.moveToNext()) {
            int radioId = cursor.getInt(idColumnIndex);
            int cityId = cursor.getInt(cityIdColumnIndex);
            int townId = cursor.getInt(townIdColumnIndex);
            int neighbourhoodId = cursor.getInt(neighbourhoodIdColumnIndex);
            String categoryId = cursor.getString(categoryIdColumnIndex);
            int userId = cursor.getInt(userIdColumnIndex);
            String radioName = cursor.getString(nameColumnIndex);
            String category = cursor.getString(categoryColumnIndex);
            String radioIconUrl = cursor.getString(iconUrlColumnIndex);
            String streamLink = cursor.getString(streamLinkColumnIndex);
            String shareableLink = cursor.getString(shareableLinkColumnIndex);
            int hit = cursor.getInt(hitColumnIndex);
            int numOfOnlineListeners = cursor.getInt(numOfOnlineListenersColumnIndex);
            boolean isInHLSFormat = false;
            if (cursor.getInt(isInHLSFormatColumnIndex) == 1) {
                isInHLSFormat = true;
            }
            boolean isBeingBuffered = false;
            if (cursor.getInt(isBeingBufferedColumnIndex) == 1) {
                isBeingBuffered = true;
            }
            boolean isLiked = false;
            if (cursor.getInt(isLikedColumnIndex) == 1) {
                isLiked = true;
            }

            Radio radio = new Radio(radioId,
                    cityId,
                    townId,
                    neighbourhoodId,
                    radioIconUrl,
                    shareableLink,
                    radioName,
                    streamLink,
                    isInHLSFormat,
                    hit,
                    categoryId,
                    userId,
                    category,
                    numOfOnlineListeners,
                    false,
                    false,
                    false);
            radios.add(radio);
        }
        return radios;
    }

    private boolean isRadioInFavourites(Radio radio, List<Radio> favoriteRadios) {
        for (Radio element: favoriteRadios) {
            if (element.getRadioId() == radio.getRadioId()) {
                return true;
            }
        }
        return false;
    }

    public interface OnAddToFavouritesListener {
        void onAddToFavouritesClick(int radioId);
    }

    public interface OnDeleteFromFavouritesListener {
        void onDeleteFromFavouritesClick(int radioId);
    }
}