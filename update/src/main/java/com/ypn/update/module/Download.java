package com.ypn.update.module;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ypn.update.config.DownloadKey;
import com.ypn.update.config.UpdateKey;
import com.ypn.update.utils.GetAppInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2016/8/10.
 */
public class Download extends Thread {
    private final static int DOWN_UPDATE = 1;
    private final static int DOWN_OVER = 2;
    private Context mContext;
    private ProgressBar mProgressBar;
    private NotificationManager mNotificationManager;
    private Notification.Builder mBuilder;
    private TextView mTextView;
    private int length;
    private int count;
    private int progress;

    public Download(ProgressBar progressBar, Context context, TextView textView) {
        mProgressBar = progressBar;
        mContext = context;
        mTextView = textView;
    }

    public Download(Notification.Builder builder, NotificationManager notificationManager, Context context) {
        mBuilder = builder;
        mNotificationManager = notificationManager;
        mContext = context;
    }

    private Handler handler = new Handler() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    if (UpdateKey.DialogOrNotification == UpdateKey.WITH_DIALOG) {
                        mProgressBar.setProgress(progress);
                        mTextView.setText(progress + "%");
                    } else if (UpdateKey.DialogOrNotification == UpdateKey.WITH_NOTIFICATION) {
                        mBuilder.setProgress(length, count, false)
                                .setContentText("下载进度:" + progress + "%");
                        mNotificationManager.notify(1115, mBuilder.build());
                    }
                    break;
                case DOWN_OVER:
                    if (UpdateKey.DialogOrNotification == UpdateKey.WITH_DIALOG) {
                        ((Activity) mContext).finish();
                    } else if (UpdateKey.DialogOrNotification == UpdateKey.WITH_NOTIFICATION) {
                        mBuilder.setTicker("下载完成");
                        mNotificationManager.notify(1115, mBuilder.build());
                        mNotificationManager.cancel(1115);
                    }
                    DownloadKey.ToShowDownloadView = DownloadKey.closeDownloadView;
                    if (checkApk()) {
                        installApk();
                    }
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void run() {
        URL url = null;
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            url = new URL(DownloadKey.apkUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            length = connection.getContentLength();
            inputStream = connection.getInputStream();
        } catch (FileNotFoundException e) {
            try {
                connection.disconnect();
                connection = (HttpURLConnection) url.openConnection();
                connection.setInstanceFollowRedirects(false);
                connection.connect();
                String location = new String(connection.getHeaderField("Location").getBytes("ISO-8859-1"), "UTF-8").replace(" ", "");
                url = new URL(location);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                length = connection.getContentLength();
                inputStream = connection.getInputStream();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File appDir = new File(DownloadKey.savePath);
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            String apkFile = DownloadKey.saleFileName;
            File file = new File(apkFile);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            long temFileLength = appDir.length();
            byte buf[] = new byte[1024];
            int times = 0;
            int numread;
            do {
                numread = inputStream.read(buf);
                count += numread;
                progress = (int) (((float) count / length) * 100);
                if ((times == 512) || (temFileLength == length)) {
                    handler.sendEmptyMessage(DOWN_UPDATE);
                    times = 0;
                }
                times++;
                if (numread <= 0) {
                    handler.sendEmptyMessage(DOWN_OVER);
                    break;
                }
                fileOutputStream.write(buf, 0, numread);
            } while (!DownloadKey.intercetFlag);//点击取消就停止下载

            fileOutputStream.flush();
            fileOutputStream.close();
            inputStream.close();
            connection.disconnect();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkApk() {
        String apkName = GetAppInfo.getAPKPackageName(mContext, DownloadKey.saleFileName);
        String appName = GetAppInfo.getAppPackageName(mContext);
        if (apkName.equals(appName)) {
            Log.i("UpdateFun TAG", "包名相同,安装apk");
            return true;
        } else {
            Log.i("UpdateFun TAG",
                    String.format("apk检验:包名不同。该app包名:%s，apk包名:%s", appName, apkName));
            Toast.makeText(mContext, "apk检验:包名不同,不进行安装,原因可能是运营商劫持", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void installApk() {
        File apkfile = new File(DownloadKey.saleFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }
}
