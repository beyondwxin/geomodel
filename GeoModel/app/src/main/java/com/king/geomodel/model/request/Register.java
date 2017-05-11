package com.king.geomodel.model.request;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.king.geomodel.BR;
/**
 * Created by king on 2016/9/18.
 * 用户注册
 */
public class Register extends BaseObservable {
    private String phone;
    private String password;
    private String authCode;
    private String getAuthCode;
    private String comfirmPassword;
    private boolean isClick; //获取验证码可点击状态
    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone= phone;
        notifyPropertyChanged(BR.phone);
    }
    @Bindable
    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
        notifyPropertyChanged(BR.authCode);
    }

    @Bindable
    public String getGetAuthCode() {
        return getAuthCode;
    }

    public void setGetAuthCode(String getAuthCode) {
        this.getAuthCode = getAuthCode;
        notifyPropertyChanged(BR.getAuthCode);
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
    public String getComfirmPassword() {
        return comfirmPassword;
    }

    public void setComfirmPassword(String comfirmPassword) {
        this.comfirmPassword = comfirmPassword;
        notifyPropertyChanged(BR.comfirmPassword);
    }
    @Bindable
    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
        notifyPropertyChanged(BR.click);
    }

}
