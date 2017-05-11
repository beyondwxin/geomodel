package com.king.geomodel.viewmodel;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.king.geomodel.R;
import com.king.geomodel.base.BaseApplication;
import com.king.geomodel.base.ModelCallBack;
import com.king.geomodel.base.SMSCallback;
import com.king.geomodel.base.SMSManager;
import com.king.geomodel.http.HttpCallBack;
import com.king.geomodel.http.RequestCenter;
import com.king.geomodel.model.request.Register;
import com.king.geomodel.tinker.SampleApplicationLike;
import com.king.geomodel.utils.ToastUtil;
import com.king.geomodel.utils.dialogUtils.DialogManager;

import org.json.JSONObject;


/**
 * Created by king on 2016/9/18.
 * 注册界面ModelView
 */
public class RegisterModelView implements ModelCallBack {
    private Context mContext;

    public RegisterModelView(Context context) {
        mContext = context;
    }


    /**
     * mob-号码检测
     *
     * @param mModel
     */
    public void verifyMun(final Register mModel) {
        SMSManager.getInstance().verifyNum(SampleApplicationLike.getInstance().getApplication(), "86", mModel.getPhone(), new SMSCallback() {
            @Override
            public void success() {
                Toast.makeText(SampleApplicationLike.getInstance().getApplication(), "验证码已发送", Toast.LENGTH_SHORT).show();
                mModel.setClick(false);
            }

            @Override
            public void error(Throwable throwable) {
                try {
                    mModel.setClick(true);
                    int status = 0;
                    JSONObject object = new JSONObject(throwable.getMessage());
                    String des = object.optString("detail");
                    status = object.optInt("status");
                    if (status == 468) {
                        Toast.makeText(SampleApplicationLike.getInstance().getApplication(), mContext.getString(R.string.toast_codeError), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!TextUtils.isEmpty(des)) {
                        Toast.makeText(SampleApplicationLike.getInstance().getApplication(), des, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * mob-验证码检测
     *
     * @param mModel
     */
    public void verifyCode(final Register mModel, final RegisterModelView mViewModel, final HttpCallBack callBack) {
        SMSManager.getInstance().verifyCode(SampleApplicationLike.getInstance().getApplication(), "86", mModel.getPhone(), mModel.getAuthCode(), new SMSCallback() {
            @Override
            public void success() {
                DialogManager.getInstance().showProgressDialog(mContext, "正在注册");
                mViewModel.doRegister(mModel, callBack);
            }

            @Override
            public void error(Throwable throwable) {
                try {
                    int status = 0;
                    JSONObject object = new JSONObject(throwable.getMessage());
                    String des = object.optString("detail");
                    status = object.optInt("status");
                    if (status == 468) {
                        Toast.makeText(SampleApplicationLike.getInstance().getApplication(), mContext.getString(R.string.toast_codeError), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!TextUtils.isEmpty(des)) {
                        Toast.makeText(SampleApplicationLike.getInstance().getApplication(), des, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    /**
     * 用户注册
     *
     * @param register
     * @param callBack
     */
    public void doRegister(Register register, HttpCallBack callBack) {
        if (!TextUtils.equals(register.getPassword(), register.getComfirmPassword())) {
            DialogManager.getInstance().dissMissDialog();
            ToastUtil.getInstance().toastInCenter(mContext, "两次密码输入不一致");
            return;
        }
        RequestCenter.request(register, callBack);

    }

    /**
     * 处理业务逻辑
     *
     * @param model model实体
     */
    @Override
    public void onResultCallback(Object model) {

    }

    @Override
    public void onMultipleResultCallBack(String method, Object[] model) {

    }

}
