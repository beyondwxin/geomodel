package com.king.geomodel.base;

import com.king.geomodel.http.HttpCallBack;
import com.king.geomodel.http.ZCResponse;
import com.king.geomodel.utils.Exception.HttpException;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by king on 2016/8/29.
 */
public class BaseViewModel implements HttpCallBack {
    protected ModelCallBack callback;
    @Override
    public boolean onSuccess(ZCResponse response, String method) {
        return false;
    }

    @Override
    public boolean onFail(Throwable error,  String url) {
        return false;
    }

//
//    @Override
//    public boolean httpCallBackPreFilter(String result, String method) {
//        return false;
//    }
}
