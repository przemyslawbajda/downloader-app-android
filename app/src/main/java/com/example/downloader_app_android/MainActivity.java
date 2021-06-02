package com.example.downloader_app_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    private FileInfo fileInfo = new FileInfo();
    private EditText addressUrl;
    private TextView fileSize;
    private TextView fileType;
    private Button getInfoButton;
    private Button downloadButton;
    private final int CODE_WRITE_EXTERNAL_STORAGE = 1;

    class DownloadInfoTask extends AsyncTask<String ,Void, FileInfo>{

        @Override
        protected FileInfo doInBackground(String... strings) {
            //connects with server, get file info and return file info object
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

        addressUrl = findViewById(R.id.editTextUrl);
        fileSize = findViewById(R.id.fileSizeNumber);
        fileType = findViewById(R.id.fileTypeName);
        getInfoButton = findViewById(R.id.buttonGetInformation);
        downloadButton = findViewById(R.id.buttonDownload);

        setGetInfoButton();
        //Log.d("intent", "Sprawdzanie uprawnien");
        setDownloadButton();



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