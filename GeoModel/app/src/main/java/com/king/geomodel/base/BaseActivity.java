package com.king.geomodel.base;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;

import com.baidu.mobstat.StatService;
import com.king.geomodel.R;
import com.king.geomodel.http.HttpCallBack;
import com.king.geomodel.http.ZCResponse;
import com.king.geomodel.tinker.SampleApplicationLike;
import com.king.geomodel.view.ActionBarView;
import com.king.geomodel.view.LoginActivity;
import com.king.greenDAO.dao.GeoModelDao;
import com.king.greenDAO.dao.PositionDao;
import com.king.greenDAO.dao.UserDao;
import com.zhy.autolayout.AutoLayoutActivity;
/**
 * Created by king on 2016/9/18.
 */
public class BaseActivity extends AutoLayoutActivity implements HttpCallBack {
    protected ActionBarView topbarView;
    //应用是否销毁标志
    protected boolean isDestroy;
    //防止重复点击设置的标志，涉及到点击打开其他Activity时，将该标志设置为false，在onResume事件中设置为true
    private boolean clickable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isDestroy = false;
        // 添加Activity到堆栈
        AppManager.getAppManager().addActivity(this);

        //配置百度统计
        stat();
    }

    public void stat() {
        // 打开调试开关，正式版本请关闭，以免影响性能
        StatService.setDebugOn(true);
        // 打开异常收集开关，默认收集java层异常，如果有嵌入SDK提供的so库，则可以收集native crash异常
        StatService.setOn(this, StatService.EXCEPTION_LOG);

        // 如果没有页面和事件埋点，此代码必须设置，否则无法完成接入
        // 设置发送策略，建议使用 APP_START
        // 发送策略，取值 取值 APP_START、SET_TIME_INTERVAL、ONCE_A_DAY
        // 备注，SET_TIME_INTERVAL与ONCE_A_DAY，如果APP退出或者进程死亡，则不会发送
        // 建议此代码不要在Application中设置，否则可能因为进程重启等造成启动次数高，具体见web端sdk帮助中心
        StatService.start(this);
    }

    /**
     * 跳转到登录
     */
    protected void goToLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    /**
     * 加载页面
     *
     * @param url
     * @param title
     */
    public void gotoWebView(String url, String title) {
        Intent intent = new Intent(this, BasicWebActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        startActivity(intent);
    }

    public ActionBarView getTopbar() {
        if (topbarView == null) {
            View view = findViewById(R.id.topbar_view);
            if (view != null) {
                topbarView = new ActionBarView(view);
            }
        }
        return topbarView;
    }

    public UserDao getUserDao() {
        return SampleApplicationLike.getInstance().getDaoSession().getUserDao();
    }

    public GeoModelDao getGeoModelDao() {
        return SampleApplicationLike.getInstance().getDaoSession().getGeoModelDao();
    }

    public PositionDao getPositionDao() {
        return SampleApplicationLike.getInstance().getDaoSession().getPositionDao();
    }

    public SQLiteDatabase getDb() {
        return SampleApplicationLike.getInstance().getDb();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isDestroy = true;
        // 结束Activity&从堆栈中移除
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //每次返回界面时，将点击标志设置为可点击
        clickable = true;
    }

    /**
     * 当前是否可以点击
     *
     * @return
     */
    protected boolean isClickable() {
        return clickable;
    }

    /**
     * 锁定点击
     */
    protected void lockClick() {
        clickable = false;
    }


    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        if (isClickable()) {
            lockClick();
            super.startActivityForResult(intent, requestCode, options);
        }
    }

    /**
     * 把一个bitmap压缩，压缩到指定大小
     *
     * @param bm
     * @param width
     * @param height
     * @return
     */
    public static Bitmap scaleBitmap(Bitmap bm, float width, float height) {
        if (bm == null) {
            return null;
        }
        int bmWidth = bm.getWidth();
        int bmHeight = bm.getHeight();
        float scaleWidth = width / bmWidth;
        float scaleHeight = height / bmHeight;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        if (scaleWidth == 1 && scaleHeight == 1) {
            return bm;
        } else {
            Bitmap resizeBitmap = Bitmap.createBitmap(bm, 0, 0, bmWidth,
                    bmHeight, matrix, false);
            bm.recycle();//回收图片内存
            bm.setDensity(240);
            return resizeBitmap;
        }
    }

    @Override
    public boolean onSuccess(ZCResponse response, String method) {
        return false;
    }

    @Override
    public boolean onFail(Throwable error, String url) {
        return false;
    }

}
