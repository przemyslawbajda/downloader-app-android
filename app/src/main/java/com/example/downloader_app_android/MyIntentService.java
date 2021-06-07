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
import android.os.Environment;
import android.util.Log;


import androidx.annotation.Nullable;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MyIntentService extends IntentService {

    private static final String TASK_1 = "com.example.intent_service.action.task1";

    private static final String FILE_URL = "com.example.intent_service.extra.parameter1";

    private static final int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;

    private static final String CHANNEL_ID = "1"; //??

    private static final int BLOCK_SIZE = 4096;

    public static void runService(Context context, String parameter){
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(TASK_1);
        intent.putExtra(FILE_URL, parameter);
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
        startForeground(NOTIFICATION_ID, createNotification());

        //do powiadomien tu

        if(intent!=null){
            final String action = intent.getAction();
            if(TASK_1.equals(action)){
                final String param1 = intent.getStringExtra(FILE_URL);
                Log.d("intent_service", "pobieranie rozpoczyna sie");
                downloadFile(param1);
            } else{
                Log.e("intent_service", "nieznana akcja");
            }
        }
        Log.d("intent_service", "usluga wykonala zadanie");
    }

    private void prepareNotificationChannel(){
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = getString(R.string.app_name);

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW);

            notificationManager.createNotificationChannel(channel);
        }
    }

    private Notification createNotification(){
        //dokonczyc!!!!
        Intent notificationIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        stackBuilder.addNextIntentWithParentStack(notificationIntent);
        PendingIntent waitingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notificationBuilder = new Notification.Builder(this);
        notificationBuilder.setContentTitle(getString(R.string.notification_title))
                .setProgress(100,1, false) // tu jakas wartoscPostepu()
                .setContentIntent(waitingIntent)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_HIGH);

        if(true){ //!!!!!
            notificationBuilder.setOngoing(false);
        }else{
            notificationBuilder.setOngoing(true);
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationBuilder.setChannelId(CHANNEL_ID);
        }

        return notificationBuilder.build();
    }


    private void downloadFile(String urlString){
        FileOutputStream streamToFile=null;
        InputStream streamFromWeb=null;
        HttpsURLConnection connection = null;
        int bytesDownloaded = 0;

        try{
            URL url = new URL(urlString);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            Log.d("intent", "nawiazanie polaczenia pomyslne");

            File tempFile = new File(url.getFile());
            File outFile = new File(
                    Environment.getExternalStorageDirectory()+
                    File.separator+ tempFile.getName()
            );

            if(outFile.exists()) tempFile.delete();

            DataInputStream reader = new DataInputStream(connection.getInputStream());
            streamToFile = new FileOutputStream(outFile.getPath());
            byte bufor[] = new byte[BLOCK_SIZE];
            int downloaded = reader.read(bufor,0, BLOCK_SIZE);

            while(downloaded != -1){
                Log.d("intent", "pobieranie...");
                streamToFile.write(bufor,0,downloaded);
                bytesDownloaded += downloaded;
                downloaded = reader.read(bufor,0,BLOCK_SIZE);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        finally {
            if(streamFromWeb!=null){
                try{
                    streamFromWeb.close();
                }catch (IOException e){
                    e.printStackTrace();
                }

            }

            if(streamToFile!=null){
                try{
                    streamToFile.close();
                }catch (IOException e){
                    e.printStackTrace();
                }

            }

            if(connection != null) connection.disconnect();
            Log.d("intent", "pobieranie zakonczone");
        }

    }
}
