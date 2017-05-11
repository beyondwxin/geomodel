package com.king.geomodel.model.response;

import android.databinding.BaseObservable;

import java.io.Serializable;

/**
 * Created by king on 2016/10/9.
 */
public class RUserInfo extends BaseObservable implements Serializable {

    private Integer id;
    private String username;
    private String password;
    private String token;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
