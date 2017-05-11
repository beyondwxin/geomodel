package com.king.geomodel.base;

/**
 * Created by 伍新 on 2016/8/29.
 */
public interface ModelCallBack<T> {
     void onResultCallback(T model) ;
     void onMultipleResultCallBack(String method, T... model);
}
