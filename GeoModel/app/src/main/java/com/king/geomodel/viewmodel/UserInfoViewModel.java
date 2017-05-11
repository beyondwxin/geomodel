package com.king.geomodel.viewmodel;

import android.content.Context;

import com.alibaba.fastjson.JSONObject;
import com.king.geomodel.base.BaseApplication;
import com.king.geomodel.base.ModelCallBack;
import com.king.geomodel.http.HttpCallBack;
import com.king.geomodel.http.MyJSON;
import com.king.geomodel.http.RequestCenter;
import com.king.geomodel.http.ZCResponse;
import com.king.geomodel.model.request.UserInfo;
import com.king.geomodel.utils.CommonValues;
import com.king.geomodel.utils.SharedPreferencesUtil;
import com.king.greenDAO.bean.User;


/**
 * Created by king on 2016/9/18.
 */
public class UserInfoViewModel implements ModelCallBack{
    private Context mContext;

    public UserInfoViewModel(Context context) {
        super();
        mContext = context;
    }

    /**
     * @param userInfo
     */
    public void doLogin(UserInfo userInfo, HttpCallBack callBack) {
        RequestCenter.login(userInfo, callBack);
    }

    /**
     * 处理业务逻辑
     * @param model model实体
     */
    @Override
    public void onResultCallback(Object model) {
        User user= (User) model;
        SharedPreferencesUtil.saveObject(mContext, CommonValues.USERINFO, user);
    }

    @Override
    public void onMultipleResultCallBack(String method, Object[] model) {

    }

}
