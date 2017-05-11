package com.king.geomodel.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.GridView;

import com.baidu.mobstat.StatService;
import com.king.geomodel.R;
import com.king.geomodel.base.BaseActivity;
import com.king.geomodel.base.BaseApplication;
import com.king.geomodel.execute.ListObjectsSamples;
import com.king.geomodel.utils.CommonValues;
import com.king.geomodel.utils.ToastUtil;
import com.king.geomodel.utils.dialogUtils.DialogManager;
import com.king.geomodel.view.adapter.PicAdapter;

import java.util.List;

public class ShowActivity extends BaseActivity implements View.OnClickListener {
    private GridView gv;
    private List<String> urls;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
//                    gv.setAdapter(new PicAdapter(ShowActivity.this, urls));
                    break;
                case 0:
                    DialogManager.getInstance().dissMissDialog();
                    ToastUtil.getInstance().toastInCenter(ShowActivity.this, "获取图片失败");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        DialogManager.getInstance().showProgressDialog(this, "正在加载");
        gv = (GridView) findViewById(R.id.gv);
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                urls = new ListObjectsSamples(BaseApplication.oss, CommonValues.ACADEMYBUCKET).asyncListObjectsWithPrefix();
//                Message message = new Message();
//                if (urls == null || urls.size() == 0) {
//                    message.what = 0;
//                    handler.sendMessage(message);
//                } else {
//                    message.what = 1;
//                    handler.sendMessage(message);
//                }
//            }
//        }).start();


    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onPageStart(this, "ShowActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this, "ShowActivity");
    }
}
