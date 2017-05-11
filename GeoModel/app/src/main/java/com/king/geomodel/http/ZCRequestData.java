package com.king.geomodel.http;


import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by admin on 2016/3/21.
 */
public class ZCRequestData  implements Serializable {
    private String params;
    private HashMap<String,Object> paramsMap =new HashMap<>();

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }
    @JSONField(serialize = false)
    public HashMap<String, Object> getParamsMap() {
        return paramsMap;
    }

    public void setParamsMap(HashMap<String, Object> paramsMap) {
        this.paramsMap = paramsMap;
    }
}
