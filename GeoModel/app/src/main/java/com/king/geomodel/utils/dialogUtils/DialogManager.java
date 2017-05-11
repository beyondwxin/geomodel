package com.king.geomodel.utils.dialogUtils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.king.geomodel.R;


/**
 * 描述:对话框管理类， 提供普通消息提示框（一个与两个按钮） 可自定义对话框布局
 */
public class DialogManager {
    private CustomDialog mProgressDialog;


    private DialogManager() {
    }

    private static DialogManager mdialogDialogManager = null;

    public static DialogManager getInstance() {
        if (mdialogDialogManager == null) {
            mdialogDialogManager = new DialogManager();
        }
        return mdialogDialogManager;
    }


    /**
     * /**
     * 描述:显示对话框
     *
     * @param context 上下文
     */
    public void showProgressDialog(Context context, String str) {
        mProgressDialog = createProgressDialog(context,str);
        mProgressDialog.setCancelable(true);
    }


    /**
     * 描述:显示对话框不可被取消
     *
     * @param context 上下文
     */
    public void showProgressDialogNotCancelbale(Context context ,String str) {
        mProgressDialog = createProgressDialog(context,str);
        mProgressDialog.setCancelable(false);
    }

    /**
     * 描述:展示自定义的对话框
     *
     * @param context     上下文对象
     * @param contentView 对话框视图
     */
    public void showCustomDialog(Context context, View contentView) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = new CustomDialog(context, R.style.Theme_Dialog);
        mProgressDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setContentView(contentView);
        showDialog();
    }

    /**
     * 描述:展示自定义的对话框
     *
     * @param context     上下文对象
     * @param contentView 对话框视图
     */
    public void showCustomDialog(Context context, View contentView, boolean cancelable) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        mProgressDialog = new CustomDialog(context, R.style.dialog);
        mProgressDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mProgressDialog.setCancelable(cancelable);
        mProgressDialog.setCanceledOnTouchOutside(cancelable);
        mProgressDialog.setContentView(contentView);
        showDialog();

    }

    /**
     * 描述: 定义弹出框的宽高，弹出对话框
     */
    public void showDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
            WindowManager windowManager = mProgressDialog.getWindow().getWindowManager();
            Display display = windowManager.getDefaultDisplay();
            WindowManager.LayoutParams lp = mProgressDialog.getWindow()
                    .getAttributes();
//            lp.width = display.getWidth(); //设置宽度
//            lp.height = display.getHeight();
//            mProgressDialog.getWindow().setAttributes(lp);
            lp.gravity = Gravity.CENTER;
            mProgressDialog.show();
            mProgressDialog.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 描述:隐藏对话框
     */
    public void dissMissDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载提示框
     */
    public CustomDialog createProgressDialog(final Context con,String str) {
        CustomDialog dlg = new CustomDialog(con, R.style.Theme_Dialog);
        dlg.show();
        dlg.setCancelable(true);
        LayoutInflater factory = LayoutInflater.from(con);
        // 加载progress_dialog为对话框的布局xml
        View view = factory.inflate(R.layout.progress, null);
        TextView text= (TextView) view.findViewById(R.id.tv_text);
        if (TextUtils.isEmpty(str)){
        }else{
            text.setText(str);
        }
        dlg.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface arg0) {
            }
        });
        dlg.getWindow().setContentView(view);
        WindowManager.LayoutParams lp = dlg.getWindow().getAttributes();
//        lp.width = LayoutValue.SCREEN_WIDTH * 2 / 4;
//        lp.height = LayoutValue.SCREEN_WIDTH / 3;
        dlg.getWindow().setAttributes(lp);
        return dlg;
    }
}
