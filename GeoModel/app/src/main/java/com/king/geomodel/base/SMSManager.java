package com.king.geomodel.base;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;


import com.king.geomodel.utils.LogUtil;
import com.king.geomodel.utils.listener.TimeListener;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


public class SMSManager {
    private static final SMSManager instance = new SMSManager();

    public static SMSManager getInstance() {
        return instance;
    }

    public static boolean DEBUG = true;
    public static final String DEFAULT_APPKEY = "1c63801f6e980";
    public static final String DEFAULT_APPSECRET = "a0f72debcd7adcf98422a9fe83428b7a";
    public static int DEFAULT_DELAY = 60;

    public ArrayList<TimeListener> timeList = new ArrayList<>();
    private boolean inited = false;
    private Timer timer;
    private int last = 0;
    private CallbackEventHandler mHandler = new CallbackEventHandler();

    private String phone;

    private void startTimer() {
        timer = new Timer();
        notifyEnable();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                last -= 1;
                notifyLastTime();
                if (last == 0) {
                    notifyEnable();
                    timer.cancel();
                }
            }
        }, 0, 1000);
    }

    public void stopTimer() {
        if (timer != null) {
            notifyEnable();
            timer.cancel();
        }
    }

    private SMSManager() {
    }

    private void init(Context ctx) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = ctx.getPackageManager()
                    .getApplicationInfo(ctx.getPackageName(),
                            PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appKey;
        String appSecret;
//        if (appInfo.metaData == null||appInfo.metaData.getString("SMS_MOB_APPKEY") == null||appInfo.metaData.getString("SMS_MOB_APPSECRET") == null){
        appKey = DEFAULT_APPKEY;
        appSecret = DEFAULT_APPSECRET;
//        }else{
//            appKey = appInfo.metaData.getString("SMS_MOB_APPKEY").trim();
//            appSecret = appInfo.metaData.getString("SMS_MOB_APPSECRET").trim();
//        }
        log("appkey:" + appKey + "  appsecret:" + appSecret);
        SMSSDK.initSDK(ctx, appKey, appSecret);
        inited = true;
        SMSSDK.registerEventHandler(mHandler);
    }

    public void setDefaultDelay(int delay) {
        DEFAULT_DELAY = delay;
    }

    public void verifyNum(Context ctx, String country, String number, final SMSCallback callback) {
        if (!inited) {
            init(ctx);
        }
        mHandler.setCallback(callback);
        sendMessage(ctx, country, number);
    }

    public void sendMessage(Context ctx, String country, String number) {
        if (!inited) {
            init(ctx);
        }
        phone = number;
        SMSSDK.getVerificationCode(country, number);
    }

    private void notifyLastTime() {
        for (TimeListener listener : timeList) {
            final TimeListener finalListener = listener;
            try {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        finalListener.onLastTimeNotify(last);
                    }
                });
            } catch (Exception e) {
                unRegisterTimeListener(listener);
            }
        }
    }

    private void notifyEnable() {
        for (TimeListener listener : timeList) {
            final TimeListener finalListener = listener;
            try {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        finalListener.onAbleNotify(last == 0);
                    }
                });
            } catch (Exception e) {
                unRegisterTimeListener(listener);
            }
        }
    }


    public void registerTimeListener(TimeListener listener) {
        timeList.add(listener);
        listener.onLastTimeNotify(last);
        listener.onAbleNotify(last == 0);
    }

    public void unRegisterTimeListener(TimeListener listener) {
        timeList.remove(listener);
    }

    public void verifyCode(Context ctx, String country, String number, String code, final SMSCallback callback) {
        if (!inited) {
            init(ctx);
        }
        mHandler.setCallback(callback);
        SMSSDK.submitVerificationCode(country, number, code);
    }

    private class CallbackEventHandler extends EventHandler {
        SMSCallback mCallback;
        Handler handler = new Handler();

        public void setCallback(SMSCallback mCallback) {
            this.mCallback = mCallback;
        }

        @Override
        public void afterEvent(int event, int result, final Object data) {
            Log.d("data", data.toString());
            if (result == SMSSDK.RESULT_COMPLETE) {
                log("回调完成");
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    log("提交验证码成功");
                    LogUtil.e("mob_submit", "提交验证码成功");

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    log("获取验证码成功");
                    last = DEFAULT_DELAY;
                    startTimer();
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    log("返回支持发送验证码的国家列表");
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null)
                            mCallback.success();
                        mCallback = null;
                    }
                });
            } else {
                log("mob:" + data.toString());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mCallback != null)
                            mCallback.error((Throwable) data);
                        mCallback = null;
                    }
                });
            }
        }
    }

    private void log(String content) {
        if (DEBUG) Log.i("SMSSDK", content);
    }

}
