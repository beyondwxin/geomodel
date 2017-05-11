package com.king.geomodel.http;

import android.app.Activity;
import android.os.Looper;
import android.util.Base64;

import com.king.geomodel.R;
import com.king.geomodel.base.BaseApplication;
import com.king.geomodel.model.serializable.SUserInfo;
import com.king.geomodel.tinker.SampleApplicationLike;
import com.king.geomodel.utils.CommonValues;
import com.king.geomodel.utils.LogUtil;
import com.king.geomodel.utils.ToastUtil;
import com.king.geomodel.utils.dialogUtils.DialogManager;
import com.king.greenDAO.bean.Position;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.id.list;

/**
 * Created by Administrator on 2016/4/5 0005.
 */
public class OkHttpUtil {

    private String TAG = "OkHttpUtil";
    private static OkHttpUtil okHttpUtil;
    public final static long CONNECT_TIMEOUT = 60000L;
    public final static long READ_TIMEOUT = 60000L;


    public static OkHttpUtil getInstance() {
        if (okHttpUtil == null) {
            synchronized (OkHttpUtil.class) {
                okHttpUtil = new OkHttpUtil();
            }
        }
        return okHttpUtil;
    }

    /**
     * 后台访问
     * fileMap为空表示上传单纯的参数，反之上传文件(附参数)
     *
     * @param method
     * @param params
     * @param fileMap
     * @param callBack
     */
    public void okHttpPost(final String method, final Map<String, String> params, final Map<String, File> fileMap, final List<Position> mListPositions, final HttpCallBack callBack) {
        if (!SampleApplicationLike.getInstance().isNetworkAvailable((Activity) callBack)) {
            ToastUtil.getInstance().toastInCenter(SampleApplicationLike.getInstance().getApplication(), R.string.toast_netadvice);
            DialogManager.getInstance().dissMissDialog();
            return;
        }
        final String url = Url.BASEURL + method;
        OkHttpClient client = new OkHttpClient();
        Request request = null;
//        Map<String, Object> header = zCRequest.getHeader();
//        Map<String, Object> params = zCRequest.getRequest().getParamsMap();
        // 若只上传参数
        if (fileMap == null) {
            FormBody.Builder formBody = new FormBody.Builder();
//            for (Map.Entry entry : header.entrySet()) {
//                formBody.add(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
//            }
            if (params != null) {
                for (Map.Entry entry : params.entrySet()) {
                    formBody.add(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
                }
            }

            RequestBody body = formBody.build();
            request = new Request.Builder().url(url).post(body).tag(this).build();
        } else {
            MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
            if (params != null) {
                // map 里面是请求中所需要的 key 和 value
                for (Map.Entry entry : params.entrySet()) {
                    requestBody.addFormDataPart(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
                }
            }
            if (mListPositions != null) {
                for (Position position : mListPositions) {
                    requestBody.addFormDataPart(String.valueOf(position.getTime()), String.valueOf(position.getAddress()));
                }
            }
            for (String key : fileMap.keySet()) {
                //userid+字段名称用base64编码+.jpg
//                File file =fileMap.get(key);
//                requestBody.addFormDataPart(key, file.getName(), RequestBody.create(null, file));
                String filename = new String(Base64.encode((SUserInfo.getUserInfoInstance().getId() + key).getBytes(), Base64.DEFAULT)) + ".jpg";
                RequestBody body = RequestBody.create(MediaType.parse("image/*"), fileMap.get(key));
                requestBody.addFormDataPart(key, filename, body);
            }
            RequestBody body = requestBody.build();
            request = new Request.Builder().url(url).post(body).tag(this).build();
        }
        client.newBuilder().readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS).connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("onFailure：", "url:" + url + "--result:" + call.toString());
                Looper.prepare();
                callBack.onFail(e, url);
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String str = response.body().string();
                    LogUtil.e("onResponse：", "url:" + url + "--result:" + str);
                    ZCResponse zCResponse = null;
                    try {
                        zCResponse = MyJSON.parseObject(str, ZCResponse.class);
                        if (httpStatusFilter(zCResponse)) {
                            Looper.prepare();
                            callBack.onSuccess(zCResponse, method);
                            Looper.loop();
                        } else {
                            Looper.prepare();
                            LogUtil.e("serverException", "服务器异常！");
                            callBack.onFail(new Exception(), url);
                            Looper.loop();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        LogUtil.e("dataException:", "url:" + url + "--exception:" + e.getMessage());
                    }
                } else {
                    Looper.prepare();
                    callBack.onFail(new Exception(), url);
                    Looper.loop();
                    LogUtil.e("responseException：", "url:" + url + "--result:" + call.toString());
                }
            }
        });
    }

    /**
     * 状态过滤
     *
     * @param response
     * @return
     */
    public static boolean httpStatusFilter(ZCResponse response) {
        if (response.getStatus().equals(CommonValues.SUCCESS_STATUS)) {
            return true;
        }
        return false;
    }
}
