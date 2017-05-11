package com.king.geomodel.viewmodel;

import com.king.geomodel.base.ModelCallBack;
import com.king.geomodel.http.HttpCallBack;
import com.king.geomodel.http.RequestCenter;
import com.king.geomodel.utils.dialogUtils.DialogManager;
import com.king.geomodel.view.PersonalActivity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by king on 2016/11/23.
 * 个人资料modelView
 */
public class PersonalInfoModelView implements ModelCallBack {
    private static PersonalInfoModelView mInstance;

    private PersonalInfoModelView() {

    }

    public static PersonalInfoModelView getmInstance() {
        if (mInstance == null) {
            synchronized (PersonalInfoModelView.class) {
                mInstance = new PersonalInfoModelView();
            }
        }
        return mInstance;
    }

    /**
     * 获取个人资料
     *
     * @param headPath
     * @param callBack
     */
    public void getPersonalInfo(String headPath, HttpCallBack callBack) {
        Map<String, File> fileMaps = new HashMap<>();
        File file = new File(headPath);
        fileMaps.put("head", file);
        RequestCenter.changeAvatarInfo(fileMaps, callBack);
    }

    /**
     * 处理业务逻辑
     *
     * @param model model实体
     */
    @Override
    public void onResultCallback(Object model) {

    }

    @Override
    public void onMultipleResultCallBack(String method, Object[] model) {

    }
}
