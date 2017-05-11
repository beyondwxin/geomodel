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
import com.king.geomodel.databinding.ActivityRegisterBinding;
import com.king.geomodel.http.RequestCenter;
import com.king.geomodel.http.ZCResponse;
import com.king.geomodel.model.request.Register;
import com.king.geomodel.utils.CommonValues;
import com.king.geomodel.utils.SharedPreferencesUtil;
import com.king.geomodel.utils.ToastUtil;
import com.king.geomodel.utils.dialogUtils.DialogManager;
import com.king.geomodel.utils.listener.TimeListener;
import com.king.geomodel.viewmodel.RegisterModelView;


public class RegisterActivity extends BaseActivity implements TimeListener {
    private Register register;
    private ActivityRegisterBinding binding;
    private RegisterModelView modelView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        getTopbar().setLeftText(R.string.text_register2);

        register = new Register();
        register.setGetAuthCode(getResources().getString(R.string.set_pwd_tv_getcode));
        binding.setRegister(register);

        modelView = new RegisterModelView(this);
        SMSManager.getInstance().registerTimeListener(this);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_getcode:
                //获取验证码，校验手机号
                Toast.makeText(this, "正在发送", Toast.LENGTH_SHORT).show();
                register.setClick(false);
                modelView.verifyMun(register);
                break;
            case R.id.btn_finish:
                //注册
                modelView.verifyCode(register, modelView, this);
                break;
        }
    }

    @Override
    public boolean onSuccess(ZCResponse response, String method) {
        DialogManager.getInstance().dissMissDialog();
        if (method.equals(RequestCenter.REGISTER_METHOD)) {
            if (TextUtils.equals(response.getCode(), CommonValues.USER_EXIST)) {
                ToastUtil.getInstance().toastInCenter(this, response.getMessage());
                SharedPreferencesUtil.saveValue(this, CommonValues.ISAUTO_LOGIN, true);
                finish();
            } else {
                ToastUtil.getInstance().toastInCenter(this, response.getMessage());
            }
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
            register.setGetAuthCode("已发送(" + lastSecond + "s)");
            register.setClick(false);
        } else {
            register.setGetAuthCode(getResources().getString(R.string.set_pwd_tv_getcode));
            register.setClick(true);
        }
    }

    @Override
    public void onAbleNotify(boolean valuable) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onPageStart(this, "RegisterActivity");
        SMSManager.getInstance().stopTimer();
        register.setGetAuthCode(getResources().getString(R.string.set_pwd_tv_getcode));
        register.setClick(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPageEnd(this, "RegisterActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSManager.getInstance().unRegisterTimeListener(this);
    }
}
