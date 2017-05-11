package com.king.geomodel.http;


import com.king.geomodel.base.BaseApplication;
import com.king.geomodel.model.request.Register;
import com.king.geomodel.model.request.ResetPassword;
import com.king.geomodel.model.request.UserInfo;
import com.king.geomodel.model.serializable.SUserInfo;
import com.king.geomodel.utils.CommonValues;
import com.king.geomodel.utils.MD5Util;
import com.king.geomodel.utils.SharedPreferencesUtil;
import com.king.greenDAO.bean.Position;
import com.king.greenDAO.bean.User;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.king.geomodel.utils.SharedPreferencesUtil.readObject;

/**
 * Created by king on 16/9/20.
 */
public class RequestCenter {
    public static final String LOGIN_METHOD = "user/login";//用户登录
    public static final String REGISTER_METHOD = "user/register";//用户注册
    public static final String FORGET_METHOD = "user/forgetPw";//忘记密码
    public static final String CHANGE_PASSWORD_METHOD = "user/modifyPw";//重置密码
    public static final String UPLOAD_PICTURES = "files/upload";//上传图片
    public static final String CHECKVERSION = "versionUpdate/update";//检测版本
    public static final String PERSONAL_INFO = "change/avatar";//获取个人资料

    /**
     * 登录
     *
     * @param userInfo
     * @param callBack
     */
    public static void login(UserInfo userInfo, HttpCallBack callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("phone", userInfo.getPhone());
        params.put("password", userInfo.getPassword());
        OkHttpUtil.getInstance().okHttpPost(LOGIN_METHOD, params, null, null, callBack);
    }

    /**
     * 注册
     *
     * @param register
     * @param callBack
     */
    public static void request(Register register, HttpCallBack callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("phone", register.getPhone());
        params.put("password", register.getPassword());
        params.put("vCode", register.getAuthCode());
        OkHttpUtil.getInstance().okHttpPost(REGISTER_METHOD, params, null, null, callBack);
    }

    /**
     * 忘记密码
     *
     * @param resetPassword
     */
    public static void forgetPassword(ResetPassword resetPassword, HttpCallBack callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("phone", resetPassword.getPhone());
        params.put("password", resetPassword.getPassword());
        params.put("vCode", resetPassword.getAuthCode());
        OkHttpUtil.getInstance().okHttpPost(FORGET_METHOD, params, null, null, callBack);
    }


    /**
     * 检测版本
     */
    public static void checkVersion(HttpCallBack callBack) {
        OkHttpUtil.getInstance().okHttpPost(CHECKVERSION, null, null, null, callBack);
    }

    /**
     * 重置密码
     *
     * @param phone
     * @param newPassword
     */
    public static void resetPassword(String phone, String newPassword, HttpCallBack callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("phone", phone);
        params.put("password", newPassword);
        OkHttpUtil.getInstance().okHttpPost(CHANGE_PASSWORD_METHOD, params, null, null, callBack);
    }

    /**
     * 上传图片
     *
     * @param picMaps
     * @param callBack
     */
    public static void upload(Map<String, File> picMaps, List<Position> positionMaps, HttpCallBack callBack) {
        Map<String, String> params = new HashMap<>();
        OkHttpUtil.getInstance().okHttpPost(UPLOAD_PICTURES, params, picMaps, positionMaps, callBack);
    }

    /**
     * 获取个人资料
     *
     * @param head     头像
     * @param callBack
     */
    public static void changeAvatarInfo(Map<String, File> head, HttpCallBack callBack) {
        Map<String, String> params = new HashMap<>();
        params.put("phone", SUserInfo.getUserInfoInstance().getPhone());
        OkHttpUtil.getInstance().okHttpPost(PERSONAL_INFO, params, head, null, callBack);
    }
}
