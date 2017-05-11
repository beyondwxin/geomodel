package com.king.geomodel.eventbus;

import android.net.Uri;

/**
 * Created by king on 2016/11/16.
 * 相应添加事件
 */

public class HeadEvent {
    private String path;

    public HeadEvent(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
