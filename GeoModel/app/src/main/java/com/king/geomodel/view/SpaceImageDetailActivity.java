package com.king.geomodel.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.king.geomodel.R;
import com.king.geomodel.base.BaseApplication;
import com.king.geomodel.tinker.SampleApplicationLike;
import com.king.geomodel.utils.BitmapTools;
import com.king.geomodel.utils.CommonValues;
import com.king.geomodel.utils.SharedPreferencesUtil;
import com.king.geomodel.utils.widget.view.SmoothImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.IOException;

/**
 * 头像放大全屏
 */
public class SpaceImageDetailActivity extends Activity {
    private SmoothImageView imageView;
    private int mLocationX;
    private int mLocationY;
    private int mWidth;
    private int mHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationX = getIntent().getIntExtra("locationX", 0);
        mLocationY = getIntent().getIntExtra("locationY", 0);
        mWidth = getIntent().getIntExtra("width", 0);
        mHeight = getIntent().getIntExtra("height", 0);

        imageView = new SmoothImageView(this);
        imageView.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
        imageView.transformIn();
        imageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        setContentView(imageView);
        final String path = SharedPreferencesUtil.getStringValue(this, CommonValues.URI_HEAD, "");
        if (!TextUtils.isEmpty(path)) {
            ImageLoader.getInstance().displayImage("file://" + path, imageView, SampleApplicationLike.getInstance().getDisplayOptions());
        } else {
            ImageLoader.getInstance().loadImage("http://210.72.27.32:6081//head/head.png", new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    try {
                        BitmapTools.saveBitmap(path, bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
    }

    @Override
    public void onBackPressed() {
        close();
    }

    /**
     * 关闭(释放资源)
     */
    private void close() {
        imageView.transformOut();
        imageView.setOnTransformListener(new SmoothImageView.TransformListener() {
            @Override
            public void onTransformComplete(int mode) {
                if (mode == 2) {
                    finish();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }

}

