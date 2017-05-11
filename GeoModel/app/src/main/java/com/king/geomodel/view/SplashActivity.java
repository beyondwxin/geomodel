package com.king.geomodel.view;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.WindowManager;

import com.baidu.mobstat.StatService;
import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppInstallListener;
import com.fm.openinstall.model.AppData;
import com.fm.openinstall.model.Error;
import com.king.geomodel.R;
import com.king.geomodel.base.BaseActivity;
import com.king.geomodel.model.serializable.SUserInfo;
import com.king.geomodel.utils.CommonValues;
import com.king.geomodel.utils.SharedPreferencesUtil;
import com.king.geomodel.utils.album.LocalImageHelper;
import com.king.greenDAO.bean.User;
import com.tencent.tinker.lib.tinker.TinkerInstaller;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by king on 2016/9/18.
 * 开屏页
 */
public class SplashActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks, AppInstallListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //判断是否是任务栈中的根Activity,）解决启动时再次创建并叠加到Task任务栈上
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }

        // 如果不是第一次启动app，则正常显示启动屏
        setContentView(R.layout.activity_splash);

        checkPermission();
        LocalImageHelper.init(this);


        //获取OpenInstall数据
        OpenInstall.getInstall(SplashActivity.this);

        boolean isFirstOpen = SharedPreferencesUtil.getBoolean(this, CommonValues.FIRST_OPEN);
        // 如果是第一次启动，则先进入功能引导页
        if (!isFirstOpen) {
            Intent intent = new Intent(this, GuideActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        handler.sendEmptyMessageDelayed(0, 2000);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            User user = SUserInfo.getUserInfoInstance();
            if (user != null) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
            goToLoginActivity();
        }
    };

    public void checkPermission() {
        String[] perms = {Manifest.permission.RECEIVE_SMS, Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CHANGE_CONFIGURATION, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, perms)) {
        } else {
            EasyPermissions.requestPermissions(SplashActivity.this, "RECEIVE_SMS", 1001,
                    Manifest.permission.RECEIVE_SMS);
            EasyPermissions.requestPermissions(SplashActivity.this, "SEND_SMS", 1002,
                    Manifest.permission.SEND_SMS);
            EasyPermissions.requestPermissions(SplashActivity.this, "READ_SMS", 1003,
                    Manifest.permission.READ_SMS);
            EasyPermissions.requestPermissions(SplashActivity.this, "READ_EXTERNAL_STORAGE", 1004,
                    Manifest.permission.READ_EXTERNAL_STORAGE);
            EasyPermissions.requestPermissions(SplashActivity.this, "WRITE_EXTERNAL_STORAGE", 1005,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            EasyPermissions.requestPermissions(SplashActivity.this, "CHANGE_CONFIGURATION", 1006,
                    Manifest.permission.CHANGE_CONFIGURATION);
            EasyPermissions.requestPermissions(SplashActivity.this, "MOUNT_UNMOUNT_FILESYSTEMS", 1007,
                    Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
            EasyPermissions.requestPermissions(SplashActivity.this, "CAMERA", 1008,
                    Manifest.permission.CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onPageStart(this, "SplashActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this, "SplashActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onInstallFinish(AppData appData, Error error) {
        if (error == null) {
            //获取渠道数据
            Log.d("SplashActivity", "channel = " + appData.getChannel());
            //获取个性化安装数据
            Log.d("SplashActivity", "install = " + appData.getData());
        } else {
            Log.d("SplashActivity", "error : " + error.toString());
        }
    }
}
