package com.king.geomodel.http;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by king on 2016/3/21.
 */
public class ZCRequest implements Serializable {
    private ZCRequestData request;
    private ZCHeader header;


    public ZCRequest() {
        request = new ZCRequestData();
        header = new ZCHeader();
    }

    public ZCHeader getHeader() {
        return header;
    }

    public void setHeader(ZCHeader header) {
        this.header = header;
    }

    public void addHeader(String key, Object value) {
        header.put(key, value);
    }

    public void putParams(String key, Object value) {
        request.getParamsMap().put(key, value);
    }

    public ZCRequestData getRequest() {
        return request;
    }

    public String readUrl() {
        return (String) header.get("action");
    }
    public void setAction(String action){
        header.put("action",action);
    }
    public String readMethod() {
        return (String) header.get("method");
    }
    public void setMethod(String method){
        header.put("method",method);
    }
    public void setZip(boolean isZip) {
        if (isZip) {
            addHeader("zip", 1);
        } else {
            addHeader("zip", 0);
        }
    }
    @JSONField(serialize = false)
    public boolean isZip() {
        return (Integer)header.get("zip") == 1;
    }


    public void setEncrpy(boolean isEncrpy) {
        if (isEncrpy) {
            addHeader("encrpy", 1);
        } else {
            addHeader("encrpy", 0);
        }
    }
    @JSONField(serialize = false)
    public boolean isEncrpy() {
        return (Integer)header.get("encrpy") == 1;
    }

    public void setRequest(ZCRequestData request) {
        this.request = request;
    }
}
