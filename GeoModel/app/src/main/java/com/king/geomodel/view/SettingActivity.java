package com.king.geomodel.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.AppUpdateInfoForInstall;
import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.CPCheckUpdateCallback;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.baidu.mobstat.StatService;
import com.king.geomodel.R;
import com.king.geomodel.base.BaseActivity;
import com.king.geomodel.http.MyJSON;
import com.king.geomodel.http.RequestCenter;
import com.king.geomodel.http.ZCResponse;
import com.king.geomodel.utils.CommonProgressDialog;
import com.king.geomodel.utils.DataCleanManager;
import com.king.geomodel.utils.DeviceInfo;
import com.king.geomodel.utils.DownLoadApkManager;
import com.king.geomodel.utils.ToastUtil;
import com.king.geomodel.utils.dialogUtils.DialogManager;
import com.king.geomodel.view.pojo.UpdateInfo;

import org.greenrobot.eventbus.EventBus;

import java.io.File;


/**
 * Created by king on 2016/12/5.
 * 设置界面
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private TextView mCurrentVersion;
    private TextView mCacheSize;
    private SwitchButton mSwitchButton;

    //版本更新部分
    private UpdateInfo info;
    private int mCurrentVersionCode;

    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getTopbar().setLeftText(R.string.tv_setting);

        mCurrentVersionCode = DeviceInfo.getVersionCode(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("正在检查...");
        dialog.setIndeterminate(true);
        initView();
        initData();
        setListener();
    }

    private void initView() {
        mCurrentVersion = (TextView) findViewById(R.id.tv_currentVersion);
        mCacheSize = (TextView) findViewById(R.id.tv__cacheSize);
        mSwitchButton = (SwitchButton) findViewById(R.id.sButton);
    }

    private void initData() {
        try {
            mCurrentVersion.setText("当前版本" + DeviceInfo.getVersionName(this));
            mCacheSize.setText(DataCleanManager.getTotalCacheSize(this));
            //设置初始状态为false
            mSwitchButton.setChecked(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setListener() {
        mSwitchButton.setOnCheckedChangeListener(this);
    }

    // 清空缓存
    private void clearCache() {
        final Dialog dialog = new Dialog(this, R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_clear_cache, null);
        dialog.setContentView(view);
        Button cancel = (Button) view.findViewById(R.id.btn_cancel);
        Button ok = (Button) view.findViewById(R.id.btn_ok);

        /**
         * 不清空
         */
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        /**
         * 清空
         */
        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DataCleanManager.clearAllCache(SettingActivity.this);
                    }
                });

                ToastUtil.getInstance().toastInCenter(SettingActivity.this, "清除成功");
                try {
                    mCacheSize.setText(DataCleanManager.getTotalCacheSize(SettingActivity.this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_checkUpdate:
                //手动更新
//                mCurrentVersionCode = DeviceInfo.getVersionCode(this);
//                RequestCenter.checkVersion(this);


                dialog.show();
                BDAutoUpdateSDK.cpUpdateCheck(SettingActivity.this, new CPCheckUpdateCallback() {
                    @Override
                    public void onCheckUpdateCallback(AppUpdateInfo appUpdateInfo, AppUpdateInfoForInstall appUpdateInfoForInstall) {
                        if (mCurrentVersionCode == appUpdateInfo.getAppVersionCode()) {
                            Toast.makeText(getApplicationContext(), "当前已最新", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        } else {
                            BDAutoUpdateSDK.uiUpdateAction(SettingActivity.this, new MyUICheckUpdateCallback());
                        }

                    }
                });

                break;
            case R.id.ll_clear:
                //清空缓存
                clearCache();
                break;
            case R.id.tv_modifyPassword:
                startActivity(new Intent(SettingActivity.this, ResetPasswordActivity.class));
                break;
            case R.id.tv_about:
                gotoWebView("http://www.radi.ac.cn/", "关于我们");
                break;
        }
    }


    private class MyUICheckUpdateCallback implements UICheckUpdateCallback {
        @Override
        public void onCheckComplete() {
            dialog.dismiss();
        }
    }


    @Override
    public boolean onSuccess(ZCResponse response, String method) {
        DialogManager.getInstance().dissMissDialog();
        //检测版本
        if (method.equals(RequestCenter.CHECKVERSION)) {
            info = MyJSON.parseObject(response.getMainData().getString("updateInfo"), UpdateInfo.class);
            if (!TextUtils.equals(info.getVersion(), mCurrentVersionCode + ".0")) {
                //版本不一致
                showUpdateDialog();
            } else {
                Toast.makeText(getApplicationContext(), "当前已最新", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onSuccess(response, method);
    }


    /*
     * 弹出对话框通知用户更新程序
	 */
    protected void showUpdateDialog() {
        final Dialog dialog = new Dialog(this, R.style.dialog);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_update, null);
        dialog.setContentView(view);
        TextView versionCode = (TextView) view.findViewById(R.id.tv_versionName);
        TextView content = (TextView) view.findViewById(R.id.tv_content);
        versionCode.setText(info.getVersion());
        content.setText(info.getDescription());
        Button cancel = (Button) view.findViewById(R.id.btn_cancel);
        Button down = (Button) view.findViewById(R.id.btn_down);

        /**
         * 不清空
         */
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        /**
         * 清空
         */
        down.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                downLoadApk();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


    /*
     * 从服务器中下载APK
     */
    protected void downLoadApk() {
        final CommonProgressDialog mDialog = new CommonProgressDialog(this);
        mDialog.setMessage("正在下载");
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        mDialog.show();
        mDialog.setMax(100 * 1024 * 1024);
        mDialog.setProgress(0);
        new Thread() {
            @Override
            public void run() {
                try {
                    File file = DownLoadApkManager.getFileFromServer(info.getApk_url(), mDialog);
                    sleep(3000);
                    installApk(file);
                    mDialog.dismiss(); //结束掉进度条对话框
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //安装apk
    protected void installApk(File file) {
        Intent intent = new Intent();
        //执行动作
        intent.setAction(Intent.ACTION_VIEW);
        //执行的数据类型
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//        if (!isChecked) {
//            JPushInterface.stopPush(getApplicationContext());
//            ToastUtil.getInstance().toastInCenter(SettingActivity.this, "已关闭");
//        } else {
//            JPushInterface.resumePush(getApplicationContext());
//            ToastUtil.getInstance().toastInCenter(SettingActivity.this, "已开启");
//        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        StatService.onPageStart(this, "SettingActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this, "SettingActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialog.dismiss();
        EventBus.getDefault().unregister(this);
    }

}
