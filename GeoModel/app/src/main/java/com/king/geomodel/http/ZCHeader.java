package com.king.geomodel.http;

import com.king.geomodel.base.BaseApplication;
import com.king.geomodel.tinker.SampleApplicationLike;
import com.king.geomodel.utils.CommonValues;
import com.king.geomodel.utils.DeviceInfo;
import com.king.geomodel.utils.SharedPreferencesUtil;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by admin on 2016/3/21.
 */
public class ZCHeader extends HashMap implements Serializable {

    public ZCHeader() {
        this.put("version", CommonValues.APP_VERSION);
        this.put("registrationId", SharedPreferencesUtil.getStringValue(SampleApplicationLike.getInstance().getApplication(), CommonValues.REGISTRATION_ID, ""));
        this.put("token", SharedPreferencesUtil.getStringValue(SampleApplicationLike.getInstance().getApplication(), CommonValues.TOKENCODE, ""));
        this.put("device", DeviceInfo.getManufacturer());
        this.put("platform", "android");
    }


}
