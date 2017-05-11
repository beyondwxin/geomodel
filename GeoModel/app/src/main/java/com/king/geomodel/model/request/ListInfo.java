package com.king.geomodel.model.request;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.king.geomodel.BR;

import java.io.Serializable;
import java.util.List;

/**
 * Created by king on 2016/9/23.
 * 首页ListView item信息
 */
public class ListInfo extends BaseObservable implements Serializable{
    private String id;
    private String time;
    private String title;//标题
    private String des;//描述
    private String status;//已保存、已上传
    private List<String> paths;//图片路径集合

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Bindable
    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
        notifyPropertyChanged(BR.time);
    }

    @Bindable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        notifyPropertyChanged(BR.title);
    }

    @Bindable
    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
        notifyPropertyChanged(BR.des);
    }

    @Bindable
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
        notifyPropertyChanged(BR.status);
    }
    @Bindable
    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
        notifyPropertyChanged(BR.paths);
    }
}
