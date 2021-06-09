package com.example.downloader_app_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private FileInfo fileInfo = new FileInfo();
    private EditText addressUrl;
    private TextView fileSize;
    private TextView fileType;
    private TextView downloadedBytesNumber;
    private Button getInfoButton;
    private Button downloadButton;
    private ProgressBar progressBar;

    public static final String FILE_TYPE = "FILE_TYPE";
    public static final String FILE_SIZE = "FILE_SIZE";
    public static final String DOWNLOADED_BYTES = "DOWNLOADED_BYTES";
    public static final String URL = "URL";


    private final int CODE_WRITE_EXTERNAL_STORAGE = 1;

    //receive broadcast message
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            ProgressInfo progressInfo = bundle.getParcelable(MyIntentService.INFO);
            progressBar.setMax(progressInfo.getFileSize());

            //sets information on the screen depends on status
            switch (progressInfo.getStatus()){
                case ProgressInfo.IN_PROGRESS:
                    downloadedBytesNumber.setText(String.valueOf(progressInfo.getDownloadedBytes()));
                    progressBar.setProgress(progressInfo.getDownloadedBytes());
                    break;
                case ProgressInfo.FINISHED:
                    downloadedBytesNumber.setText(progressInfo.getFileSize());
                    progressBar.setProgress(100);
                    break;
                case ProgressInfo.ERROR:
                    Toast.makeText(getApplicationContext(), R.string.downloading_error ,Toast.LENGTH_SHORT).show();
            }
        }
    };

    //Register receiver
    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(
                broadcastReceiver, new IntentFilter(MyIntentService.NOTIFICATION));
    }

    //Unregister receiver
    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    class DownloadInfoTask extends AsyncTask<String ,Void, FileInfo>{


        //connects with server, get file info and return file info object
        @Override
        protected FileInfo doInBackground(String... strings) {

            HttpsURLConnection connection = null;


            try{
                URL url = new URL(strings[0]); //??
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                fileInfo.setFileSize(connection.getContentLength());
                fileInfo.setFileType(connection.getContentType());

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(connection != null) connection.disconnect();
            }

            return fileInfo;
        }


        //set file type and size text when task is completed
        @Override
        protected void onPostExecute(FileInfo fileInfo) {
            super.onPostExecute(fileInfo);

            fileSize.setText(String.valueOf(fileInfo.getFileSize()));
            fileType.setText(fileInfo.getFileType());

        }


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setReferences();

        if(savedInstanceState != null){
            fileType.setText(savedInstanceState.getString(FILE_TYPE));
            fileSize.setText(savedInstanceState.getString(FILE_SIZE));
            downloadedBytesNumber.setText(savedInstanceState.getString(DOWNLOADED_BYTES));
            addressUrl.setText(savedInstanceState.getString(URL));
        }

        setGetInfoButton();
        setDownloadButton();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(FILE_TYPE, fileType.getText().toString());
        outState.putString(FILE_SIZE, fileSize.getText().toString());
        outState.putString(DOWNLOADED_BYTES, downloadedBytesNumber.getText().toString());
        outState.putString(URL, addressUrl.getText().toString());

    }



    private void setReferences() {
        addressUrl = findViewById(R.id.editTextUrl);
        fileSize = findViewById(R.id.fileSizeNumber);
        fileType = findViewById(R.id.fileTypeName);
        getInfoButton = findViewById(R.id.buttonGetInformation);
        downloadButton = findViewById(R.id.buttonDownload);
        downloadedBytesNumber = findViewById(R.id.downloadedBytesNumber);
        progressBar = findViewById(R.id.progressBar);

        progressBar.getProgressDrawable().setColorFilter(
                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
    }

    void setGetInfoButton(){
        getInfoButton.setOnClickListener(v -> {
            DownloadInfoTask downloadInfoTask = new DownloadInfoTask();
            downloadInfoTask.execute(addressUrl.getText().toString());
        });
    }

    void setDownloadButton(){
        downloadButton.setOnClickListener(v -> {

            Log.d("intent", "Sprawdzanie uprawnien");
            if(ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_GRANTED){
                Log.d("intent", "nadanie uprawnien");
                MyIntentService.runService(MainActivity.this, addressUrl.getText().toString());

            } else{
                Log.d("intent", "przed nadanie requesta");
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    Log.d("intent", "nadanie requesta");
                    ActivityCompat.requestPermissions(this,
                            new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_WRITE_EXTERNAL_STORAGE);

                }else{
                    Log.d("intent", "force nadanie requesta");
                    ActivityCompat.requestPermissions(this,
                            new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, CODE_WRITE_EXTERNAL_STORAGE);
                }

            }

        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case CODE_WRITE_EXTERNAL_STORAGE:
                if(permissions.length > 0 &&
                permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    setDownloadButton();
                }else{

                }
                break;
        }
    }
}