package com.king.geomodel.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.king.geomodel.R;


/**
 * 吐司工具类
 *
 * @author xbybaoying
 */
public class ToastUtil {
    private static ToastUtil mToastUtils;

    public static ToastUtil getInstance() {
        if (mToastUtils == null) {
            mToastUtils = new ToastUtil();
        }
        return mToastUtils;
    }

    private ToastUtil() {
    }

    private Toast mToast;

    /**
     * 中间显示吐司
     *
     * @param context 上下文对象
     * @param text    要显示的文本
     */
    public void toastInCenter(Context context, String text) {
        if (mToast != null) {
            mToast.cancel();
        }
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_one, null);
        ((TextView) layout.findViewById(R.id.textview)).setText(text);
        mToast = new Toast(context);
        mToast.setView(layout);
        mToast.setGravity(Gravity.BOTTOM, 0, 200);
        mToast.setDuration(Toast.LENGTH_SHORT);
        mToast.show();
    }

    /**
     * 中间显示吐司
     *
     * @param context  上下文对象
     * @param stringId 要显示的文本ID
     */
    public void toastInCenter(Context context, int stringId) {
        if (mToast != null) {
            mToast.cancel();
        }
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_one, null);
        ((TextView) layout.findViewById(R.id.textview)).setText(context.getResources().getString(stringId));
        mToast = new Toast(context);
        mToast.setView(layout);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setDuration(1000);
        mToast.show();
    }

    /**
     * 文字左侧带图标的toast
     *
     * @param context    上下文对象
     * @param text       内容
     * @param drawableId 分享成功可以用来做成功或失败时的图片id
     */
    public void toastInCenter(Context context, String text, int drawableId) {
        if (mToast != null) {
            mToast.cancel();
        }
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.toast_one, null);
        TextView tv = (TextView) layout.findViewById(R.id.textview);
        tv.setText(text);
        //分享成功
        tv.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(drawableId), null, null, null);
        mToast = new Toast(context);
        mToast.setView(layout);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.setDuration(1000);
        mToast.show();
    }

}
