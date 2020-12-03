package com.awarenesskit.demo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.kit.awareness.Awareness;
import com.huawei.hms.kit.awareness.barrier.AwarenessBarrier;
import com.huawei.hms.kit.awareness.barrier.BarrierUpdateRequest;
import com.huawei.hms.kit.awareness.barrier.HeadsetBarrier;

import static com.awarenesskit.demo.HeadsetBarrierReceiver.HEADSET_BARRIER_LABEL;

public class MyService extends Service {

    private HeadsetBarrierReceiver headsetBarrierReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            notification = createCustomNotification();
        else
            notification = new Notification();

        startForeground(1234, notification);

        String barrierReceiverAction = getApplication().getPackageName() + "HEADSET_BARRIER_RECEIVER_ACTION";
        Intent intent = new Intent(barrierReceiverAction);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        headsetBarrierReceiver = new HeadsetBarrierReceiver();
        registerReceiver(headsetBarrierReceiver, new IntentFilter(barrierReceiverAction));

        AwarenessBarrier headsetBarrier = HeadsetBarrier.connecting();

        createBarrier(this, HEADSET_BARRIER_LABEL, headsetBarrier, pendingIntent);
    }

    private void createBarrier(Context context, String barrierLabel, AwarenessBarrier barrier, PendingIntent pendingIntent) {
        BarrierUpdateRequest.Builder builder = new BarrierUpdateRequest.Builder();

        BarrierUpdateRequest request = builder.addBarrier(barrierLabel, barrier, pendingIntent).build();
        Awareness.getBarrierClient(context).updateBarriers(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void void1) {
                        System.out.println("Barrier Create Success");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        System.out.println("Barrier Create Fail");
                    }
                });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(headsetBarrierReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification createCustomNotification() {
        NotificationChannel notificationChannel = new NotificationChannel("1234", "name", NotificationManager.IMPORTANCE_HIGH);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "com.awarenesskit.demo");

        return notificationBuilder
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Observing headset status")
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .build();
    }
}
