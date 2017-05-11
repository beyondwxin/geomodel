package com.king.geomodel.execute;

import android.util.Log;

import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.ListObjectsRequest;
import com.alibaba.sdk.android.oss.model.ListObjectsResult;
import com.king.geomodel.utils.CommonValues;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouzhuo on 12/3/15.
 */
public class ListObjectsSamples {

    private OSS oss;
    private String academyBucket;
    private int fileCount = 0;

    public ListObjectsSamples(OSS client, String academyBucket) {
        this.oss = client;
        this.academyBucket = academyBucket;
    }

    // 异步罗列Bucket中文件
    public int AyncListObjects() {
        // 创建罗列请求
        ListObjectsRequest listObjects = new ListObjectsRequest(academyBucket);
        // 设置成功、失败回调，发送异步罗列请求
        listObjects.setPrefix(CommonValues.DOWNLOADOBJECT);
        OSSAsyncTask task = oss.asyncListObjects(listObjects, new OSSCompletedCallback<ListObjectsRequest, ListObjectsResult>() {
            @Override
            public void onSuccess(ListObjectsRequest request, ListObjectsResult result) {
                Log.d("AyncListObjects", "Success!");
                fileCount = result.getObjectSummaries().size();
//                for (int i = 0; i < result.getObjectSummaries().size(); i++) {
//                    Log.d("AyncListObjects", "object: " + result.getObjectSummaries().get(i).getKey() + " "
//                            + result.getObjectSummaries().get(i).getETag() + " "
//                            + result.getObjectSummaries().get(i).getLastModified());
//                }
            }

            @Override
            public void onFailure(ListObjectsRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        task.waitUntilFinished();
        return fileCount;
    }

    // 同步罗列指定prefix/delimiter文件
    public void listObjectsWithPrefix() {
        ListObjectsRequest listObjects = new ListObjectsRequest(academyBucket);
        // 设定前缀
        listObjects.setPrefix("picture");
        listObjects.setDelimiter("/");

        try {
            // 发送同步罗列请求，等待结果返回
            ListObjectsResult result = oss.listObjects(listObjects);
            for (int i = 0; i < result.getObjectSummaries().size(); i++) {
                Log.d("listObjectsWithPrefix", "object: " + result.getObjectSummaries().get(i).getKey() + " "
                        + result.getObjectSummaries().get(i).getETag() + " "
                        + result.getObjectSummaries().get(i).getLastModified());
            }

            for (int i = 0; i < result.getCommonPrefixes().size(); i++) {
                Log.d("listObjectsWithPrefix", "prefixes: " + result.getCommonPrefixes().get(i));
            }
        } catch (ClientException clientException) {
            clientException.printStackTrace();
        } catch (ServiceException serviceException) {
            Log.e("ErrorCode", serviceException.getErrorCode());
            Log.e("RequestId", serviceException.getRequestId());
            Log.e("HostId", serviceException.getHostId());
            Log.e("RawMessage", serviceException.getRawMessage());
        }
    }

    // 异步下载指定前缀文件
    public List<String> asyncListObjectsWithPrefix() {
        final List<String> list=new ArrayList<>();
        ListObjectsRequest listObjects = new ListObjectsRequest(academyBucket);
        // 设定前缀
        listObjects.setPrefix(CommonValues.DOWNLOADOBJECT);

        // 设置成功、失败回调，发送异步罗列请求
        OSSAsyncTask task = oss.asyncListObjects(listObjects, new OSSCompletedCallback<ListObjectsRequest, ListObjectsResult>() {
            @Override
            public void onSuccess(ListObjectsRequest request, ListObjectsResult result) {
                Log.d("AyncListObjects", "Success!");
                //这里key:academy/picture/academy_1.jpg
                for (int i =result.getObjectSummaries().size(); i >0; i--) {
                    Log.e("urls","http://academy.oss-cn-beijing.aliyuncs.com"+"/"+CommonValues.DOWNLOADOBJECT+"/academy_"+i+".jpg");
                    list.add("http://academy.oss-cn-beijing.aliyuncs.com"+"/"+CommonValues.DOWNLOADOBJECT+"/academy_"+i+".jpg");
//                    Log.d("AyncListObjects", "object: " + result.getObjectSummaries().get(i).getKey() + " "
//                            + result.getObjectSummaries().get(i).getETag() + " "
//                            + result.getObjectSummaries().get(i).getLastModified());
                }
            }
            @Override
            public void onFailure(ListObjectsRequest request, ClientException clientExcepion, ServiceException serviceException) {
                // 请求异常
                if (clientExcepion != null) {
                    // 本地异常如网络异常等
                    clientExcepion.printStackTrace();
                }
                if (serviceException != null) {
                    // 服务异常
                    Log.e("ErrorCode", serviceException.getErrorCode());
                    Log.e("RequestId", serviceException.getRequestId());
                    Log.e("HostId", serviceException.getHostId());
                    Log.e("RawMessage", serviceException.getRawMessage());
                }
            }
        });
        task.waitUntilFinished();
        return list;
    }
}
