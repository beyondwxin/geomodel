package com.king.geomodel.model.serializable;



import com.king.geomodel.base.BaseApplication;
import com.king.geomodel.tinker.SampleApplicationLike;
import com.king.geomodel.utils.CommonValues;
import com.king.geomodel.utils.SharedPreferencesUtil;
import com.king.greenDAO.bean.User;

import java.io.Serializable;

/**
 * 序列化用户信息
 * Created by king on 2016/10/9.
 */
public class SUserInfo implements Serializable {
    private static User userInfo;

    public static User getUserInfoInstance() {
        userInfo = (User) SharedPreferencesUtil.readObject(SampleApplicationLike.getInstance().getApplication(), CommonValues.USERINFO);
        return userInfo;
    }
}
