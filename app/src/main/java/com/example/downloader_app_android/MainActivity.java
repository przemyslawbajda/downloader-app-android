package com.example.downloader_app_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
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

            fileSize.setText(fileInfo.getFileSize());
            fileType.setText(fileInfo.getFileType());

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addressUrl = findViewById(R.id.editTextUrl);
        fileSize = findViewById(R.id.textFileSize);
        fileType = findViewById(R.id.textFileType);

        //DownloadInfoTask downloadInfoTask = new DownloadInfoTask();
        //downloadInfoTask.execute(addressUrl.getText().toString());



    }
}