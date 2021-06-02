package com.example.downloader_app_android;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

public class MyIntentService extends IntentService {

    private static final String TASK_1 = "com.example.intent_service.action.task1";

    private static final String PARAMETER_1 = "com.example.intent_service.extra.parameter1";

    private static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;

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
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //przygotujkanalpowiadomien()

    }
}
