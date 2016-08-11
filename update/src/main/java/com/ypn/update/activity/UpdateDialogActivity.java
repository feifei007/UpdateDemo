package com.ypn.update.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ypn.update.R;
import com.ypn.update.config.DownloadKey;


/**
 * Created by Administrator on 2016/8/10.
 */
public class UpdateDialogActivity extends Activity {

    TextView yes, no;
    TextView tv_changelog;

    Context context = DownloadKey.fromActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_update_dialog);

        yes = (TextView) findViewById(R.id.download);
        no = (TextView) findViewById(R.id.cancel);
        tv_changelog = (TextView) findViewById(R.id.updatedialog_text_changelog);

        tv_changelog.setText("更新日志：\n" + DownloadKey.changeLog);

        yes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(UpdateDialogActivity.this, context.getClass());
                setResult(2, intent);
                DownloadKey.ToShowDownloadView = DownloadKey.showDownloadView;
                finish();
            }
        });

        no.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(UpdateDialogActivity.this, context.getClass());
                setResult(1, intent);
                DownloadKey.ToShowDownloadView = DownloadKey.closeDownloadView;
                finish();
            }
        });
}
        }
