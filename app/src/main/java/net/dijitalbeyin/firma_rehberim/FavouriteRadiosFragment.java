package net.dijitalbeyin.firma_rehberim;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.p000v4.app.Fragment;
import android.support.p000v4.app.LoaderManager.LoaderCallbacks;
import android.support.p000v4.content.AsyncTaskLoader;
import android.support.p000v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import net.dijitalbeyin.firma_rehberim.adapters.RadioAdapter;

public class FavouriteRadiosFragment extends Fragment implements LoaderCallbacks<List<Radio>> {
    private static final int FAVOURITE_RADIO_LOADER_ID = 1;
    private static final String FAVOURITE_RADIO_REQUEST_URL = "https://firmarehberim.com/sayfalar/radyo/json/radyolar_favori.php";
    private static final String LOG_TAG = "FavouriteRadiosFragment";
    private ListView lw_radios;
    OnEventFromFavRadiosFragment onEventFromFavRadiosFragment;
    OnFavRadioItemClickListener onFavRadioItemClickListener;
    private ProgressBar pb_bufferingRadio;
    private ProgressBar pb_loadingRadios;
    RadioAdapter radioAdapter;
    Radio radioClicked;
    private TextView tv_emptyView;

    public interface OnEventFromFavRadiosFragment {
        void onEventFromFavRadiosFragment(int i);
    }

    public interface OnFavRadioItemClickListener {
        void onFavRadioItemClick(Radio radio);
    }

    private static class RadioLoader extends AsyncTaskLoader<List<Radio>> {
        private String requestUrl;

        public RadioLoader(@NonNull Context context, String str) {
            super(context);
            this.requestUrl = str;
        }

        public List<Radio> loadInBackground() {
            return QueryUtils.fetchFavouriteRadioData(this.requestUrl);
        }

        /* access modifiers changed from: protected */
        public void onStartLoading() {
            forceLoad();
        }
    }

    public void setOnEventFromFavRadiosFragment(OnEventFromFavRadiosFragment onEventFromFavRadiosFragment2) {
        this.onEventFromFavRadiosFragment = onEventFromFavRadiosFragment2;
    }

    public void setOnFavRadioItemClickListener(OnFavRadioItemClickListener onFavRadioItemClickListener2) {
        this.onFavRadioItemClickListener = onFavRadioItemClickListener2;
    }

    @Nullable
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        return (ViewGroup) layoutInflater.inflate(C0662R.layout.fragment_favourite_radios, viewGroup, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) getActivity().getSystemService("connectivity")).getActiveNetworkInfo();
        boolean z = activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        this.pb_loadingRadios = (ProgressBar) view.findViewById(C0662R.C0664id.pb_loadingRadios);
        this.pb_bufferingRadio = (ProgressBar) view.findViewById(C0662R.C0664id.pb_buffering_radio);
        this.lw_radios = (ListView) view.findViewById(C0662R.C0664id.lw_radios);
        this.tv_emptyView = (TextView) view.findViewById(C0662R.C0664id.tv_emptyRadioView);
        this.lw_radios.setEmptyView(this.tv_emptyView);
        RadioAdapter radioAdapter2 = new RadioAdapter(getContext(), C0662R.layout.item_radio, new ArrayList(), null, null);
        this.radioAdapter = radioAdapter2;
        this.lw_radios.setAdapter(this.radioAdapter);
        if (z) {
            getLoaderManager().initLoader(1, null, this).forceLoad();
        } else {
            Log.d("TAG", "No Network Connection");
            this.tv_emptyView.setText(getString(C0662R.string.no_internet_connection_text));
            this.pb_loadingRadios.setVisibility(8);
        }
        this.lw_radios.setAdapter(this.radioAdapter);
        if (z) {
            getLoaderManager().initLoader(1, null, this).forceLoad();
        } else {
            this.tv_emptyView.setText(getString(C0662R.string.no_internet_connection_text));
            this.pb_loadingRadios.setVisibility(8);
        }
        this.lw_radios.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (FavouriteRadiosFragment.this.radioClicked != null) {
                    FavouriteRadiosFragment.this.onFavRadioItemClickListener.onFavRadioItemClick(FavouriteRadiosFragment.this.radioClicked);
                    FavouriteRadiosFragment.this.radioClicked.setBeingBuffered(false);
                    FavouriteRadiosFragment.this.radioAdapter.notifyDataSetChanged();
                }
                FavouriteRadiosFragment.this.radioClicked = (Radio) adapterView.getItemAtPosition(i);
                FavouriteRadiosFragment.this.radioClicked.setBeingBuffered(true);
                FavouriteRadiosFragment.this.radioAdapter.notifyDataSetChanged();
            }
        });
    }

    public Loader<List<Radio>> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new RadioLoader(getContext(), FAVOURITE_RADIO_REQUEST_URL);
    }

    public void onLoadFinished(@NonNull Loader<List<Radio>> loader, List<Radio> list) {
        this.radioAdapter.setPermanentRadiosList(list);
        this.radioAdapter.clear();
        if (list != null) {
            this.radioAdapter.addAll(list);
        }
        this.tv_emptyView.setText(getString(C0662R.string.empty_radios_text));
        this.pb_loadingRadios.setVisibility(8);
    }

    public void onLoaderReset(@NonNull Loader<List<Radio>> loader) {
        this.radioAdapter.clear();
    }

    public void setCurrentRadioStatus(int i, Radio radio) {
        for (Radio radio2 : this.radioAdapter.getItems()) {
            if (radio2.getRadioId() == radio.getRadioId()) {
                String str = "TAG";
                switch (i) {
                    case 10:
                        radio2.setBeingBuffered(true);
                        this.radioAdapter.notifyDataSetChanged();
                        Log.d(str, "STATE_BUFFERING");
                        break;
                    case 11:
                        radio2.setBeingBuffered(false);
                        this.radioAdapter.notifyDataSetChanged();
                        Log.d(str, "STATE_READY");
                        break;
                    case 12:
                        radio2.setBeingBuffered(false);
                        this.radioAdapter.notifyDataSetChanged();
                        Log.d(str, "STATE_IDLE");
                        break;
                    default:
                        String str2 = LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Unknown status code: ");
                        sb.append(i);
                        Log.e(str2, sb.toString());
                        break;
                }
            }
        }
    }
}
