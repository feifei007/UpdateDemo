package com.ypn.update.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ypn.update.R;
import com.ypn.update.config.DownloadKey;
import com.ypn.update.module.Download;

/**
 * Created by Administrator on 2016/8/10.
 */
public class DownloadingActivity extends Activity {
    private ImageView close;
    private ProgressBar mProgress;
    private TextView count;

    private Context mContext = DownloadKey.fromActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_download_dialog);

        close = (ImageView) findViewById(R.id.downloaddialog_close);
        mProgress = (ProgressBar) findViewById(R.id.downloaddialog_progress);
        count = (TextView) findViewById(R.id.downloaddialog_count);

        new Download(mProgress, this, count).start();

        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(DownloadingActivity.this,
                        mContext.getClass());
                setResult(3, intent);
                DownloadKey.ToShowDownloadView = DownloadKey.closeDownloadView;
                DownloadKey.intercetFlag = true;
                finish();
            }
        });

    }

}
