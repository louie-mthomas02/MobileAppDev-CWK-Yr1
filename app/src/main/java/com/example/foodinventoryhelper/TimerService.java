package com.example.foodinventoryhelper;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class TimerService extends Service {

    CountDownTimer timer;

    public TimerService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID){

        System.out.println("Service Starts");

        int timeLeft = intent.getIntExtra("timeSet", 0);

        final String CHANNEL_ID = "1";

        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, activityIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_inventory_foreground)
                .setContentTitle("Food Inventory Timer")
                .setContentText("Timer has ended!")
                .addAction(R.mipmap.ic_launcher, "Go to App", pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);


        timer = new CountDownTimer(timeLeft*1000, 1000){

            @Override
            public void onTick(long l) {
                l = Math.round(l/1000)*1000;
                Log.d("Timer", String.valueOf(l));
                updateTime(l);
            }

            @Override
            public void onFinish() {
                updateTime(0);
                notificationManager.notify(1, builder.build());
                stopSelf();
            }
        };

        timer.start();

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void updateTime(long sec) {
        Intent intent = new Intent("timeUpdate");
        intent.putExtra("timeLeft", sec/1000);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onDestroy(){
        timer.cancel();
    }

}