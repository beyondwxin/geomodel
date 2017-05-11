package com.king.geomodel.viewmodel;

import com.king.geomodel.base.ModelCallBack;

/**
 * Created by king on 2016/9/23.
 * 首页modelView
 */
public class PicModelView implements ModelCallBack {
    private static PicModelView mInstance;

    private PicModelView() {

    }

    public static PicModelView getmInstance() {
        if (mInstance == null) {
            synchronized (PicModelView.class) {

                mInstance = new PicModelView();
            }
        }
        return mInstance;
    }

    /**
     * 处理业务逻辑
     * @param model model实体
     */
    @Override
    public void onResultCallback(Object model) {

    }

    @Override
    public void onMultipleResultCallBack(String method, Object[] model) {

    }

//    /**
//     * 获取所有照片数据
//     *
//     * @return
//     */
//    public List<ListInfo> getData() {
//        List<ListInfo> picInfos = new ArrayList<>();
//        ListInfo picInfo = new ListInfo();
//        picInfo.getTime();
//        picInfo.getTitle();
//        picInfo.getDes();
//        picInfo.getStatus();
//        picInfos.add(picInfo);
//        return picInfos;
//    }

}
