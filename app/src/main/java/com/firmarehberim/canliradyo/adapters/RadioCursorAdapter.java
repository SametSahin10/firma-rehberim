package com.firmarehberim.canliradyo.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.firmarehberim.canliradyo.data.RadioContract;
import com.squareup.picasso.Picasso;

import com.firmarehberim.canliradyo.R;
import com.firmarehberim.canliradyo.datamodel.Radio;
import com.firmarehberim.canliradyo.data.RadioDbHelper;

import static com.firmarehberim.canliradyo.data.RadioContract.*;

public class RadioCursorAdapter extends CursorAdapter {
    Context context;
    OnRadioDeleteListener onRadioDeleteListener;

    public void setOnRadioDeleteListener(OnRadioDeleteListener onRadioDeleteListener) {
        this.onRadioDeleteListener = onRadioDeleteListener;
    }

    private static final String LOG_TAG = RadioCursorAdapter.class.getSimpleName();

    public RadioCursorAdapter(Context context, Cursor c, OnRadioDeleteListener onRadioDeleteListener) {
        super(context, c);
        this.context = context;
        this.onRadioDeleteListener = onRadioDeleteListener;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_radio, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView iv_item_radio_icon = view.findViewById(R.id.iv_item_radio_icon);
        TextView tv_radio_name = view.findViewById(R.id.tv_radio_name);
        ProgressBar pb_buffering_radio = view.findViewById(R.id.pb_buffering_radio);
        ImageButton ib_add_to_favourites = view.findViewById(R.id.ib_add_to_favourites);

        int idColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_ID);
        int cityIdColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_CITY_ID);
        int townIdColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_TOWN_ID);
        int neighbourhoodIdColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_NEIGHBOURHOOD_ID);
        int categoryIdColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_CATEGORY_ID);
        int userIdColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_USER_ID);
        int nameColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_NAME);
        int categoryColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_CATEGORY);
        int iconUrlColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_ICON_URL);
        int streamLinkColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_STREAM_LINK);
        int isInHLSFormatColumnIndex = cursor.getColumnIndex(RadioContract.RadioEntry.COLUMN_IS_IN_HLS_FORMAT);
        int shareableLinkColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_SHAREABLE_LINK);
        int numOfOnlineListenersColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_NUM_OF_ONLINE_LISTENERS);
        int hitColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_HIT);
        int isBeingBufferedColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_IS_BEING_BUFFERED);
        int isLikedColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_IS_LIKED);

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

        final Radio currentRadio = new Radio(radioId,
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
        String iconUrl = currentRadio.getRadioIconUrl();
        float scale = view.getContext().getResources().getDisplayMetrics().density;
        int width = (int) (60 * scale + 0.5f);
        int height = (int) (60 * scale + 0.5f);
        Picasso.get().load(iconUrl)
                .resize(width, height)
                .centerInside()
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.ic_pause_radio)
                .into(iv_item_radio_icon);
        tv_radio_name.setText(currentRadio.getRadioName());
        if (currentRadio.isBeingBuffered()) {
            pb_buffering_radio.setVisibility(View.VISIBLE);
        } else {
            pb_buffering_radio.setVisibility(View.INVISIBLE);
        }
        if (currentRadio.isLiked()) {
            ib_add_to_favourites.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_favourite_full));
        } else {
            ib_add_to_favourites.setImageDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_favourite_empty));
        }
        ib_add_to_favourites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentRadio.isLiked()) {
                    currentRadio.setLiked(true);
                    addToFavourites(currentRadio);
                } else {
                    currentRadio.setLiked(false);
                    deleteFromFavourites(currentRadio);
                    onRadioDeleteListener.onRadioDelete(currentRadio.getRadioId());
                }
                notifyDataSetChanged();
            }
        });
    }

    private void addToFavourites(Radio radio) {
        RadioDbHelper dbHelper = new RadioDbHelper((context));
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RadioEntry.COLUMN_ID, radio.getRadioId());
        contentValues.put(RadioEntry.COLUMN_NAME, radio.getRadioName());
        contentValues.put(RadioEntry.COLUMN_CATEGORY, radio.getCategory());
        contentValues.put(RadioEntry.COLUMN_ICON_URL, radio.getRadioIconUrl());
        contentValues.put(RadioEntry.COLUMN_STREAM_LINK, radio.getStreamLink());
        contentValues.put(RadioEntry.COLUMN_SHAREABLE_LINK, radio.getShareableLink());
        contentValues.put(RadioEntry.COLUMN_HIT, radio.getHit());
        contentValues.put(RadioEntry.COLUMN_NUM_OF_ONLINE_LISTENERS, radio.getNumOfOnlineListeners());
        contentValues.put(RadioEntry.COLUMN_IS_BEING_BUFFERED, radio.isBeingBuffered());
        contentValues.put(RadioEntry.COLUMN_IS_LIKED, radio.isLiked());
        long newRowId = sqLiteDatabase.insert(RadioEntry.TABLE_NAME, null, contentValues);
    }

    private void deleteFromFavourites(Radio radio) {
        RadioDbHelper dbHelper = new RadioDbHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        String selection = RadioEntry.COLUMN_NAME + "=?";
        String[] selectionArgs = {radio.getRadioName()};
        int numOfDeletedRows = sqLiteDatabase.delete(RadioEntry.TABLE_NAME, selection, selectionArgs);
    }

    public interface OnRadioDeleteListener {
        void onRadioDelete(int radioId);
    }
}