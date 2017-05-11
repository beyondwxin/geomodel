package com.king.geomodel.view;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.baidu.mobstat.StatService;
import com.king.geomodel.R;
import com.king.geomodel.base.BaseActivity;
import com.king.geomodel.http.RequestCenter;
import com.king.geomodel.http.ZCResponse;
import com.king.geomodel.model.serializable.SUserInfo;
import com.king.geomodel.utils.LogUtil;
import com.king.geomodel.utils.MD5Util;
import com.king.geomodel.utils.ToastUtil;
import com.king.geomodel.utils.dialogUtils.DialogManager;


/**
 * Created by king on 2016/12/6.
 * 修改密码
 */
public class ResetPasswordActivity extends BaseActivity implements View.OnClickListener {
    private EditText mOldPassword;
    private EditText mNewPassword;
    private CheckBox mShowPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        getTopbar().setLeftText(R.string.tv_modifyPassword);

        mOldPassword = (EditText) findViewById(R.id.et_oldPwd);
        mNewPassword = (EditText) findViewById(R.id.et_newPwd);
        mShowPassword = (CheckBox) findViewById(R.id.cb_show);

        mShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //如果选中，显示密码
                    mOldPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //否则隐藏密码
                    mOldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.btn_finish:
                if (TextUtils.isEmpty(mOldPassword.getText().toString().trim()) || TextUtils.isEmpty(mNewPassword.getText().toString().trim())) {
                    DialogManager.getInstance().dissMissDialog();
                    ToastUtil.getInstance().toastInCenter(this, "密码为空");
                    return;
                }
                if (!TextUtils.equals(mOldPassword.getText().toString().trim(), SUserInfo.getUserInfoInstance().getPassword())) {
                    LogUtil.e("new:", SUserInfo.getUserInfoInstance().getPassword() + "old:" + mOldPassword.getText().toString().trim());
                    ToastUtil.getInstance().toastInCenter(this, "旧密码输入错误");
                    return;
                }
                if (TextUtils.equals(mOldPassword.getText().toString().trim(), mNewPassword.getText().toString().trim())) {
                    ToastUtil.getInstance().toastInCenter(this, "不能与旧密码一致");
                    return;
                }
                DialogManager.getInstance().showProgressDialog(ResetPasswordActivity.this, "正在修改");
                RequestCenter.resetPassword(SUserInfo.getUserInfoInstance().getPhone(), mNewPassword.getText().toString().trim(), this);
                break;
        }
    }

    @Override
    public boolean onSuccess(ZCResponse response, String method) {
        DialogManager.getInstance().dissMissDialog();
        ToastUtil.getInstance().toastInCenter(this, response.getMessage());
        SUserInfo.getUserInfoInstance().setPassword(mNewPassword.getText().toString().trim());
        finish();
        return super.onSuccess(response, method);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onPageStart(this, "ResetPasswordActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this, "ResetPasswordActivity");
    }
}
