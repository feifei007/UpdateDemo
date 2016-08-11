package com.ypn.update;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.ypn.update.activity.DownloadingActivity;
import com.ypn.update.activity.UpdateDialogActivity;
import com.ypn.update.config.DownloadKey;
import com.ypn.update.config.UpdateKey;
import com.ypn.update.module.Download;
import com.ypn.update.utils.GetAppInfo;

/**
 * Created by Administrator on 2016/8/10.
 */
public class UpdateFunGo {
    private Context mContext;
    private static NotificationManager notificationManager;
    private static Notification.Builder builder;
    private static volatile UpdateFunGo sInst = null;
    private static Download mDownload;

    public static UpdateFunGo init(Context context) {
        UpdateFunGo inst = sInst;
        if (inst == null) {
            synchronized (UpdateFunGo.class) {
                inst = sInst;
                if (inst == null) {
                    inst = new UpdateFunGo(context);
                    sInst = inst;
                }
            }
        }
        return inst;
    }

    public UpdateFunGo(Context context) {
        mContext = context;
        DownloadKey.fromActivity = context;
        DownloadKey.saleFileName = DownloadKey.savePath + GetAppInfo.getAppPackageName(context) + ".apk";
        showUpdateDialog();
    }

    /**
     * 在检查版本的时候，弹出版本更新框
     */
    public void showUpdateDialog() {
        if (DownloadKey.ToShowDownloadView == DownloadKey.showUpdateView && !DownloadKey.version.equals(GetAppInfo.getAppVersionCode(mContext))) {
            showNoticeDialog(mContext);//显示更新弹框
        }
    }


    private static void showNoticeDialog(Context context) {
        Intent intent = new Intent(context, UpdateDialogActivity.class);
        ((Activity) context).startActivityForResult(intent, 100);
    }

    /**
     * 通知栏或者弹框下载
     *
     * @param context
     */
    private static void showDownloadView(Context context) {
        switch (UpdateKey.DialogOrNotification) {
            case UpdateKey.WITH_DIALOG:
                Intent intent = new Intent(context, DownloadingActivity.class);
                ((Activity) context).startActivityForResult(intent, 0);
                break;
            case UpdateKey.WITH_NOTIFICATION:
                notificationInit(context);
                mDownload = new Download(builder, notificationManager, context);
                mDownload.start();
                break;
        }
    }

    private static void notificationInit(Context context) {
        Intent intent = new Intent(context, context.getClass());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new Notification.Builder(context);
        builder.setSmallIcon(android.R.drawable.ic_menu_info_details)
                .setTicker("开始下载")
                .setContentTitle(GetAppInfo.getAppName(context))
                .setContentText("正在更新")
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis());
    }

    /**
     * 显示下载弹框
     *
     * @param context
     */
    public static void onResume(Context context) {
        if (DownloadKey.ToShowDownloadView == DownloadKey.showDownloadView)
            showDownloadView(context);
    }

    public static void onStop() {
        if (DownloadKey.ToShowDownloadView == DownloadKey.showDownloadView && UpdateKey.DialogOrNotification == UpdateKey.WITH_NOTIFICATION) {
            mDownload.interrupt();//关闭下载
        }
    }
}
