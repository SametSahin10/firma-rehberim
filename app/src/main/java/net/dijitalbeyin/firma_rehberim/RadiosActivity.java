package net.dijitalbeyin.firma_rehberim;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.dijitalbeyin.firma_rehberim.adapters.RadioAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RadiosActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Radio>> {
    private static final String RADIO_REQUEST_URL = "https://firmarehberim.com/sayfalar/radyo/json/radyolar_arama.php?q=";
    private static final int RADIO_LOADER_ID = 1;

    ListView lw_radios;
    RadioAdapter radioAdapter;
    TextView tv_emptyView;
    ProgressBar pb_loadingRadios;

    MediaPlayer mediaPlayer = new MediaPlayer();

    ImageView iv_radioIcon;
    TextView  tv_radioTitle;
    ImageButton ib_playPauseRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio);

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                              && activeNetwork.isConnectedOrConnecting();

        pb_loadingRadios = findViewById(R.id.pb_loadingRadios);
        lw_radios = findViewById(R.id.lw_radios);
        tv_emptyView = findViewById(R.id.tv_emptyRadioView);
        lw_radios.setEmptyView(tv_emptyView);
        radioAdapter = new RadioAdapter(this, R.layout.item_radio, new ArrayList<Radio>());
        lw_radios.setAdapter(radioAdapter);
        if (isConnected) {
            getSupportLoaderManager().initLoader(RADIO_LOADER_ID, null, this).forceLoad();
        } else {
            tv_emptyView.setText(getString(R.string.no_internet_connection_text));
            pb_loadingRadios.setVisibility(View.GONE);
        }

        //////////////////////////////////////////////////////////////////////////////
        iv_radioIcon = findViewById(R.id.iv_radio_icon);
        tv_radioTitle = findViewById(R.id.tv_radio_title);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        ib_playPauseRadio = findViewById(R.id.ib_play_radio);
        lw_radios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mediaPlayer.reset();
                }
                ib_playPauseRadio.setImageResource(R.drawable.ic_pause_radio);
                Radio radioClicked = (Radio) adapterView.getItemAtPosition(position);
                tv_radioTitle.setText(radioClicked.getRadioName());
                String streamLink = radioClicked.getStreamLink();
                try {
                    mediaPlayer.setDataSource(streamLink);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
            }
        });

        ib_playPauseRadio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    ib_playPauseRadio.setImageResource(R.drawable.ic_play_radio);
                    mediaPlayer.pause();
                } else {
                    ib_playPauseRadio.setImageResource(R.drawable.ic_pause_radio);
                    mediaPlayer.start();
                }
            }
        });
    }

    @Override
    public Loader<List<Radio>> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new RadioLoader(this, RADIO_REQUEST_URL);
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
}
