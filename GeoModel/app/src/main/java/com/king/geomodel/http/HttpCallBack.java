package com.king.geomodel.http;

import okhttp3.Call;

/**
 * Http请求回调接口
 */
public interface HttpCallBack {
    boolean onSuccess(ZCResponse response, String method);

    boolean onFail(Throwable error, String url);

}