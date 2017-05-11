package com.king.geomodel.view;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;

import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.UICheckUpdateCallback;
import com.baidu.mobstat.StatService;
import com.king.geomodel.R;
import com.king.geomodel.base.AppManager;
import com.king.geomodel.base.BaseActivity;
import com.king.geomodel.databinding.ActivityLoginBinding;
import com.king.geomodel.http.RequestCenter;
import com.king.geomodel.http.ZCResponse;
import com.king.geomodel.model.request.UserInfo;
import com.king.geomodel.utils.CommonValues;
import com.king.geomodel.utils.ToastUtil;
import com.king.geomodel.utils.dialogUtils.DialogManager;
import com.king.geomodel.viewmodel.UserInfoViewModel;
import com.king.greenDAO.bean.User;
import com.tencent.bugly.beta.Beta;

import java.util.List;

/**
 * Created by king on 2016/9/18.
 * 登录界面
 */
public class LoginActivity extends BaseActivity {
    private ActivityLoginBinding mDataBinding;
    private UserInfo mUserInfo;
    private UserInfoViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mUserInfo = new UserInfo();
        mDataBinding.setUserInfo(mUserInfo);
        mViewModel = new UserInfoViewModel(this);
        mDataBinding.tvRegister.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        loadPatch();//热修复
        Beta.checkUpgrade(false,false);
    }

    /**
     * 加载热补丁插件
     */
    public void loadPatch() {
        Beta.applyTinkerPatch(getApplicationContext(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk");
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.btn_login:
                DialogManager.getInstance().showProgressDialog(this, "正在登录");
                mViewModel.doLogin(mUserInfo, this);
                break;
            case R.id.tv_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.tv_resetPwd:
                startActivity(new Intent(this, ForgetPasswordActivity.class));
                break;
        }
    }

    @Override
    public boolean onSuccess(ZCResponse response, String method) {
        DialogManager.getInstance().dissMissDialog();
        if (method.equals(RequestCenter.LOGIN_METHOD)) {
            if (TextUtils.equals(response.getCode(), CommonValues.USER_ERROR)) {
            } else {
                //插入用户表
                List<User> list = getUserDao().loadAll();
                User user;
                if (list != null && list.size() > 0) {
                    user = list.get(0);
                    user = new User(user.getId(), mUserInfo.getPhone(), mUserInfo.getPassword());
                    getUserDao().update(user);
                } else {
                    user = new User(null, mUserInfo.getPhone(), mUserInfo.getPassword());
                    getUserDao().insert(user);
                }

                mViewModel.onResultCallback(user);
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }
            ToastUtil.getInstance().toastInCenter(this, response.getMessage());
        }
        return super.onSuccess(response, method);
    }

    @Override
    public boolean onFail(Throwable error, String url) {
        DialogManager.getInstance().dissMissDialog();
        ToastUtil.getInstance().toastInCenter(this, R.string.serverException);
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        StatService.onPageStart(this, "LoginActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this, "LoginActivity");
    }
}
