package net.dijitalbeyin.firma_rehberim.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.p000v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filter.FilterResults;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import net.dijitalbeyin.firma_rehberim.C0662R;
import net.dijitalbeyin.firma_rehberim.Radio;
import net.dijitalbeyin.firma_rehberim.data.RadioContract.RadioEntry;
import net.dijitalbeyin.firma_rehberim.data.RadioDbHelper;

public class RadioAdapter extends ArrayAdapter<Radio> {
    private static final int FILTER_TYPE_CATEGORY = 3;
    private static final int FILTER_TYPE_CITY = 2;
    private static final int FILTER_TYPE_RADIO = 1;
    private static final String LOG_TAG = "RadioAdapter";
    Context context;
    /* access modifiers changed from: private */
    public int filteringSelection;
    int layoutResourceId;
    OnAddToFavouritesListener onAddToFavouritesListener;
    OnDeleteFromFavouritesListener onDeleteFromFavouritesListener;
    List<Radio> permanentRadiosList;
    ArrayList<Radio> radios;

    public interface OnAddToFavouritesListener {
        void onAddToFavouritesClick(int i);
    }

    public interface OnDeleteFromFavouritesListener {
        void onDeleteFromFavouritesClick(int i);
    }

    private class RadioHolder {
        /* access modifiers changed from: private */
        public ImageButton ib_add_to_favourites;
        /* access modifiers changed from: private */
        public ImageView iv_item_radio_icon;
        /* access modifiers changed from: private */
        public ProgressBar pb_buffering_radio;
        /* access modifiers changed from: private */
        public TextView tv_radio_name;

        private RadioHolder() {
        }
    }

    public void setFilteringSelection(int i) {
        this.filteringSelection = i;
    }

    public RadioAdapter(Context context2, int i, ArrayList<Radio> arrayList, OnAddToFavouritesListener onAddToFavouritesListener2, OnDeleteFromFavouritesListener onDeleteFromFavouritesListener2) {
        super(context2, i, arrayList);
        this.context = context2;
        this.layoutResourceId = i;
        this.radios = arrayList;
        this.onAddToFavouritesListener = onAddToFavouritesListener2;
        this.onDeleteFromFavouritesListener = onDeleteFromFavouritesListener2;
    }

    public void setPermanentRadiosList(List<Radio> list) {
        this.permanentRadiosList = list;
    }

    public int getCount() {
        return this.radios.size();
    }

    public List<Radio> getItems() {
        return this.radios;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        RadioHolder radioHolder;
        if (view == null) {
            view = LayoutInflater.from(this.context).inflate(C0662R.layout.item_radio, viewGroup, false);
            radioHolder = new RadioHolder();
            radioHolder.iv_item_radio_icon = (ImageView) view.findViewById(C0662R.C0664id.iv_item_radio_icon);
            radioHolder.tv_radio_name = (TextView) view.findViewById(C0662R.C0664id.tv_radio_name);
            radioHolder.pb_buffering_radio = (ProgressBar) view.findViewById(C0662R.C0664id.pb_buffering_radio);
            radioHolder.ib_add_to_favourites = (ImageButton) view.findViewById(C0662R.C0664id.ib_add_to_favourites);
            view.setTag(radioHolder);
        } else {
            radioHolder = (RadioHolder) view.getTag();
        }
        final Radio radio = (Radio) this.radios.get(i);
        int i2 = (int) ((getContext().getResources().getDisplayMetrics().density * 50.0f) + 0.5f);
        Picasso.with(view.getContext()).load(radio.getRadioIconUrl()).resize(i2, i2).centerInside().placeholder((int) C0662R.C0663drawable.ic_placeholder_radio_black).error((int) C0662R.C0663drawable.ic_pause_radio).into(radioHolder.iv_item_radio_icon);
        radioHolder.tv_radio_name.setText(radio.getRadioName());
        if (radio.isBeingBuffered()) {
            radioHolder.pb_buffering_radio.setVisibility(0);
        } else {
            radioHolder.pb_buffering_radio.setVisibility(4);
        }
        if (radio.isLiked()) {
            radioHolder.ib_add_to_favourites.setImageDrawable(ContextCompat.getDrawable(getContext(), C0662R.C0663drawable.ic_favourite_full));
        } else {
            radioHolder.ib_add_to_favourites.setImageDrawable(ContextCompat.getDrawable(getContext(), C0662R.C0663drawable.ic_favourite_empty));
        }
        radioHolder.ib_add_to_favourites.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (!radio.isLiked()) {
                    radio.setLiked(true);
                    RadioAdapter.this.addToFavourites(radio);
                    RadioAdapter.this.onAddToFavouritesListener.onAddToFavouritesClick(radio.getRadioId());
                } else {
                    radio.setLiked(false);
                    RadioAdapter.this.deleteFromFavourites(radio);
                    RadioAdapter.this.onDeleteFromFavouritesListener.onDeleteFromFavouritesClick(radio.getRadioId());
                }
                RadioAdapter.this.notifyDataSetChanged();
            }
        });
        return view;
    }

    public Filter getFilter() {
        return new Filter() {
            /* access modifiers changed from: protected */
            public FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                ArrayList arrayList = new ArrayList();
                String lowerCase = charSequence.toString().toLowerCase();
                if (lowerCase.length() == 0) {
                    filterResults.count = RadioAdapter.this.permanentRadiosList.size();
                    filterResults.values = RadioAdapter.this.permanentRadiosList;
                    return filterResults;
                }
                int access$700 = RadioAdapter.this.filteringSelection;
                if (access$700 == 1) {
                    for (Radio radio : RadioAdapter.this.permanentRadiosList) {
                        if (radio.getRadioName().toLowerCase().contains(lowerCase)) {
                            arrayList.add(radio);
                        }
                    }
                } else if (access$700 == 2) {
                    for (Radio radio2 : RadioAdapter.this.permanentRadiosList) {
                        if (radio2.getRadioName().toLowerCase().contains(lowerCase)) {
                            arrayList.add(radio2);
                        }
                    }
                } else if (access$700 == 3) {
                    for (Radio radio3 : RadioAdapter.this.permanentRadiosList) {
                        if (radio3.getCategory().toLowerCase().contains(lowerCase)) {
                            arrayList.add(radio3);
                        }
                    }
                }
                filterResults.count = arrayList.size();
                filterResults.values = arrayList;
                return filterResults;
            }

            /* access modifiers changed from: protected */
            public void publishResults(CharSequence charSequence, FilterResults filterResults) {
                RadioAdapter.this.radios = (ArrayList) filterResults.values;
                RadioAdapter.this.notifyDataSetChanged();
            }
        };
    }

    /* access modifiers changed from: private */
    public void addToFavourites(Radio radio) {
        SQLiteDatabase writableDatabase = new RadioDbHelper(getContext()).getWritableDatabase();
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
        new RadioDbHelper(getContext()).getWritableDatabase().delete(RadioEntry.TABLE_NAME, "name=?", strArr);
    }

    private void shareRadio(Radio radio) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        StringBuilder sb = new StringBuilder();
        sb.append("I'm listening to ");
        sb.append(radio.getRadioName());
        sb.append(" on Firma Rehberim Radyo");
        intent.putExtra("android.intent.extra.TEXT", sb.toString());
        intent.setType("text/plain");
        if (intent.resolveActivity(this.context.getPackageManager()) != null) {
            this.context.startActivity(intent);
        }
    }
}
