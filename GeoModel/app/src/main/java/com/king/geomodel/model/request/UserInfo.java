package com.king.geomodel.model.request;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.king.geomodel.BR;

import java.io.Serializable;



/**
 * Created by king on 2016/89/18.
 * 登录Model
 */
public class UserInfo extends BaseObservable implements Serializable {
    private Integer id;
    private String phone;
    private String password;
    private String nickname;
    private String sex;
    private String sign;
    private String token;

    @Bindable
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    @Bindable
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        notifyPropertyChanged(BR.nickname);
    }

    @Bindable
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
        notifyPropertyChanged(BR.sex);
    }

    @Bindable
    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
        notifyPropertyChanged(BR.sign);
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
