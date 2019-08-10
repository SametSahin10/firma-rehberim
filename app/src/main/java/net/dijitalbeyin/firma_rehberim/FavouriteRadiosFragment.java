package net.dijitalbeyin.firma_rehberim;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import net.dijitalbeyin.firma_rehberim.adapters.RadioCursorAdapter;
import net.dijitalbeyin.firma_rehberim.data.RadioDbHelper;

import static net.dijitalbeyin.firma_rehberim.data.RadioContract.*;

public class FavouriteRadiosFragment extends Fragment implements RadioCursorAdapter.OnRadioDeleteListener {
    private static final String LOG_TAG = FavouriteRadiosFragment.class.getSimpleName();

    OnEventFromFavRadiosFragment onEventFromFavRadiosFragment;
    OnFavRadioItemClickListener onFavRadioItemClickListener;

    public void setOnEventFromFavRadiosFragment(OnEventFromFavRadiosFragment onEventFromFavRadiosFragment) {
        this.onEventFromFavRadiosFragment = onEventFromFavRadiosFragment;
    }

    public void setOnFavRadioItemClickListener(OnFavRadioItemClickListener onFavRadioItemClickListener) {
        this.onFavRadioItemClickListener = onFavRadioItemClickListener;
    }

    RadioDbHelper dbHelper;

    private ListView lw_radios;
    private RadioCursorAdapter radioCursorAdapter;
    private TextView tv_emptyView;

    Radio radioClicked;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_favourite_radios, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        lw_radios = view.findViewById(R.id.lw_radios);
        tv_emptyView = view.findViewById(R.id.tv_emptyRadioView);
        lw_radios.setEmptyView(tv_emptyView);
        final Cursor cursor = queryAllTheRadios(getContext());
        radioCursorAdapter = new RadioCursorAdapter(getContext(), cursor, this);
        lw_radios.setAdapter(radioCursorAdapter);
//        if (isConnected) {
////            getLoaderManager().initLoader(RADIO_LOADER_ID, null, this).forceLoad();
//            //Use Cursor Loader instead
//        } else {
//            tv_emptyView.setText(getString(R.string.no_internet_connection_text));
//            pb_loadingRadios.setVisibility(View.GONE);
//        }
//        Will implement this later.

        //////////////////////////////////////////////////////////////////////////////
        lw_radios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (radioClicked != null) {
                    onFavRadioItemClickListener.onFavRadioItemClick(radioClicked);
                    radioClicked.setBeingBuffered(false);
                    radioCursorAdapter.notifyDataSetChanged();
                }
                Cursor radioCursor = (Cursor) adapterView.getItemAtPosition(position);
                Radio radioFromCursor = retireveRadioFromCursor(radioCursor, position);
                radioClicked = radioFromCursor;
                radioClicked.setBeingBuffered(true);
            }
        });
    }

    public Cursor queryAllTheRadios(Context context) {
        dbHelper = new RadioDbHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        String[] projection = {
                RadioEntry._ID,
                RadioEntry.COLUMN_RADIO_ID,
                RadioEntry.COLUMN_RADIO_NAME,
                RadioEntry.COLUMN_RADIO_CATEGORY,
                RadioEntry.COLUMN_RADIO_ICON_URL,
                RadioEntry.COLUMN_RADIO_STREAM_LINK,
                RadioEntry.COLUMN_RADIO_SHAREABLE_LINK,
                RadioEntry.COLUMN_RADIO_HIT,
                RadioEntry.COLUMN_NUM_OF_ONLINE_LISTENERS,
                RadioEntry.COLUMN_RADIO_IS_BEING_BUFFERED,
                RadioEntry.COLUMN_RADIO_IS_LIKED};
        Cursor cursor = sqLiteDatabase.query(RadioEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);
        return cursor;
    }

    private Radio retireveRadioFromCursor(Cursor cursor, int position) {
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

        cursor.moveToPosition(position);
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

        Radio radio = new Radio(
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
        return radio;
    }

    protected void updateFavouriteRadiosList() {
        Cursor cursor = queryAllTheRadios(getContext());
        radioCursorAdapter.swapCursor(cursor);
    }

    public void setCurrentRadioStatus(int statusCode, Radio radioCurrentlyPlaying) {
        Cursor cursor = queryAllTheRadios(getContext());
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
        while (cursor.moveToNext()) {
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
            Radio radio = new Radio(
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
            if (radio.getRadioId() == radioCurrentlyPlaying.getRadioId()) {
                radioClicked = radio;
            }
        }
        radioCursorAdapter.notifyDataSetChanged();
        switch (statusCode) {
            case 10: //STATE_BUFFERING
                radioClicked.setBeingBuffered(true);
                radioCursorAdapter.notifyDataSetChanged();
                Log.d("TAG", "STATE_BUFFERING");
                break;
            case 11: //STATE_READY
                radioClicked.setBeingBuffered(false);
                radioCursorAdapter.notifyDataSetChanged();
                Log.d("TAG", "STATE_READY");
                break;
            case 12: //STATE_IDLE
                radioClicked.setBeingBuffered(false);
                radioCursorAdapter.notifyDataSetChanged();
                Log.d("TAG", "STATE_IDLE");
                break;
            default:
                Log.e(LOG_TAG, "Unknown status code: " + statusCode);
        }
    }

    @Override
    public void onRadioDelete(int radioId) {
        updateFavouriteRadiosList();
        onEventFromFavRadiosFragment.onEventFromFavRadiosFragment(radioId);
    }

    public interface OnEventFromFavRadiosFragment {
        void onEventFromFavRadiosFragment(int radioId);
    }

    public interface OnFavRadioItemClickListener {
        void onFavRadioItemClick(Radio currentFavRadio);
    }
}
