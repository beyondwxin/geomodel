package com.king.geomodel.utils;

import android.app.Activity;

import java.util.ArrayList;

/**
 * Created by king on 2016/9/18.
 */
public class APPActivityManager {
    private ArrayList<Activity> lists = new ArrayList<Activity>();

    private APPActivityManager() {
    }

    private static APPActivityManager activityManager;

    public static APPActivityManager getInstance() {
        if (activityManager == null) {
            activityManager = new APPActivityManager();
        }
        return activityManager;
    }

    public void addActivity(Activity activity) {
        if (activity != null) {
            lists.add(activity);
        }
    }

    public void removeActivity(Activity activity) {
        if (activity != null) {
            lists.remove(activity);
        }
    }

//    /**
//     * 销毁除主页面和登录页面外的页面（修改完登录密码后使用）
//     */
//    public void finishActivitiesButMain() {
//        int size = lists.size();
//        for (int i = 0; i < size; i++) {
//            Activity activity = lists.get(i);
//            if (activity != null) {
//                if (!(activity instanceof AddActivity)) {
//                    if (!(activity instanceof LoginAct)) {
//                        activity.finish();
//                    }
//                }
//            }
//        }
//    }
}
