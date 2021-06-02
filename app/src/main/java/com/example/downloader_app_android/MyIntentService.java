package com.example.downloader_app_android;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;


import androidx.annotation.Nullable;

public class MyIntentService extends IntentService {

    private static final String TASK_1 = "com.example.intent_service.action.task1";

    private static final String PARAMETER_1 = "com.example.intent_service.extra.parameter1";

    private static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;

    private static final String CHANNEL_ID = "1"; //??

    public static void runService(Context context, int parameter){
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(TASK_1);
        intent.putExtra(PARAMETER_1, parameter);
        context.startService(intent);

    }


    public MyIntentService() {
        super("MyIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //DOKONCZYC
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        prepareNotificationChannel();
    }

    private void prepareNotificationChannel(){
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.0){
            CharSequence name = getString(R.string.app_name);

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW);

            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification(){
        //dokonczyc!!!!
        Intent notificationIntent = new Intent(this, MyIntentService.class);
        //notificationIntent.putExtra();

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MyIntentService.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent waitingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notificationBuilder = new Notification.Builder(this);
        notificationBuilder.setContentTitle(getString(R.string.notification_title))
                .setProgress(100,1, false) // tu jakas wartoscPostepu()
                .setContentIntent(waitingIntent)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_HIGH);

        if(/*jezeli pobieranie trwa*/){
            notificationBuilder.setOngoing(false);
        }else{
            notificationBuilder.setOngoing(true);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.0){
            notificationBuilder.setChannelId(CHANNEL_ID);
        }

        return notificationBuilder.build();
    }
}
