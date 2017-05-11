package com.king.geomodel.view;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.king.geomodel.R;
import com.king.geomodel.base.BaseActivity;
import com.king.geomodel.base.SMSManager;
import com.king.geomodel.databinding.ActivityForgetpasswordBinding;
import com.king.geomodel.http.RequestCenter;
import com.king.geomodel.http.ZCResponse;
import com.king.geomodel.model.request.ResetPassword;
import com.king.geomodel.utils.CommonValues;
import com.king.geomodel.utils.ToastUtil;
import com.king.geomodel.utils.dialogUtils.DialogManager;
import com.king.geomodel.utils.listener.TimeListener;
import com.king.geomodel.viewmodel.ForgetPasswordModelView;

/**
 * 忘记密码
 */
public class ForgetPasswordActivity extends BaseActivity implements TimeListener {
    private ActivityForgetpasswordBinding mBinding;
    private ResetPassword mForgetPassword;
    private ForgetPasswordModelView mModelView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgetpassword);
        mModelView = new ForgetPasswordModelView(this);

        getTopbar().setLeftText(R.string.text_titleForget);

        mForgetPassword = new ResetPassword();
        mForgetPassword.setGetAuthCode(getResources().getString(R.string.set_pwd_tv_getcode));
        mBinding.setForgetPassword(mForgetPassword);

        SMSManager.getInstance().registerTimeListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_getcode:
                Toast.makeText(this, "正在发送", Toast.LENGTH_SHORT).show();
                mForgetPassword.setClick(false);
                mModelView.verifyMun(mForgetPassword);
                break;
            case R.id.btn_finish:
                mModelView.verifyCode(mForgetPassword, mModelView, this);
                break;
        }
    }

    @Override
    public boolean onSuccess(ZCResponse response, String method) {
        DialogManager.getInstance().dissMissDialog();
        if (method.equals(RequestCenter.FORGET_METHOD)) {
            if (TextUtils.equals(response.getCode(), CommonValues.USER_ERROR)) {

            } else {
                finish();
            }
            ToastUtil.getInstance().toastInCenter(this, response.getMessage());

        }
        return super.onSuccess(response, method);
    }

    @Override
    public boolean onFail(Throwable error, String method) {
        DialogManager.getInstance().dissMissDialog();
        ToastUtil.getInstance().toastInCenter(this, R.string.connecction_timeout);
        return false;
    }

    @Override
    public void onLastTimeNotify(int lastSecond) {
        if (lastSecond > 0) {
            mForgetPassword.setGetAuthCode("已发送(" + lastSecond + "s)");
            mForgetPassword.setClick(false);
        } else {
            mForgetPassword.setGetAuthCode(getResources().getString(R.string.set_pwd_tv_getcode));
            mForgetPassword.setClick(false);
        }
    }

    @Override
    public void onAbleNotify(boolean valuable) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onPageStart(this, "ForgetPasswordActivity");
        SMSManager.getInstance().stopTimer();
        mForgetPassword.setGetAuthCode(getResources().getString(R.string.set_pwd_tv_getcode));
        mForgetPassword.setClick(true);
    }


    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this, "ForgetPasswordActivity");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSManager.getInstance().unRegisterTimeListener(this);
    }
}
