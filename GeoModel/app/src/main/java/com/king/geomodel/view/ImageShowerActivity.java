package com.king.geomodel.view;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.baidu.mobstat.StatService;
import com.king.geomodel.R;
import com.king.geomodel.base.BaseActivity;
import com.king.geomodel.utils.CommonValues;
import com.king.geomodel.utils.SharedPreferencesUtil;
import com.king.geomodel.utils.dialogUtils.ImageLoadingDialog;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ImageShowerActivity extends BaseActivity implements View.OnClickListener {
    private ImageView image;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_shower);
        getTopbar().setLeftText("眼镜");
        image = (ImageView) findViewById(R.id.result);
        url = getIntent().getStringExtra("url");
        final ImageLoadingDialog dialog = new ImageLoadingDialog(this);
        dialog.show();
        // 两秒后关闭后dialog
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                ImageLoader.getInstance().displayImage(url, image);
            }
        }, 3000);

    }

    @Override
    public void onClick(View v) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onPageStart(this, "ImageShowerActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this, "ImageShowerActivity");
    }
}
