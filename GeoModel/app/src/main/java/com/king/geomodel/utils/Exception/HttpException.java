package com.king.geomodel.utils.Exception;

/**
 * Created by king on 2016/9/18.
 */
public class HttpException extends Exception {
    public HttpException(String message, int code){
        super(message);
    }
}
