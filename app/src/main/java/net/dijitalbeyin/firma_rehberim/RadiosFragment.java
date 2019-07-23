package net.dijitalbeyin.firma_rehberim;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.dijitalbeyin.firma_rehberim.adapters.RadioAdapter;

import java.util.ArrayList;
import java.util.List;

public class RadiosFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Radio>>,
                                                        RadioAdapter.OnAddToFavouritesListener,
                                                        RadioAdapter.OnDeleteFromFavouritesListener {
    private static final String LOG_TAG = RadiosFragment.class.getSimpleName();
    private static final String RADIO_REQUEST_URL = "https://firmarehberim.com/sayfalar/radyo/json/radyolar_arama.php?q=";
    private static final int RADIO_LOADER_ID = 1;

    OnEventFromRadiosFragmentListener onEventFromRadiosFragmentListener;
    OnRadioItemClickListener onRadioItemClickListener;

    public void setOnEventFromRadiosFragmentListener(OnEventFromRadiosFragmentListener onEventFromRadiosFragmentListener) {
        this.onEventFromRadiosFragmentListener = onEventFromRadiosFragmentListener;
    }

    public void setOnRadioItemClickListener(OnRadioItemClickListener onRadioItemClickListener) {
        this.onRadioItemClickListener = onRadioItemClickListener;
    }

    private ListView lw_radios;
    private RadioAdapter radioAdapter;
    private TextView tv_emptyView;
    private ProgressBar pb_loadingRadios;
    private ProgressBar pb_bufferingRadio;

    Radio radioClicked;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_radios, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        pb_loadingRadios = view.findViewById(R.id.pb_loadingRadios);
        pb_bufferingRadio = view.findViewById(R.id.pb_buffering_radio);
        lw_radios = view.findViewById(R.id.lw_radios);
        tv_emptyView = view.findViewById(R.id.tv_emptyRadioView);
        lw_radios.setEmptyView(tv_emptyView);
        radioAdapter = new RadioAdapter(getContext(),
                                        R.layout.item_radio,
                                        new ArrayList<Radio>(),
                this,
                this);
        lw_radios.setAdapter(radioAdapter);
        if (isConnected) {
            getLoaderManager().initLoader(RADIO_LOADER_ID, null, this).forceLoad();
        } else {
            tv_emptyView.setText(getString(R.string.no_internet_connection_text));
            pb_loadingRadios.setVisibility(View.GONE);
        }

        //////////////////////////////////////////////////////////////////////////////
        lw_radios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (radioClicked != null) {
                    radioClicked.setBeingBuffered(false);
                    radioAdapter.notifyDataSetChanged();
                }
                radioClicked = (Radio) adapterView.getItemAtPosition(position);
                radioClicked.setBeingBuffered(true);
                radioAdapter.notifyDataSetChanged();
                onRadioItemClickListener.onRadioItemClick(radioClicked);
            }
        });
    }

    @Override
    public Loader<List<Radio>> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new RadioLoader(getContext(), RADIO_REQUEST_URL);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Radio>> loader, List<Radio> radios) {
        radioAdapter.clear();
        if (radios != null) {
            radioAdapter.addAll(radios);
        }
        tv_emptyView.setText(getString(R.string.empty_radios_text));
        pb_loadingRadios.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Radio>> loader) {
        radioAdapter.clear();
    }

    private static class RadioLoader extends AsyncTaskLoader<List<Radio>> {
        private String requestUrl;

        public RadioLoader(@NonNull Context context, String requestUrl) {
            super(context);
            this.requestUrl = requestUrl;
        }

        @Override
        public List<Radio> loadInBackground() {
            ArrayList<Radio> radios = QueryUtils.fetchRadioData(requestUrl);
            return radios;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }
    }

    public void refreshRadiosList(int radioId) {
        Log.d(LOG_TAG, "radioId: " + radioId);
        List<Radio> radios = radioAdapter.getItems();
        for (Radio radio: radios) {
            if (radio.getRadioId() == radioId) {
                radio.setLiked(false);
            }
        }
        radioAdapter.notifyDataSetChanged();
    }

    public void setCurrentRadioStatus(int statusCode, Radio radioCurrentlyPlaying) {
        List<Radio> radios = radioAdapter.getItems();
        //find the currently playing radio from the radio list
        for (Radio radio: radios) {
            if (radio.getRadioId() == radioCurrentlyPlaying.getRadioId()) {
                switch (statusCode) {
                    case 10: //STATE_BUFFERING
                        radio.setBeingBuffered(true);
                        radioAdapter.notifyDataSetChanged();
                        break;
                    case 11: //STATE_READY
                        radio.setBeingBuffered(false);
                        radioAdapter.notifyDataSetChanged();
                        break;
                    case 12: //STATE_IDLE
                        radio.setBeingBuffered(false);
                        radioAdapter.notifyDataSetChanged();
                        break;
                    default:
                        Log.e(LOG_TAG, "Unknown status code: " + statusCode);
                }
            }
        }
    }

    @Override
    public void onAddToFavouritesClick(int radioId) {
        onEventFromRadiosFragmentListener.onEventFromRadiosFragment(radioId, true);
    }

    @Override
    public void onDeleteFromFavouritesClick(int radioId) {
        onEventFromRadiosFragmentListener.onEventFromRadiosFragment(radioId, false);
    }

    public interface OnEventFromRadiosFragmentListener {
        void onEventFromRadiosFragment(int radioId, boolean isLiked);
    }

    public interface OnRadioItemClickListener {
        void onRadioItemClick(Radio currentRadio);
    }
}
