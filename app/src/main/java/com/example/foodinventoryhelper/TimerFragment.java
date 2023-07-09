package com.example.foodinventoryhelper;

import static android.content.Context.NOTIFICATION_SERVICE;

import static java.lang.Thread.sleep;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.foodinventoryhelper.browseRecipes.ViewRecipeActivity;

public class TimerFragment extends Fragment {

    NotificationManager notificationManager;
    int timeLeft;
    EditText hoursET;
    EditText minET;
    EditText secET;
    Button startTimerBtn;
    Button cancelTimerBtn;

    public TimerFragment() {
        // Required empty public constructor
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            hoursET.setEnabled(false);
            minET.setEnabled(false);
            secET.setEnabled(false);

            startTimerBtn.setEnabled(false);
            cancelTimerBtn.setEnabled(true);

            timeLeft = (int) intent.getLongExtra("timeLeft", 1000);
            hoursET.setText(String.format("%02d", timeLeft / (60 * 60)));
            timeLeft %= 60*60;
            minET.setText(String.format("%02d", timeLeft / 60));
            timeLeft %= 60;
            secET.setText(String.format("%02d", timeLeft));

            if (timeLeft == 0) {

                hoursET.setEnabled(true);
                minET.setEnabled(true);
                secET.setEnabled(true);

                startTimerBtn.setEnabled(true);
                cancelTimerBtn.setEnabled(false);


            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastReceiver,
                new IntentFilter("timeUpdate"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_timer, container, false);

        startTimerBtn = (Button) v.findViewById(R.id.startTimer);
        cancelTimerBtn = (Button) v.findViewById(R.id.cancelTimer);
        hoursET = (EditText) v.findViewById(R.id.hourTV);
        minET = (EditText) v.findViewById(R.id.minTV);
        secET = (EditText) v.findViewById(R.id.secTV);


        startTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int timeSet = 0;

                int hours = Integer.parseInt(hoursET.getText().toString()) * 60 * 60;
                int min = Integer.parseInt(minET.getText().toString()) * 60;
                int sec = Integer.parseInt(secET.getText().toString());

                timeSet += hours + min + sec;

                Intent i = new Intent(getActivity(), TimerService.class);
                i.putExtra("timeSet", timeSet);
                getActivity().startService(i);

            }
        });

        cancelTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), TimerService.class);
                getActivity().stopService(i);

                hoursET.setText(String.format("%02d", 0));
                minET.setText(String.format("%02d", 0));
                secET.setText(String.format("%02d", 0));

                hoursET.setEnabled(true);
                minET.setEnabled(true);
                secET.setEnabled(true);

                startTimerBtn.setEnabled(true);
                cancelTimerBtn.setEnabled(false);
            }
        });

        // Inflate the layout for this fragment
        return v;
    }
}