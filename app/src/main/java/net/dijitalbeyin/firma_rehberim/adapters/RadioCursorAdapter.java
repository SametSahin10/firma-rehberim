package net.dijitalbeyin.firma_rehberim.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import net.dijitalbeyin.firma_rehberim.C0662R;
import net.dijitalbeyin.firma_rehberim.Radio;
import net.dijitalbeyin.firma_rehberim.data.RadioContract.RadioEntry;
import net.dijitalbeyin.firma_rehberim.data.RadioDbHelper;

public class RadioCursorAdapter extends CursorAdapter {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "RadioCursorAdapter";
    Context context;
    OnRadioDeleteListener onRadioDeleteListener;

    public interface OnRadioDeleteListener {
        void onRadioDelete(int i);
    }

    public void setOnRadioDeleteListener(OnRadioDeleteListener onRadioDeleteListener2) {
        this.onRadioDeleteListener = onRadioDeleteListener2;
    }

    public RadioCursorAdapter(Context context2, Cursor cursor, OnRadioDeleteListener onRadioDeleteListener2) {
        super(context2, cursor);
        this.context = context2;
        this.onRadioDeleteListener = onRadioDeleteListener2;
    }

    public View newView(Context context2, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context2).inflate(C0662R.layout.item_radio, viewGroup, false);
    }

    public void bindView(View view, Context context2, Cursor cursor) {
        ImageButton imageButton;
        View view2 = view;
        Cursor cursor2 = cursor;
        ImageView imageView = (ImageView) view2.findViewById(C0662R.C0664id.iv_item_radio_icon);
        TextView textView = (TextView) view2.findViewById(C0662R.C0664id.tv_radio_name);
        ProgressBar progressBar = (ProgressBar) view2.findViewById(C0662R.C0664id.pb_buffering_radio);
        ImageButton imageButton2 = (ImageButton) view2.findViewById(C0662R.C0664id.ib_add_to_favourites);
        int columnIndex = cursor2.getColumnIndex("id");
        int columnIndex2 = cursor2.getColumnIndex(RadioEntry.COLUMN_CITY_ID);
        int columnIndex3 = cursor2.getColumnIndex(RadioEntry.COLUMN_TOWN_ID);
        int columnIndex4 = cursor2.getColumnIndex(RadioEntry.COLUMN_NEIGHBOURHOOD_ID);
        int columnIndex5 = cursor2.getColumnIndex(RadioEntry.COLUMN_CATEGORY_ID);
        int columnIndex6 = cursor2.getColumnIndex(RadioEntry.COLUMN_USER_ID);
        int columnIndex7 = cursor2.getColumnIndex(RadioEntry.COLUMN_RADIO_NAME);
        int columnIndex8 = cursor2.getColumnIndex(RadioEntry.COLUMN_RADIO_CATEGORY);
        int columnIndex9 = cursor2.getColumnIndex(RadioEntry.COLUMN_RADIO_ICON_URL);
        int columnIndex10 = cursor2.getColumnIndex(RadioEntry.COLUMN_RADIO_STREAM_LINK);
        int columnIndex11 = cursor2.getColumnIndex(RadioEntry.COLUMN_RADIO_SHAREABLE_LINK);
        ImageButton imageButton3 = imageButton2;
        int columnIndex12 = cursor2.getColumnIndex(RadioEntry.COLUMN_NUM_OF_ONLINE_LISTENERS);
        ProgressBar progressBar2 = progressBar;
        int columnIndex13 = cursor2.getColumnIndex(RadioEntry.COLUMN_RADIO_HIT);
        TextView textView2 = textView;
        int columnIndex14 = cursor2.getColumnIndex(RadioEntry.COLUMN_RADIO_IS_BEING_BUFFERED);
        ImageView imageView2 = imageView;
        int columnIndex15 = cursor2.getColumnIndex(RadioEntry.COLUMN_RADIO_IS_LIKED);
        int i = cursor2.getInt(columnIndex);
        int i2 = cursor2.getInt(columnIndex2);
        int i3 = cursor2.getInt(columnIndex3);
        int i4 = cursor2.getInt(columnIndex4);
        String string = cursor2.getString(columnIndex5);
        int i5 = cursor2.getInt(columnIndex6);
        String string2 = cursor2.getString(columnIndex7);
        String string3 = cursor2.getString(columnIndex8);
        String string4 = cursor2.getString(columnIndex9);
        String string5 = cursor2.getString(columnIndex10);
        String string6 = cursor2.getString(columnIndex11);
        int i6 = cursor2.getInt(columnIndex13);
        int i7 = cursor2.getInt(columnIndex12);
        int i8 = cursor2.getInt(columnIndex14);
        int i9 = cursor2.getInt(columnIndex15);
        final Radio radio = new Radio(i, string2, i2, i3, i4, string, i5, string3, string4, string5, string6, i6, i7, false, false);
        int i10 = (int) ((view.getContext().getResources().getDisplayMetrics().density * 60.0f) + 0.5f);
        Picasso.with(view.getContext()).load(radio.getRadioIconUrl()).resize(i10, i10).centerInside().placeholder((int) C0662R.mipmap.ic_launcher).error((int) C0662R.C0663drawable.ic_pause_radio).into(imageView2);
        textView2.setText(radio.getRadioName());
        if (radio.isBeingBuffered()) {
            progressBar2.setVisibility(0);
        } else {
            progressBar2.setVisibility(4);
        }
        if (radio.isLiked()) {
            imageButton = imageButton3;
            imageButton.setImageDrawable(ContextCompat.getDrawable(view.getContext(), C0662R.C0663drawable.ic_favourite_full));
        } else {
            imageButton = imageButton3;
            imageButton.setImageDrawable(ContextCompat.getDrawable(view.getContext(), C0662R.C0663drawable.ic_favourite_empty));
        }
        imageButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (!radio.isLiked()) {
                    radio.setLiked(true);
                    RadioCursorAdapter.this.addToFavourites(radio);
                } else {
                    radio.setLiked(false);
                    RadioCursorAdapter.this.deleteFromFavourites(radio);
                    RadioCursorAdapter.this.onRadioDeleteListener.onRadioDelete(radio.getRadioId());
                }
                RadioCursorAdapter.this.notifyDataSetChanged();
                Log.d(RadioCursorAdapter.LOG_TAG, "onClick ON RadioCursorAdapter: ");
            }
        });
    }

    /* access modifiers changed from: private */
    public void addToFavourites(Radio radio) {
        SQLiteDatabase writableDatabase = new RadioDbHelper(this.context).getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", Integer.valueOf(radio.getRadioId()));
        contentValues.put(RadioEntry.COLUMN_RADIO_NAME, radio.getRadioName());
        contentValues.put(RadioEntry.COLUMN_RADIO_CATEGORY, radio.getCategory());
        contentValues.put(RadioEntry.COLUMN_RADIO_ICON_URL, radio.getRadioIconUrl());
        contentValues.put(RadioEntry.COLUMN_RADIO_STREAM_LINK, radio.getStreamLink());
        contentValues.put(RadioEntry.COLUMN_RADIO_SHAREABLE_LINK, radio.getShareableLink());
        contentValues.put(RadioEntry.COLUMN_RADIO_HIT, Integer.valueOf(radio.getHit()));
        contentValues.put(RadioEntry.COLUMN_NUM_OF_ONLINE_LISTENERS, Integer.valueOf(radio.getNumOfOnlineListeners()));
        contentValues.put(RadioEntry.COLUMN_RADIO_IS_BEING_BUFFERED, Boolean.valueOf(radio.isBeingBuffered()));
        contentValues.put(RadioEntry.COLUMN_RADIO_IS_LIKED, Boolean.valueOf(radio.isLiked()));
        long insert = writableDatabase.insert(RadioEntry.TABLE_NAME, null, contentValues);
        String str = LOG_TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("newRowId: ");
        sb.append(insert);
        Log.d(str, sb.toString());
    }

    /* access modifiers changed from: private */
    public void deleteFromFavourites(Radio radio) {
        String[] strArr = {radio.getRadioName()};
        new RadioDbHelper(this.context).getWritableDatabase().delete(RadioEntry.TABLE_NAME, "name=?", strArr);
    }
}
