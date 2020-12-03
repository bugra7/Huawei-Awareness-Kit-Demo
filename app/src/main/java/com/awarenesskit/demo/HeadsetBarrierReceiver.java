package com.awarenesskit.demo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.huawei.hms.kit.awareness.barrier.BarrierStatus;

public class HeadsetBarrierReceiver extends BroadcastReceiver {

    public static final String HEADSET_BARRIER_LABEL = "HEADSET_BARRIER_LABEL";

    @Override
    public void onReceive(Context context, Intent intent) {
        BarrierStatus barrierStatus = BarrierStatus.extract(intent);
        String barrierLabel = barrierStatus.getBarrierLabel();
        int barrierPresentStatus = barrierStatus.getPresentStatus();

        if (HEADSET_BARRIER_LABEL.equals(barrierLabel)) {
            if (barrierPresentStatus == BarrierStatus.TRUE) {
                System.out.println("The headset is connected.");

                createNotification(context);
            }
            else if (barrierPresentStatus == BarrierStatus.FALSE) {
                System.out.println("The headset is disconnected.");
            }
        }
    }

    private void createNotification(Context context) {
        // Create PendingIntent to make user open the application when clicking on the notification
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1234, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "channelId")
                .setSmallIcon(R.drawable.ic_headset)
                .setContentTitle("Cool Headset!")
                .setContentText("Want to listen to some music ?")
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("channelId", "ChannelName", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(1234, notificationBuilder.build());
    }
}
