package com.firmarehberim.canliradyo.fragments;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firmarehberim.canliradyo.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimerFragment extends Fragment {
    private final static String LOG_TAG = TimerFragment.class.getSimpleName();

    ViewGroup rootView;

    TimePicker timePicker;
    Button btn_set_timer;
    Button btn_cancel_timer;
    TextView tv_timer_question;
    TextView tv_remaining_time;
    TextView tv_remaining_time_desc;
    CountDownTimer countDownTimer = null;

    OnCountdownFinishedListener onCountdownFinishedListener;

    private boolean isInflated;

    public void setOnCountdownFinishedListener(OnCountdownFinishedListener onCountdownFinishedListener) {
        this.onCountdownFinishedListener = onCountdownFinishedListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (!isInflated) {
            rootView = (ViewGroup) inflater.inflate(R.layout.fragment_timer,
                    container,
                    false);
            isInflated = true;
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        timePicker = view.findViewById(R.id.time_picker);
        timePicker.setIs24HourView(true);
        tv_timer_question = view.findViewById(R.id.tv_timer_question);
        tv_remaining_time_desc = view.findViewById(R.id.tv_remaining_time_desc);
        tv_remaining_time = view.findViewById(R.id.tv_remaining_time);
        btn_cancel_timer = view.findViewById(R.id.btn_cancel_timer);
        btn_set_timer = view.findViewById(R.id.btn_set_timer);
        btn_set_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedHour, selectedMinute;
                if (Build.VERSION.SDK_INT >= 23) {
                    selectedHour = timePicker.getHour();
                    selectedMinute = timePicker.getMinute();
                } else {
                    selectedHour = timePicker.getCurrentHour();
                    selectedMinute = timePicker.getCurrentMinute();
                }
                DateFormat dateFormat = new SimpleDateFormat("HH:mm");
                String currentTime = dateFormat.format(Calendar.getInstance().getTime());
                String[] timeAsArray = currentTime.split(":");
                int currentHour = Integer.valueOf(timeAsArray[0]);
                int currentMinute = Integer.valueOf(timeAsArray[1]);
                long countDownInterval = ((selectedHour - currentHour)*3600000) + ((selectedMinute - currentMinute)*60000);
                if (countDownTimer == null) {
                    countDownTimer = new CountDownTimer(countDownInterval, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            DateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                            Date remainingTimeAsDate = new Date(millisUntilFinished);
                            tv_remaining_time.setText(simpleDateFormat.format(remainingTimeAsDate));
                        }

                        @Override
                        public void onFinish() {
                            //TODO: Stop radio playing
                            onCountdownFinishedListener.onCountDownFinished();
                        }
                    };
                    countDownTimer.start();
                } else {
                    countDownTimer.cancel();
                    countDownTimer = null;
                }

                tv_timer_question.setVisibility(View.INVISIBLE);
                timePicker.setVisibility(View.INVISIBLE);
                btn_set_timer.setVisibility(View.INVISIBLE);
                tv_remaining_time_desc.setVisibility(View.VISIBLE);
                tv_remaining_time.setVisibility(View.VISIBLE);
                btn_cancel_timer.setVisibility(View.VISIBLE);
            }
        });

        btn_cancel_timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Zamanlayıcı iptal edildi", Toast.LENGTH_SHORT).show();
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                tv_timer_question.setVisibility(View.VISIBLE);
                timePicker.setVisibility(View.VISIBLE);
                btn_set_timer.setVisibility(View.VISIBLE);
                tv_remaining_time_desc.setVisibility(View.INVISIBLE);
                tv_remaining_time.setVisibility(View.INVISIBLE);
                btn_cancel_timer.setVisibility(View.INVISIBLE);
            }
        });
    }

    public interface OnCountdownFinishedListener {
        void onCountDownFinished();
    }
}
