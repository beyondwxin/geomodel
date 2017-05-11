package com.king.geomodel.http;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Created by king on 2016/10/19.
 */
public class ZCResponse {
    private ZCResponseData result;
    private String message;
    private String status;
    private String code;//判断用户是否存在 00：不存在  01:存在

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ZCResponseData getResult() {
        return result;
    }

    public void setResult(ZCResponseData result) {

        this.result = result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public JSONObject getMainData() {
        try {
            JSONObject object = MyJSON.parseObject(result.getData());
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Map<String, Object>> getData() {
        return null;
    }
}
