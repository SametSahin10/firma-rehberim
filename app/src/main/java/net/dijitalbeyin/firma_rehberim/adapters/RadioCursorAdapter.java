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
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.dijitalbeyin.firma_rehberim.R;
import net.dijitalbeyin.firma_rehberim.Radio;
import net.dijitalbeyin.firma_rehberim.data.RadioDbHelper;

import static net.dijitalbeyin.firma_rehberim.data.RadioContract.*;

public class RadioCursorAdapter extends CursorAdapter {
    Context context;

    private static final String LOG_TAG = RadioCursorAdapter.class.getSimpleName();

    public RadioCursorAdapter(Context context, Cursor c) {
        super(context, c);
        this.context = context;
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

        int idColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_ID);
        int nameColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_NAME);
        int categoryColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_CATEGORY);
        int iconUrlColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_ICON_URL);
        int streamLinkColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_STREAM_LINK);
        int shareableLinkColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_SHAREABLE_LINK);
        int numOfOnlineListenersColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_NUM_OF_ONLINE_LISTENERS);
        int hitColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_HIT);
        int isBeingBufferedColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_IS_BEING_BUFFERED);
        int isLikedColumnIndex = cursor.getColumnIndex(RadioEntry.COLUMN_RADIO_IS_LIKED);

        Log.d("TAG", "idColumnIndex: " + idColumnIndex);
        Log.d("TAG", "nameColumnIndex: " + nameColumnIndex);
        Log.d("TAG", "hitColumnIndex: " + hitColumnIndex);

        int radioId = cursor.getInt(idColumnIndex);
        String radioName = cursor.getString(nameColumnIndex);
        String category = cursor.getString(categoryColumnIndex);
        String radioIconUrl = cursor.getString(iconUrlColumnIndex);
        String streamLink = cursor.getString(streamLinkColumnIndex);
        String shareableLink = cursor.getString(shareableLinkColumnIndex);
        int hit = cursor.getInt(hitColumnIndex);
        int numOfOnlineListeners = cursor.getInt(numOfOnlineListenersColumnIndex);
        boolean isBeingBuffered = false;
        if (cursor.getInt(isBeingBufferedColumnIndex) == 1) {
            isBeingBuffered = true;
        }
        boolean isLiked = false;
        if (cursor.getInt(isLikedColumnIndex) == 1) {
            isLiked = true;
        }

        final Radio currentRadio = new Radio(
                    radioId,
                    radioName,
                    category,
                    radioIconUrl,
                    streamLink,
                    shareableLink,
                    hit,
                    numOfOnlineListeners,
                    isBeingBuffered,
                    isLiked);

        String iconUrl = currentRadio.getRadioIconUrl();
        Picasso.with(view.getContext()).load(iconUrl)
                .resize(200, 200)
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
                }
                notifyDataSetChanged();
            }
        });
    }

    private void addToFavourites(Radio radio) {
        RadioDbHelper dbHelper = new RadioDbHelper((context));
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
        RadioDbHelper dbHelper = new RadioDbHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
        String selection = RadioEntry.COLUMN_RADIO_NAME + "=?";
        String[] selectionArgs = {radio.getRadioName()};
        int numOfDeletedRows = sqLiteDatabase.delete(RadioEntry.TABLE_NAME, selection, selectionArgs);
    }
}