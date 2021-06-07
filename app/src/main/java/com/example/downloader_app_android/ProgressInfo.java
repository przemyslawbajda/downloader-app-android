package com.example.downloader_app_android;

import android.os.Parcel;
import android.os.Parcelable;

public class ProgressInfo implements Parcelable {

    public static int IN_PROGRESS = 0;
    public static int FINISHED = 1;
    public static int ERROR = -1;

    private int downloadedBytes = 0;
    private int fileSize = 0;
    private int status;


    protected ProgressInfo(Parcel in) {
        downloadedBytes = in.readInt();
        fileSize = in.readInt();
        status = in.readInt();
    }

    public static final Creator<ProgressInfo> CREATOR = new Creator<ProgressInfo>() {
        @Override
        public ProgressInfo createFromParcel(Parcel in) {
            return new ProgressInfo(in);
        }

        @Override
        public ProgressInfo[] newArray(int size) {
            return new ProgressInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(downloadedBytes);
        dest.writeInt(fileSize);
        dest.writeInt(status);
    }

    public int getDownloadedBytes() {
        return downloadedBytes;
    }

    public void setDownloadedBytes(int downloadedBytes) {
        this.downloadedBytes = downloadedBytes;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

